package com.playerdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.common.PlayerEventListener;
import com.playerdata.readonly.FriendMgrIF;
import com.playerdata.readonly.PlayerIF;
import com.rw.service.friend.FriendHandler;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.dao.friend.FriendUtils;
import com.rwbase.dao.friend.TableFriend;
import com.rwbase.dao.friend.TableFriendDAO;
import com.rwbase.dao.friend.vo.FriendGiveState;
import com.rwbase.dao.friend.vo.FriendItem;
import com.rwbase.dao.friend.vo.FriendResultVo;
import com.rwbase.dao.hotPoint.EHotPointType;
import com.rwbase.dao.power.RoleUpgradeCfgDAO;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.FriendServiceProtos;
import com.rwproto.FriendServiceProtos.EFriendResultType;
import com.rwproto.FriendServiceProtos.FriendInfo;

public class FriendMgr implements FriendMgrIF, PlayerEventListener {
	private final int FRIEND_LIMIT = 100;// 好友上限
	private final int BLACK_LIMIT = 30;// 黑名单上限
	private final int A_POWER_COUNT = 1;// 领取一次体力获得体力数

	// private TableFriend tableFriend;
	private String userId;
	private TableFriendDAO friendDAO = TableFriendDAO.getInstance();

	protected Player m_pPlayer = null;

	public TableFriend getTableFriend() {
		return friendDAO.get(userId);
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		if (!player.isRobot()) {
			TableFriend tableFriend = new TableFriend();
			tableFriend.setUserId(player.getUserId());
			friendDAO.update(tableFriend);
		}
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		// TODO Auto-generated method stub

	}

	// 初始化
	public void init(Player playerP) {
		m_pPlayer = playerP;
		this.userId = playerP.getUserId();
		// tableFriend = friendDAO.get(playerP.getUserId());
		// if (tableFriend == null) {
		// tableFriend = new TableFriend();
		// tableFriend.setUserId(playerP.getUserId());
		// }
	}

	/** 每日5点重置 */
	public void onNewDay5Clock() {
		if (getTableFriend().resetGiveState()) {
			friendDAO.update(userId);
		}
	}

	/** 获取好友列表 */
	public List<FriendInfo> getFriendList() {
		return friendItemToInfoList(getTableFriend().getFriendList());
	}

	/** 获取请求列表 */
	public List<FriendInfo> getRequestList() {
		return friendItemToInfoList(getTableFriend().getRequestList());
	}

	public boolean hasRequest() {
		return getTableFriend().getRequestList().size() > 0;
	}

	/** 获取黑名单列表 */
	public List<FriendInfo> getBlackList() {
		return friendItemToInfoList(getTableFriend().getBlackList());
	}

	/** 添加好友 */
	private FriendResultVo addFriend(String otherUserId) {
		FriendResultVo resultVo = new FriendResultVo();
		List<FriendItem> list = new ArrayList<FriendItem>();
		TableFriend tableFriend = getTableFriend();
		if (isSelfUser(otherUserId)) {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "该玩家是自己";
		} else if (tableFriend.getFriendList().size() >= FRIEND_LIMIT) {// 自己好友达到上限
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "好友数量已达上限";
		} else if (tableFriend.getFriendList().containsKey(otherUserId)) {// 已经是好友了
			resultVo.resultType = EFriendResultType.FAIL_2;// 需推送请求列表
			resultVo.resultMsg = "对方已经是自己的好友";
		} else if (!isOtherFriendLimit(otherUserId, this.userId)) {// 对方好友达到上限
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "对方好友数量已达上限";
		} else {
			FriendItem friendItem = FriendItem.newInstance(otherUserId);
			tableFriend.getFriendList().put(otherUserId, friendItem);

			FriendGiveState giveState = tableFriend.getFriendGiveList().get(otherUserId);
			if (giveState == null) {
				giveState = new FriendGiveState();
				giveState.setUserId(otherUserId);
				tableFriend.getFriendGiveList().put(otherUserId, giveState);
			}

			if (tableFriend.getBlackList().containsKey(otherUserId)) {// 从黑名单中移除
				tableFriend.getBlackList().remove(otherUserId);
			}

			list.add(friendItem);
			resultVo.updateList = friendItemToInfoList(list);
			resultVo.resultType = EFriendResultType.SUCCESS;
			resultVo.resultMsg = "添加成功";

			// 添加任务
			m_pPlayer.getTaskMgr().AddTaskTimes(eTaskFinishDef.Add_Friend);
			Player otherUser = PlayerMgr.getInstance().find(otherUserId);
			if (otherUser != null) {
				otherUser.getTaskMgr().AddTaskTimes(eTaskFinishDef.Add_Friend);
			}
		}
		return resultVo;
	}

