package com.playerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.RankType;
import com.bm.rank.fightingAll.FightingComparable;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.common.PlayerEventListener;
import com.playerdata.readonly.FriendMgrIF;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.service.friend.FriendGetOperation;
import com.rw.service.friend.FriendHandler;
import com.rw.service.group.helper.GroupHelper;
import com.rw.service.group.helper.GroupMemberHelper;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.friend.FriendUtils;
import com.rwbase.dao.friend.TableFriend;
import com.rwbase.dao.friend.TableFriendDAO;
import com.rwbase.dao.friend.vo.FriendGiveState;
import com.rwbase.dao.friend.vo.FriendItem;
import com.rwbase.dao.friend.vo.FriendResultVo;
import com.rwbase.dao.openLevelTiggerService.pojo.OpenLevelTiggerServiceSubItem;
import com.rwbase.dao.power.RoleUpgradeCfgDAO;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.setting.HeadBoxCfgDAO;
import com.rwbase.dao.setting.pojo.HeadBoxType;
import com.rwproto.FriendServiceProtos.EFriendResultType;
import com.rwproto.FriendServiceProtos.FriendInfo;

public class FriendMgr implements FriendMgrIF, PlayerEventListener {
	private final int FRIEND_LIMIT = 100;// 好友上限
	private final int BLACK_LIMIT = 30;// 黑名单上限
	private final int A_POWER_COUNT = 1;// 领取一次体力获得体力数
	private String userId;
	private TableFriendDAO friendDAO = TableFriendDAO.getInstance();

	protected Player m_pPlayer = null;

	public TableFriend getTableFriend() {
		return friendDAO.get(userId);
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		// if (!player.isRobot()) {
		// TableFriend tableFriend = new TableFriend();
		// tableFriend.setUserId(player.getUserId());
		// friendDAO.update(tableFriend);
		// }
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		try {
			long currentTime = System.currentTimeMillis();
			TableFriend tableFriend = getTableFriend();
			notifyLoginTime(tableFriend, FriendGetOperation.FRIEND, userId, currentTime);
			notifyLoginTime(tableFriend, FriendGetOperation.BLACKLIST, userId, currentTime);
		} catch (Exception e) {
			GameLog.error("FriendMgr", "#updateLoginTime()", "登录更新好友信息异常", e);
		}
	}

	// 初始化
	public void init(Player playerP) {
		m_pPlayer = playerP;
		this.userId = playerP.getUserId();
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

	public int getFriendCount() {
		return getTableFriend().getFriendList().size();
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

		TableFriend tableFriend = getTableFriend();
		Player other = PlayerMgr.getInstance().find(otherUserId);
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
		} else if (other != null && other.isRobot()) {
			resultVo = addRobotOrPlayerToFriend(otherUserId, tableFriend);
			doOpenLevelTiggerService(other, otherUserId, tableFriend);
		} else {
			resultVo = addRobotOrPlayerToFriend(otherUserId, tableFriend);
		}
		return resultVo;
	}

	/**
	 * 
	 * @param tableFriend 执行添加机器人为好友的一些引导性操作
	 */
	private void doOpenLevelTiggerService(Player other, String otherUserId, TableFriend tableFriend) {
		List<OpenLevelTiggerServiceSubItem> subItemlist = tableFriend.getOpenLevelTiggerServiceItem().getSubItemList();
		for (OpenLevelTiggerServiceSubItem subItem : subItemlist) {
			if (!StringUtils.equals(subItem.getUserId(), otherUserId)) {
				continue;
			}
			boolean isOver = subItem.isOver();
			subItem.setOver(true);
			if (isOver || !subItem.isGivePower()) {
				continue;
			}
			other.getFriendMgr().givePower(m_pPlayer.getUserId());
		}
		save();
	}

	private FriendResultVo addRobotOrPlayerToFriend(String otherUserId, TableFriend tableFriend) {
		FriendResultVo resultVo = new FriendResultVo();
		List<FriendItem> list = new ArrayList<FriendItem>();
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
		String userId = m_pPlayer.getUserId();
		TableFriend tableFriend = getTableFriend();
		if (isSelfUser(otherUserId)) {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "该玩家是自己";
		} else if (tableFriend.getFriendList().containsKey(otherUserId)) {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "对方已经是你的好友";
		} else if (otherUserId.length() > 20) {
			resultVo = requestAddOneRobotToFriend(otherUserId, userId);
		} else {
			resultVo = requestToAddFriend(otherUserId, userId, tableFriend);
		}
		return resultVo;
	}