	/** 删除好友 */
	public FriendResultVo removeFriend(String otherUserId) {
		FriendResultVo resultVo = new FriendResultVo();
		TableFriend tableFriend = getTableFriend();
		if (tableFriend.getFriendList().containsKey(otherUserId)) {
			tableFriend.getFriendList().remove(otherUserId);
			TableFriend otherFriend = getOtherTableFriend(otherUserId);
			if (otherFriend != null && otherFriend.getFriendList().containsKey(m_pPlayer.getUserId())) {
				FriendHandler.getInstance().pushRemoveFriend(PlayerMgr.getInstance().find(otherUserId), m_pPlayer.getUserId());
				otherFriend.getFriendList().remove(m_pPlayer.getUserId());
				friendDAO.update(otherFriend);
			}
			resultVo.resultType = EFriendResultType.SUCCESS;
			resultVo.resultMsg = "删除好友成功";
			FriendUtils.checkHasNotReceive(m_pPlayer, getTableFriend());
		} else {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "该玩家不是您的好友";
		}
		return resultVo;
	}

	/** 请求添加好友 */
	public FriendResultVo requestAddFriend(String otherUserId) {
		FriendResultVo resultVo = new FriendResultVo();
		TableFriend tableFriend = getTableFriend();
		if (isSelfUser(otherUserId)) {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "该玩家是自己";
		} else if (tableFriend.getFriendList().containsKey(otherUserId)) {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "对方已经是你的好友";
		} else {
			TableFriend otherTable = getOtherTableFriend(otherUserId);
			if (otherTable.getBlackList().containsKey(m_pPlayer.getUserId())) {
				// 如果在对方的黑名单列表中，不做操作
			} else {
				FriendItem friendItem = FriendItem.newInstance(m_pPlayer.getUserId());
				if (!otherTable.getRequestList().containsKey(friendItem.getUserId())) {
					otherTable.getRequestList().put(friendItem.getUserId(), friendItem);
					FriendHandler.getInstance().pushRequestAddFriend(PlayerMgr.getInstance().find(otherUserId), friendItem);
					tableFriend.removeFromBlackList(otherUserId);
				}
				friendDAO.update(otherTable);
				HotPointMgr.changeHotPointState(otherUserId, EHotPointType.Friend_Request, true);
			}
			resultVo.resultType = EFriendResultType.SUCCESS;
			resultVo.resultMsg = "已向对方发送添加好友请求";
		}
		return resultVo;
	}

	/** 同意添加好友 */
	public FriendResultVo consentAddFriend(String otherUserId) {
		FriendResultVo resultVo = addFriend(otherUserId);
		TableFriend tableFriend = getTableFriend();
		if (tableFriend.getRequestList().containsKey(otherUserId)) {// 从请求列表中移除
			tableFriend.getRequestList().remove(otherUserId);
		}
		return resultVo;
	}

	/** 拒绝添加好友 */
	public FriendResultVo refusedAddFriend(String otherUserId) {
		FriendResultVo resultVo = new FriendResultVo();
		TableFriend tableFriend = getTableFriend();
		if (tableFriend.getRequestList().containsKey(otherUserId)) {
			tableFriend.getRequestList().remove(otherUserId);
			resultVo.resultType = EFriendResultType.SUCCESS;
			resultVo.resultMsg = "";
		} else {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "该玩家不在您的请求列表中";
		}
		return resultVo;
	}

	/** 同意所有请求 */
	public FriendResultVo consentAddFriendAll() {
		FriendResultVo resultVo = new FriendResultVo();
		List<FriendItem> list = new ArrayList<FriendItem>();
		TableFriend tableFriend = getTableFriend();
		Iterator<FriendItem> it = tableFriend.getRequestList().values().iterator();
		int count = 0;
		while (it.hasNext()) {
			FriendItem friendItem = it.next();
			resultVo = addFriend(friendItem.getUserId());
			if (resultVo.resultType == EFriendResultType.SUCCESS) {
				count++;
				list.add(friendItem);
				it.remove();
			}
		}
		if (count > 0) {
			resultVo.updateList = friendItemToInfoList(list);
			resultVo.resultType = EFriendResultType.SUCCESS_MSG;
			resultVo.resultMsg = "成功添加 " + count + " 位好友";
		} else if (tableFriend.getRequestList().size() > 0) {
			// 使用addFriend好友返回结果
		} else {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "没有要添加的好友";
		}
		return resultVo;
	}

	/** 拒绝所有请求 */
	public FriendResultVo refusedAddFriendAll() {
		FriendResultVo resultVo = new FriendResultVo();
		TableFriend tableFriend = getTableFriend();
		if (tableFriend.getRequestList().size() > 0) {
			tableFriend.setRequestList(new ConcurrentHashMap<String, FriendItem>());
			resultVo.resultMsg = "";
			resultVo.resultType = EFriendResultType.SUCCESS;
		} else {
			resultVo.resultType = EFriendResultType.SUCCESS_MSG;
			resultVo.resultMsg = "请求列表为空";
		}
		return resultVo;
	}

	/** 加入黑名单 */
	public FriendResultVo addBlack(String otherUserId) {
		FriendResultVo resultVo = new FriendResultVo();
		List<FriendItem> list = new ArrayList<FriendItem>();
		TableFriend tableFriend = getTableFriend();
		if (isSelfUser(otherUserId)) {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "该玩家是自己";
		} else if (tableFriend.getBlackList().size() >= BLACK_LIMIT) {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "您的黑名单列表已满";
		} else {
			removeFriend(otherUserId);
			refusedAddFriend(otherUserId);
			if (!tableFriend.getBlackList().containsKey(otherUserId)) {
				FriendItem friendItem = FriendItem.newInstance(otherUserId);
				tableFriend.getBlackList().put(otherUserId, friendItem);
				list.add(friendItem);
			}
			resultVo.updateList = friendItemToInfoList(list);
			resultVo.resultType = EFriendResultType.SUCCESS;
			resultVo.resultMsg = "拉黑成功，不会再收到该玩家信息";
		}
		return resultVo;
	}

	/** 移出黑名单 */
	public FriendResultVo removeBlack(String otherUserId) {
		FriendResultVo resultVo = new FriendResultVo();
		TableFriend tableFriend = getTableFriend();
		if (tableFriend.getBlackList().containsKey(otherUserId)) {
			tableFriend.getBlackList().remove(otherUserId);
			resultVo.resultType = EFriendResultType.SUCCESS;
			resultVo.resultMsg = "移出黑名单成功，将会收到该玩家信息";
		} else {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "该玩家不在您的黑名单列表中";
		}
		return resultVo;
	}