	/** 请求添加好友 */
	private FriendResultVo requestToAddFriend(String otherUserId, String userId2, TableFriend tableFriend) {
		FriendResultVo resultVo = new FriendResultVo();
		TableFriend otherTable = getOtherTableFriend(otherUserId);
		if (otherTable.getBlackList().containsKey(userId)) {
			// 如果在对方的黑名单列表中，不做操作
		} else {
			FriendItem friendItem = FriendItem.newInstance(userId);
			if (!otherTable.getRequestList().containsKey(friendItem.getUserId())) {
				otherTable.getRequestList().put(friendItem.getUserId(), friendItem);
				FriendHandler.getInstance().pushRequestAddFriend(PlayerMgr.getInstance().find(otherUserId), friendItem);
				tableFriend.removeFromBlackList(otherUserId);
			}
			friendDAO.update(otherTable);
		}
		resultVo.resultType = EFriendResultType.SUCCESS;
		resultVo.resultMsg = "已向对方发送添加好友请求";
		// 增加红点检查
		PlayerMgr.getInstance().setRedPointForHeartBeat(otherUserId);
		return resultVo;
	}

	/**
	 * 
	 * @param otherUserId
	 * @param userId2
	 * @return 加的是机器人好友,跳过申请过程直接加上；
	 */
	private FriendResultVo requestAddOneRobotToFriend(String otherUserId, String userId2) {
		FriendResultVo resultVo = new FriendResultVo();
		TableFriend otherTable = getOtherTableFriend(otherUserId);
		TableFriend friendTable = getTableFriend();
		FriendItem friendItem = FriendItem.newInstance(userId);
		FriendItem robotFriendItem = FriendItem.newInstance(otherUserId);
		Player otherPlayer = PlayerMgr.getInstance().find(otherUserId);
		if (!otherTable.getFriendList().containsKey(friendItem.getUserId())) {
			otherPlayer.getFriendMgr().consentAddFriend(userId);
		}
		if (friendTable.getReCommandfriendList().isEmpty()) {// 加的是第一个机器人，机器人会立刻赠送体力
			otherPlayer.getFriendMgr().givePower(userId);
		}
		if (!friendTable.getReCommandfriendList().containsKey(otherUserId)) {
			friendTable.getReCommandfriendList().put(otherUserId, robotFriendItem);
		}
		friendDAO.update(otherTable);
		resultVo.resultType = EFriendResultType.SUCCESS;
		resultVo.resultMsg = "已向对方发送添加好友请求";
		PlayerMgr.getInstance().setRedPointForHeartBeat(otherUserId);
		return resultVo;
	}

	/**
	 * false 排行榜没有找到机器人
	 * 
	 * @param subItem
	 */
	public boolean robotRequestAddPlayerToFriend(OpenLevelTiggerServiceSubItem subItem, TableFriend friendTable) {
		FriendHandler handler = FriendHandler.getInstance();
		List<FriendInfo> robotList = handler.reCommandRobot(m_pPlayer, friendTable, RankType.LEVEL_ROBOT, false);
		if (robotList == null || robotList.isEmpty()) {
			GameLog.error(LogModule.robotFriend, m_pPlayer.getUserId(), "隔时推送没找到机器人", null);
			return false;
		}
		handler.updataRobotLoginTime(robotList);
		FriendInfo robot = robotList.get(0);
		String robotUserId = robot.getUserId();
		TableFriend otherTable = getOtherTableFriend(robotUserId);
		Player robotPlayer = PlayerMgr.getInstance().find(robotUserId);
		robotPlayer.getFriendMgr().requestToAddFriend(m_pPlayer.getUserId(), robotUserId, otherTable);
		subItem.setUserId(robotUserId);
		return true;
	}