	/** 搜索好友 */
	public FriendResultVo searchFriend(String searchKey) {
		FriendResultVo resultVo = new FriendResultVo();
		if (searchKey.equals(m_pPlayer.getUserName())) {// 是玩家自己
			resultVo.resultMsg = "您搜索的玩家是自己";
			resultVo.resultType = EFriendResultType.FAIL;
			return resultVo;
		}

		String userId = PlayerMgr.getInstance().getUserIdByName(searchKey);
		if (userId == null) {
			resultVo.resultMsg = "没有找到该玩家";
			resultVo.resultType = EFriendResultType.FAIL;
			return resultVo;
		}

		FriendItem friendItem = FriendItem.newInstance(userId);
		resultVo.updateList = friendItemToInfoList(friendItem);
		resultVo.resultMsg = "";
		resultVo.resultType = EFriendResultType.SUCCESS;
		return resultVo;
	}

	/** 未搜索到好友，为其查找相近好友，最多10人。 */
	public List<FriendInfo> searchNearFriend() {
		List<FriendInfo> resultList = new ArrayList<FriendServiceProtos.FriendInfo>();
		Map<String, Player> m_PlayerList = PlayerMgr.getInstance().getAllPlayer();
		Iterator<Player> it = m_PlayerList.values().iterator();
		TableFriend tableFriend = getTableFriend();
		while (resultList.size() < 10 && it.hasNext()) {
			Player player = it.next();
			if (Math.abs(player.getLevel() - m_pPlayer.getLevel()) <= 5 && // 等级符合
					!tableFriend.getFriendList().containsKey(player.getUserId()) && // 不是自己好友
					!tableFriend.getRequestList().containsKey(player.getUserId()) && // 未在请求列表
					!tableFriend.getBlackList().containsKey(player.getUserId()) && // 未在黑名单列表
					!player.getUserId().equals(m_pPlayer.getUserId())) {// 不是自己

				FriendItem item = FriendItem.newInstance(player.getUserId());
				resultList.add(friendItemToInfo(item));
			}
		}
		return resultList;
	}

	/** 赠送体力 */
	public FriendResultVo givePower(String otherUserId) {
		FriendResultVo resultVo = new FriendResultVo();
		List<FriendItem> list = new ArrayList<FriendItem>();
		TableFriend tableFriend = getTableFriend();
		if (isSelfUser(otherUserId)) {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "该玩家是自己";
		} else if (tableFriend.getFriendList().containsKey(otherUserId)) {
			if (tableFriend.getFriendGiveList().get(otherUserId).isGiveState()) {// 可赠送
				tableFriend.getFriendGiveList().get(otherUserId).setGiveState(false);
				TableFriend otherFriend = getOtherTableFriend(otherUserId);
				if (otherFriend != null) {
					if (otherFriend.getFriendGiveList().containsKey(m_pPlayer.getUserId())) {
						otherFriend.getFriendGiveList().get(m_pPlayer.getUserId()).setReceiveState(true);
						friendDAO.update(otherFriend);
						HotPointMgr.changeHotPointState(otherUserId, EHotPointType.Friend_Give, true);
					}
				}
				list.add(tableFriend.getFriendList().get(otherUserId));
				resultVo.resultType = EFriendResultType.SUCCESS;
				resultVo.updateList = friendItemToInfoList(list);
				resultVo.resultMsg = "赠送成功";
			} else {
				resultVo.resultType = EFriendResultType.FAIL;
				resultVo.resultMsg = "已赠送过该玩家体力";
			}
		} else {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "该玩家不是您的好友";
		}
		return resultVo;
	}

	/** 领取体力 */
	public FriendResultVo receivePower(String otherUserId) {
		FriendResultVo resultVo = new FriendResultVo();

		RoleUpgradeCfg cfg = RoleUpgradeCfgDAO.getInstance().getCfg(m_pPlayer.getLevel());
		if (m_pPlayer.getUserGameDataMgr().getPower() >= cfg.getMostPower()) {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "体力已达上限，无法领取";
			return resultVo;
		}

		List<FriendItem> list = new ArrayList<FriendItem>();
		TableFriend tableFriend = getTableFriend();
		if (tableFriend.getFriendList().containsKey(otherUserId)) {
			if (tableFriend.getFriendVo().isCanReceive(m_pPlayer.getLevel())) {// 还未达到体力上限
				if (tableFriend.getFriendGiveList().get(otherUserId).isReceiveState()) {// 可领取
					tableFriend.getFriendGiveList().get(otherUserId).setReceiveState(false);
					resultVo.resultType = EFriendResultType.SUCCESS;
					list.add(tableFriend.getFriendList().get(otherUserId));
					tableFriend.getFriendVo().addOnePower(A_POWER_COUNT);
					resultVo.powerCount = A_POWER_COUNT;
					resultVo.updateList = friendItemToInfoList(list);
					resultVo.resultMsg = "成功领取 " + A_POWER_COUNT + "体力，今日还可领取 " + tableFriend.getFriendVo().getSurplusCount(m_pPlayer.getLevel()) + " 次";
				} else {
					resultVo.resultType = EFriendResultType.FAIL;
					resultVo.resultMsg = "没有可领取的体力";
				}
			} else {
				resultVo.resultType = EFriendResultType.FAIL;
				resultVo.resultMsg = "今日可领取体力已达上限";
			}
		} else {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "该玩家不是您的好友";
		}
		FriendUtils.checkHasNotReceive(m_pPlayer, tableFriend);
		return resultVo;
	}

	/** 一健赠送体力 */
	public FriendResultVo givePowerAll() {
		FriendResultVo resultVo = new FriendResultVo();
		List<FriendItem> list = new ArrayList<FriendItem>();
		TableFriend tableFriend = getTableFriend();
		Iterator<FriendItem> it = tableFriend.getFriendList().values().iterator();
		int count = 0;
		while (it.hasNext()) {
			FriendItem friendItem = it.next();
			FriendGiveState giveState = tableFriend.getFriendGiveList().get(friendItem.getUserId());
			if (giveState.isGiveState()) {// 可赠送
				TableFriend otherFriend = getOtherTableFriend(giveState.getUserId());
				if (otherFriend != null) {
					otherFriend.getFriendGiveList().get(m_pPlayer.getUserId()).setReceiveState(true);
					friendDAO.update(otherFriend);
					HotPointMgr.changeHotPointState(giveState.getUserId(), EHotPointType.Friend_Give, true);
				}
				giveState.setGiveState(false);
				count++;
				list.add(friendItem);
			}
		}
		if (count <= 0) {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "没有要赠送的好友";
		} else {
			resultVo.updateList = friendItemToInfoList(list);// 更新列表
			resultVo.resultType = EFriendResultType.SUCCESS;
			resultVo.resultMsg = "已为所有好友赠送体力";
		}
		return resultVo;
	}

	public boolean hasReceivePower() {
		TableFriend tableFriend = getTableFriend();
		if (!tableFriend.getFriendVo().isCanReceive(m_pPlayer.getLevel())) {
			return false;
		}
		Iterator<FriendItem> it = tableFriend.getFriendList().values().iterator();
		while (it.hasNext()) {
			FriendItem friendItem = it.next();
			FriendGiveState giveState = tableFriend.getFriendGiveList().get(friendItem.getUserId());
			if (giveState.isReceiveState()) {
				return true;
			}
		}
		return false;
	}