	/** 请求添加一群人好友 */
	public FriendResultVo requestAddFriendList(List<String> friendList) {
		FriendResultVo resultVo = new FriendResultVo();
		TableFriend tableFriend = getTableFriend();
		String userId = m_pPlayer.getUserId();
		resultVo.resultType = EFriendResultType.FAIL;
		resultVo.resultMsg = "没有向人申请好友";
		for (int i = 0; i < friendList.size(); i++) {
			resultVo.resultType = EFriendResultType.SUCCESS;
			resultVo.resultMsg = "申请成功";
			String otherUserId = friendList.get(i);
			Player other = PlayerMgr.getInstance().find(otherUserId);
			if (isSelfUser(otherUserId)) {
				// resultVo.resultType = EFriendResultType.FAIL;
				// resultVo.resultMsg = "该玩家是自己";
			} else if (tableFriend.getFriendList().containsKey(otherUserId)) {
				// resultVo.resultType = EFriendResultType.FAIL;
				// resultVo.resultMsg = "对方已经是你的好友";
			} else if (other != null && otherUserId.length() > 20) {
				requestAddOneRobotToFriend(otherUserId, userId);
			} else {
				TableFriend otherTable = getOtherTableFriend(otherUserId);
				if (otherTable.getBlackList().containsKey(userId)) {
					// 如果在对方的黑名单列表中，不做操作
				} else {
					FriendItem friendItem = FriendItem.newInstance(userId);
					if (!otherTable.getRequestList().containsKey(friendItem.getUserId())) {
						otherTable.getRequestList().put(friendItem.getUserId(), friendItem);
						FriendHandler.getInstance().pushRequestAddFriend(PlayerMgr.getInstance().find(otherUserId), friendItem);
						tableFriend.removeFromBlackList(otherUserId);
					}
					friendDAO.update(otherTable);
				}
				// 增加红点检查
				PlayerMgr.getInstance().setRedPointForHeartBeat(otherUserId);
			}
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

	/** 赠送体力 */
	public FriendResultVo givePower(String otherUserId) {
		FriendResultVo resultVo = new FriendResultVo();
		List<FriendItem> list = new ArrayList<FriendItem>();
		TableFriend tableFriend = getTableFriend();
		if (isSelfUser(otherUserId)) {
			resultVo.resultType = EFriendResultType.FAIL;
			resultVo.resultMsg = "该玩家是自己";
		} else if (tableFriend.getFriendList().containsKey(otherUserId)) {
			FriendGiveState friendGiveState = tableFriend.getFriendGiveList().get(otherUserId);
			if (friendGiveState.isGiveState()) {// 可赠送
				friendGiveState.setGiveState(false);
				TableFriend otherFriend = getOtherTableFriend(otherUserId);
				if (otherFriend != null) {
					if (otherFriend.getFriendGiveList().containsKey(m_pPlayer.getUserId())) {
						otherFriend.getFriendGiveList().get(m_pPlayer.getUserId()).setReceiveState(true);
						friendDAO.update(otherFriend);
					}
				}

				UserEventMgr.getInstance().givePowerVitality(m_pPlayer, 1);
				list.add(tableFriend.getFriendList().get(otherUserId));
				resultVo.resultType = EFriendResultType.SUCCESS;
				resultVo.updateList = friendItemToInfoList(list);
				resultVo.resultMsg = "赠送成功";
				PlayerMgr.getInstance().setRedPointForHeartBeat(otherUserId);

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
					tableFriend.getFriendVo().addOnePower(1);
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
		String userId = m_pPlayer.getUserId();
		while (it.hasNext()) {
			FriendItem friendItem = it.next();
			FriendGiveState giveState = tableFriend.getFriendGiveList().get(friendItem.getUserId());
			if (!giveState.isGiveState()) {// 不能赠送
				continue;
			}

			TableFriend otherFriend = getOtherTableFriend(giveState.getUserId());
			if (otherFriend != null) {
				FriendGiveState otherGiveState = otherFriend.getFriendGiveList().get(userId);
				if (otherGiveState == null) {
					// 2016-10-17 by Perry : 有个bug是在对方的列表没有找到自身的数据，看了添加的逻辑，没有发现什么问题，这里先在这里做一个保护，后续有时间继续跟进
					otherGiveState = new FriendGiveState();
					otherGiveState.setUserId(userId);
					otherFriend.getFriendGiveList().put(userId, otherGiveState);
				}
				otherGiveState.setReceiveState(true);// 设置接受成功
				friendDAO.update(otherFriend);
				UserEventMgr.getInstance().givePowerVitality(m_pPlayer, 1);
			}

			giveState.setGiveState(false);
			count++;
			list.add(friendItem);
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

		tableFriend.getFriendVo().addOnePower(count); // 2016-08-30 by PERRY

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
		// tableFriend.getFriendVo().addOnePower(resultVo.powerCount); // 放前一点

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

	private Comparator<FriendItem> comparator = new Comparator<FriendItem>() {

		@Override
		public int compare(FriendItem o1, FriendItem o2) {
			if (o1.getLastLoginTime() - o2.getLastLoginTime() > 0) {
				return -1;
			} else {
				return 1;
			}
		}
	};

	private List<FriendInfo> friendItemToInfoList(Map<String, FriendItem> map) {
		// 临时更改好友列表
		// initFriendData(map);
		if (map == null || map.isEmpty()) {
			return new ArrayList<FriendInfo>();
		}
		// TODO 不改好友协议临时兼容方案， 多一次ArrayList的创建
		ArrayList<FriendItem> l = new ArrayList<FriendItem>(map.values());
		Collections.sort(l, comparator);
		List<FriendInfo> list = new ArrayList<FriendInfo>(l.size());
		// Iterator<FriendItem> it = map.values().iterator();
		Iterator<FriendItem> it = l.iterator();
		while (it.hasNext()) {
			FriendItem item = it.next();
			list.add(friendItemToInfo(item));
		}
		// Collections.sort(list, comparator);
		return list;
	}

	public FriendInfo friendItemToInfo(FriendItem item) {
		FriendInfo.Builder friendInfo = FriendInfo.newBuilder();
		String userId = item.getUserId();
		friendInfo.setUserId(item.getUserId());
		friendInfo.setUserName(item.getUserName());
		friendInfo.setHeadImage(item.getUserHead());
		friendInfo.setCareer(item.getCareer());
		friendInfo.setUnionName(item.getUnionName());
		friendInfo.setLastLoginTime(item.getLastLoginTime());
		friendInfo.setLastLoginTip(FriendUtils.getLastLoginTip(item.getLastLoginTime()));
		friendInfo.setLevel(item.getLevel());
		friendInfo.setGroupId(GroupHelper.getUserGroupId(userId));
		friendInfo.setGroupName(GroupHelper.getGroupName(userId));

		if (item.getHeadFrame() == null) {
			List<String> defaultHeadBoxList = HeadBoxCfgDAO.getInstance().getHeadBoxByType(HeadBoxType.HEADBOX_DEFAULT);
			// TODO 这个逻辑应该放在setting中完成
			item.setHeadFrame(defaultHeadBoxList.get(0));
		}
		friendInfo.setHeadbox(item.getHeadFrame());
		TableFriend tableFriend = getTableFriend();
		FriendGiveState giveState = tableFriend.getFriendGiveList().get(item.getUserId());
		if (giveState != null) {
			friendInfo.setGiveState(giveState.isGiveState());
			friendInfo.setReceiveState(giveState.isReceiveState());
		}
		Ranking<FightingComparable, RankingLevelData> ranking = RankingFactory.getRanking(RankType.FIGHTING_ALL);
		RankingEntry<FightingComparable, RankingLevelData> entry = ranking.getRankingEntry(userId);
		if (entry != null) {
			friendInfo.setFighting(entry.getComparable().getFighting());
		} else {
			friendInfo.setFighting(item.getFighting());
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
	public void onPlayerChange(Player changedPlayer) {
		TableFriend tableFriend = getTableFriend();
		changeOtherInfo(tableFriend, changedPlayer, FriendGetOperation.FRIEND);
		changeOtherInfo(tableFriend, changedPlayer, FriendGetOperation.BLACKLIST);
	}

	private void changeOtherInfo(TableFriend hostTable, Player changedPlayer, FriendGetOperation op) {
		Enumeration<FriendItem> it = op.getItemEnumeration(hostTable);
		while (it.hasMoreElements()) {
			FriendItem item = it.nextElement();
			String otherUserId = item.getUserId();
			// 只更新在内存玩家
			TableFriend otherTableFriend = TableFriendDAO.getInstance().getFromMemory(otherUserId);
			if (otherTableFriend == null) {
				return;
			}
			FriendItem friendItem = op.getItem(otherTableFriend, userId);
			if (friendItem != null) {
				changeFriendItem(friendItem, changedPlayer);
			}
		}
	}

	private void changeFriendItem(FriendItem friendItem, Player player) {
		friendItem.setUserId(player.getUserId());
		friendItem.setUserName(player.getUserName());
		friendItem.setLastLoginTime(player.getLastLoginTime());
		friendItem.setLevel(player.getLevel());
		friendItem.setUserHead(player.getHeadImage());
		friendItem.setCareer(player.getCareer());
		// 添加头像框
		friendItem.setHeadFrame(player.getUserGameDataMgr().getHeadBox());
		// friendItem.setUnionName(player.getGuildUserMgr().getGuildName());
		// TODO 帮派获取名字后再提供
		friendItem.setUnionName(GroupMemberHelper.getGroupName(player));
		friendItem.setFighting(player.getHeroMgr().getFightingAll(player));
	}

	private void notifyLoginTime(TableFriend hostTable, FriendGetOperation getOp, String userId, long currentTime) {
		TableFriendDAO friendDAO = TableFriendDAO.getInstance();
		Enumeration<FriendItem> enumeration = getOp.getItemEnumeration(hostTable);
		while (enumeration.hasMoreElements()) {
			FriendItem item = enumeration.nextElement();
			String otherUserId = item.getUserId();
			TableFriend otherTable = friendDAO.getFromMemory(otherUserId);
			if (otherTable == null) {
				continue;
			}
			// 获取别人最新信息(只限内存操作)
			Player otherPlayer = PlayerMgr.getInstance().findPlayerFromMemory(otherUserId);
			changeFriendItem(item, otherPlayer);
			// 把自己的登录时间更新给其他人(只限内存操作)
			FriendItem self = getOp.getItem(otherTable, userId);
			if (self != null) {
				self.setLastLoginTime(currentTime);
			}
		}
	}

	public boolean save() {
		friendDAO.update(userId);
		return true;
	}

}