	/** 一键领取体力 */
	public FriendResultVo receivePowerAll() {
		FriendResultVo resultVo = new FriendResultVo();
		RoleUpgradeCfg cfg = RoleUpgradeCfgDAO.getInstance().getCfg(m_pPlayer.getLevel());
		List<FriendItem> list = new ArrayList<FriendItem>();
		TableFriend tableFriend = getTableFriend();
		Iterator<FriendItem> it = tableFriend.getFriendList().values().iterator();
		int count = 0;
		while (it.hasNext()) {
			FriendItem friendItem = it.next();
			FriendGiveState giveState = tableFriend.getFriendGiveList().get(friendItem.getUserId());
			if (tableFriend.getFriendVo().isCanReceive(m_pPlayer.getLevel())) {// 可以再领取
				if (giveState.isReceiveState()) {// 可领取
					if (m_pPlayer.getUserGameDataMgr().getPower() >= cfg.getMostPower()) {
						resultVo.resultType = EFriendResultType.FAIL;
						resultVo.resultMsg = "体力已达上限，无法领取";
						break;
					} else {
						giveState.setReceiveState(false);
						count++;
						list.add(friendItem);
					}
				}
			}
		}
		resultVo.powerCount = count * A_POWER_COUNT;

		if (count == 0 && !tableFriend.getFriendVo().isCanReceive(m_pPlayer.getLevel())) {
			resultVo.resultMsg = "今日可领取体力已达上限";
			resultVo.resultType = EFriendResultType.FAIL;
		} else if (count == 0) {
			resultVo.resultMsg = "没有可领取体力";
			resultVo.resultType = EFriendResultType.FAIL;
		} else if (count > 0 && !tableFriend.getFriendVo().isCanReceive(m_pPlayer.getLevel())) {// 成功了一些，然后体力满了
			resultVo.resultMsg = "成功领取 " + resultVo.powerCount + " 体力，今日可领取体力已达上限";
			resultVo.resultType = EFriendResultType.SUCCESS_MSG;
		} else if (count > 0) {
			resultVo.resultMsg = "成功领取 " + resultVo.powerCount + " 体力，今日还可领取 " + tableFriend.getFriendVo().getSurplusCount(m_pPlayer.getLevel()) + " 次";
			resultVo.resultType = EFriendResultType.SUCCESS_MSG;
		}

		resultVo.updateList = friendItemToInfoList(list);
		tableFriend.getFriendVo().addOnePower(resultVo.powerCount);

		FriendUtils.checkHasNotReceive(m_pPlayer, tableFriend);
		return resultVo;
	}

	/** 判断并添加到对方好友列表 */
	private boolean isOtherFriendLimit(String selfUserId, String otherUserId) {
		boolean result = false;
		TableFriend otherFriend = getOtherTableFriend(selfUserId);
		if (otherFriend.getFriendList().size() < FRIEND_LIMIT) {
			result = true;
		}
		if (result) {
			FriendItem friendItem = FriendItem.newInstance(otherUserId);
			otherFriend.getFriendList().put(otherUserId, friendItem);
			FriendHandler.getInstance().pushConsentAddFriend(PlayerMgr.getInstance().find(selfUserId), friendItem);
			FriendGiveState giveState = otherFriend.getFriendGiveList().get(otherUserId);
			if (giveState == null) {
				giveState = new FriendGiveState();
				giveState.setUserId(otherUserId);
				otherFriend.getFriendGiveList().put(otherUserId, giveState);
			}

			// if(otherFriend.getRequestList().containsKey(otherUserId)){//从请求列表中移除
			// otherFriend.getRequestList().remove(otherUserId);
			// }
			otherFriend.removeFromRequest(otherUserId);
			// if(otherFriend.getBlackList().containsKey(otherUserId)){//从黑名单中移除
			// otherFriend.getBlackList().remove(otherUserId);
			// }
			otherFriend.removeFromBlackList(otherUserId);

			friendDAO.update(otherFriend);
		}
		return result;
	}

	public List<FriendInfo> friendItemToInfoList(FriendItem friendItem) {
		// Map<String, FriendItem> map = new HashMap<String, FriendItem>();
		// map.put(friendItem.getUserId(), friendItem);
		// return friendItemToInfoList(map);
		List<FriendInfo> list = new ArrayList<FriendInfo>();
		list.add(friendItemToInfo(friendItem));
		return list;
	}

	private List<FriendInfo> friendItemToInfoList(List<FriendItem> list) {
		if (list == null || list.isEmpty()) {
			return new ArrayList<FriendInfo>();
		}

		int size = list.size();
		Map<String, FriendItem> map = new HashMap<String, FriendItem>(size);
		for (int i = 0; i < size; i++) {
			FriendItem value = list.get(i);
			map.put(value.getUserId(), value);
		}

		return friendItemToInfoList(map);
	}

	private List<FriendInfo> friendItemToInfoList(Map<String, FriendItem> map) {
		// 临时更改好友列表
		// initFriendData(map);
		if (map == null || map.isEmpty()) {
			return new ArrayList<FriendInfo>();
		}

		List<FriendInfo> list = new ArrayList<FriendInfo>(map.size());
		Iterator<FriendItem> it = map.values().iterator();
		while (it.hasNext()) {
			FriendItem item = it.next();
			list.add(friendItemToInfo(item));
		}
		return list;
	}

	public FriendInfo friendItemToInfo(FriendItem item) {
		FriendInfo.Builder friendInfo = FriendInfo.newBuilder();
		friendInfo.setUserId(item.getUserId());
		friendInfo.setUserName(item.getUserName());
		friendInfo.setHeadImage(item.getUserHead());
		friendInfo.setCareer(item.getCareer());
		friendInfo.setUnionName(item.getUnionName());
		friendInfo.setLastLoginTime(item.getLastLoginTime());
		friendInfo.setLastLoginTip(FriendUtils.getLastLoginTip(item.getLastLoginTime()));
		friendInfo.setLevel(item.getLevel());
		TableFriend tableFriend = getTableFriend();
		FriendGiveState giveState = tableFriend.getFriendGiveList().get(item.getUserId());
		if (giveState != null) {
			friendInfo.setGiveState(giveState.isGiveState());
			friendInfo.setReceiveState(giveState.isReceiveState());
		}
		return friendInfo.build();
	}

	/** 获取其它玩家的数据列表 */
	private TableFriend getOtherTableFriend(String otherUserId) {
		TableFriend otherTable;
		PlayerIF player = PlayerMgr.getInstance().getReadOnlyPlayer(otherUserId);
		if (player == null) {
			otherTable = friendDAO.get(otherUserId);
			if (otherTable == null) {
				otherTable = new TableFriend();
				otherTable.setUserId(otherUserId);
			}
		} else {
			otherTable = player.getFriendMgr().getTableFriend();
		}
		return otherTable;
	}

	/** 是否是自己 */
	private boolean isSelfUser(String userId) {
		return userId.equals(m_pPlayer.getUserId());
	}

	/** 玩家数据改变 */
	public void onPlayerChange(Player player) {
		TableFriend tableFriend = getTableFriend();
		Iterator<FriendItem> it = tableFriend.getFriendList().values().iterator();
		while (it.hasNext()) {
			FriendItem item = it.next();
			PlayerIF otherPlayer = PlayerMgr.getInstance().getReadOnlyPlayer(item.getUserId());
			if (otherPlayer != null) {
				GameWorldFactory.getGameWorld().asyncExecute(item.getUserId(), new PlayerTask() {

					@Override
					public void run(Player player) {

						player.getFriendMgr().pushPlayerChange(player);
					}
				});

			}
		}
	}

	/** 角色数据改变(由其它玩家数据改变时调用) */
	public void pushPlayerChange(Player player) {
		TableFriend tableFriend = getTableFriend();
		if (tableFriend.getFriendList().containsKey(player.getUserId())) {
			FriendItem friendItem = tableFriend.getFriendList().get(player.getUserId());
			friendItem.setUserId(player.getUserId());
			friendItem.setUserName(player.getUserName());
			friendItem.setLastLoginTime(player.getUserGameDataMgr().getLastLoginTime());
			friendItem.setLevel(player.getLevel());
			friendItem.setUserHead(player.getHeadImage());
			friendItem.setCareer(player.getCareer());
			// friendItem.setUnionName(player.getGuildUserMgr().getGuildName());
			// TODO 帮派获取名字后再提供
			friendItem.setUnionName("");
		}
	}

	public boolean save() {
		friendDAO.update(userId);
		return true;
	}

}
