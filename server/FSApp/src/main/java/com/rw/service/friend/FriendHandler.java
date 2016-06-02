package com.rw.service.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.bm.rank.RankType;
import com.bm.rank.level.LevelComparable;
import com.common.HPCUtil;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.common.SegmentList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.service.friend.datamodel.RecommandCfgDAO;
import com.rw.service.friend.datamodel.RecommandConditionCfg;
import com.rw.service.friend.datamodel.RecommandConditionCfgDAO;
import com.rwbase.dao.friend.FriendUtils;
import com.rwbase.dao.friend.vo.FriendItem;
import com.rwbase.dao.friend.vo.FriendResultVo;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.user.User;
import com.rwproto.FriendServiceProtos;
import com.rwproto.FriendServiceProtos.AllList;
import com.rwproto.FriendServiceProtos.EFriendRequestType;
import com.rwproto.FriendServiceProtos.EFriendResultType;
import com.rwproto.FriendServiceProtos.FriendInfo;
import com.rwproto.FriendServiceProtos.FriendRequest;
import com.rwproto.FriendServiceProtos.FriendResponse;
import com.rwproto.MsgDef.Command;

/** 好友通迅类 */
public class FriendHandler {
	private static FriendHandler instance = new FriendHandler();

	private FriendHandler() {
	}

	public static FriendHandler getInstance() {
		return instance;
	}

	/** 所有列表 */
	public ByteString allList(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		AllList.Builder allListObj = AllList.newBuilder();
		allListObj.addAllFriendList(player.getFriendMgr().getFriendList());
		allListObj.addAllRequestList(player.getFriendMgr().getRequestList());
		allListObj.addAllBlackList(player.getFriendMgr().getBlackList());
		response.setAllList(allListObj);
		response.setResultType(EFriendResultType.SUCCESS);

		FriendUtils.checkHasNotReceive(player, player.getFriendMgr().getTableFriend());
		return response.build().toByteString();
	}

	/** 好友列表 */
	public ByteString friendList(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.addAllList(player.getFriendMgr().getFriendList());
		response.setResultType(EFriendResultType.SUCCESS);
		return response.build().toByteString();
	}

	/** 黑名单列表 */
	public ByteString blackList(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.addAllList(player.getFriendMgr().getBlackList());
		response.setResultType(EFriendResultType.SUCCESS);
		return response.build().toByteString();
	}

	/** 好友请求列表 */
	public ByteString requestList(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.addAllList(player.getFriendMgr().getRequestList());
		response.setResultType(EFriendResultType.SUCCESS);
		return response.build().toByteString();
	}

	/** 搜索玩家 */
	public ByteString searchFriend(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		String searchKey = request.getSearchKey();
		if (searchKey == null || searchKey.isEmpty()) {
			return defaultSearch(response, player, "为您推荐以下玩家");
		} else {
			// TODO 这个resultVO留待以后重构，创建许多无谓对象，思路混乱
			FriendResultVo resultVo = player.getFriendMgr().searchFriend(searchKey);
			boolean isSearchValue = resultVo.updateList.isEmpty();
			if (!isSearchValue) {
				response.setIsSearchValue(isSearchValue);
				response.addAllList(resultVo.updateList);
				response.setResultMsg(resultVo.resultMsg);
				response.setResultType(resultVo.resultType);
				return response.build().toByteString();
			} else {
				return defaultSearch(response, player, "找不到对应玩家，为您推荐以下玩家");
			}
		}
	}

	private int defaultRecommandCount = 10;

	private ByteString defaultSearch(FriendResponse.Builder response, Player player, String tips) {
		List<FriendInfo> friendList = recommandFriends(player);
		boolean searchValue;
		String text;
		if (friendList.isEmpty()) {
			searchValue = false;
			text = "找不到对应玩家";
		} else {
			searchValue = true;
			text = tips;
		}
		response.setIsSearchValue(searchValue);
		response.addAllList(friendList);
		response.setResultMsg(text);
		response.setResultType(EFriendResultType.SUCCESS);
		return response.build().toByteString();
	}

	private List<FriendInfo> recommandFriends(Player player) {
		Ranking<LevelComparable, RankingLevelData> ranking = RankingFactory.getRanking(RankType.LEVEL_PLAYER);
		int level = player.getLevel();
		int start = 0;
		int end = 0;
		HashMap<String, Player> playersMap = new HashMap<String, Player>();
		// 按条件过滤出要随机的人数
		List<RecommandConditionCfg> cfgList = RecommandConditionCfgDAO.getInstance().getOrderConditions();
		int recommandCount = defaultRecommandCount;
		for (int i = 0, len = cfgList.size(); i < len; i++) {
			RecommandConditionCfg cfg = cfgList.get(i);
			// 每次循环都会改变随机人数，由配置决定
			int randomRecommand = cfg.getRandomCount();
			int days = cfg.getDays();
			recommandCount = cfg.getCount();
			if (i == 0) {
				start = Math.max(1, level + cfg.getDesLevel());
				end = level + cfg.getIncLevel();
				fillSegmentPlayers(playersMap, ranking, start, end, days, randomRecommand);
				if (playersMap.size() >= randomRecommand) {
					break;
				}
			} else {
				int tempEnd = end;
				if (start > 1) {
					end = start - 1;
					start = start + cfg.getDesLevel();
					fillSegmentPlayers(playersMap, ranking, start, end, days, randomRecommand);
					if (playersMap.size() >= randomRecommand) {
						break;
					}
				}
				start = tempEnd + 1;
				end = tempEnd + cfg.getIncLevel();
				fillSegmentPlayers(playersMap, ranking, start, end, days, randomRecommand);
				if (playersMap.size() >= randomRecommand) {
					break;
				}
			}
		}
		// 移除自己
		playersMap.remove(player.getUserId());
		int currentSize = playersMap.size();
		if (currentSize <= recommandCount) {
			ArrayList<FriendInfo> resultList = new ArrayList<FriendServiceProtos.FriendInfo>(currentSize);
			for (Player otherPlayer : playersMap.values()) {
				FriendItem friendItem = FriendItem.newInstance(otherPlayer.getUserId());
				resultList.add(otherPlayer.getFriendMgr().friendItemToInfo(friendItem));
			}
			return resultList;
		}
		// 按权重随机
		RecommandCfgDAO cfgDAO = RecommandCfgDAO.getInstance();
		ArrayList<TempFriendItem> tempList = new ArrayList<TempFriendItem>(currentSize);
		int total = 0;
		for (Player other : playersMap.values()) {
			int weight = cfgDAO.getWeight(player, other) + 100;
			if (weight > 0) {
				total += weight;
				tempList.add(new TempFriendItem(other, weight));
			}
		}
		Random random = HPCUtil.getRandom();
		ArrayList<FriendInfo> resultList = new ArrayList<FriendServiceProtos.FriendInfo>(recommandCount);
		for (int i = recommandCount; --i >= 0;) {
			int r = random.nextInt(total);
			int current = 0;
			int tempListSize = tempList.size();
			for (int j = tempListSize; --j >= 0;) {
				TempFriendItem item = tempList.get(j);
				int w = item.getWeight();
				current += w;
				if (current >= r) {
					total -= w;
					Player otherPlayer = item.getPlayer();
					FriendItem friendItem = FriendItem.newInstance(otherPlayer.getUserId());
					resultList.add(otherPlayer.getFriendMgr().friendItemToInfo(friendItem));
					// 删除最后一个元素
					int last = tempListSize - 1;
					tempList.set(j, tempList.get(last));
					tempList.remove(last);
					break;
				}
			}
		}
		return resultList;
	}

	private void fillSegmentPlayers(HashMap<String, Player> playersMap, Ranking<LevelComparable, RankingLevelData> ranking, int start, int end, int days, int randomRecommand) {
		SegmentList<? extends MomentRankingEntry<LevelComparable, RankingLevelData>> segmentList = ranking.getSegmentList(new LevelComparable(start, 0), new LevelComparable(end, Integer.MAX_VALUE));
		int size = segmentList.getRefSize();
		ArrayList<String> list = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			MomentRankingEntry<LevelComparable, RankingLevelData> momentRankingEntry = segmentList.get(i);
			list.add(momentRankingEntry.getKey());
		}
		PlayerMgr playerMgr = PlayerMgr.getInstance();
		for (int i = 0; i < size; i++) {
			String userId = list.get(i);
			Player otherPlayer = playerMgr.findPlayerFromMemory(userId);
			if (otherPlayer != null) {
				playersMap.put(userId, otherPlayer);
			}
		}

		int currentSize = playersMap.size();
		// 不够数获取一个
		if (currentSize < randomRecommand && currentSize < size) {
			Player otherUser = getOneRandomPlayer(list, playersMap);
			if (otherUser != null) {
				playersMap.put(otherUser.getUserId(), otherUser);
			}
		}
		long MAX_OFF_LINE_TIME = TimeUnit.DAYS.toMillis(days);
		for (Iterator<Player> it = playersMap.values().iterator(); it.hasNext();) {
			Player player = it.next();
			User user = player.getUserDataMgr().getUser();
			if (user == null || System.currentTimeMillis() - user.getLastLoginTime() > MAX_OFF_LINE_TIME) {
				it.remove();
			}
		}
	}

	private Player getOneRandomPlayer(ArrayList<String> segmentList, HashMap<String, Player> playersMap) {
		int size = segmentList.size();
		if (size == 0) {
			return null;
		}
		int random = HPCUtil.getRandom().nextInt(size);
		for (int i = 0; i < size; i++) {
			String userId = segmentList.get(random);
			if (playersMap.containsKey(userId)) {
				if ((random & 1) == 1) {
					random++;
				} else {
					random--;
				}
				continue;
			}
			return PlayerMgr.getInstance().find(userId);
		}
		return null;
	}

	/** 赠送体力 */
	public ByteString givePower(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setOtherUserId(request.getOtherUserId());

		FriendResultVo resultVo = player.getFriendMgr().givePower(request.getOtherUserId());
		response.setResultType(resultVo.resultType);
		response.setResultMsg(resultVo.resultMsg);
		response.addAllUpdateList(resultVo.updateList);
		return response.build().toByteString();
	}

	/** 领取体力 */
	public ByteString receivePower(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setOtherUserId(request.getOtherUserId());

		FriendResultVo resultVo = player.getFriendMgr().receivePower(request.getOtherUserId());
		response.setResultType(resultVo.resultType);
		response.setResultMsg(resultVo.resultMsg);
		response.addAllUpdateList(resultVo.updateList);

		if (resultVo.resultType != EFriendResultType.FAIL) {// 没有失败
			player.addPower(resultVo.powerCount);// 加体力
		}
		return response.build().toByteString();
	}

	/** 一键赠送体力 */
	public ByteString givePowerAll(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());

		FriendResultVo resultVo = player.getFriendMgr().givePowerAll();
		response.setResultType(resultVo.resultType);
		response.setResultMsg(resultVo.resultMsg);
		response.addAllUpdateList(resultVo.updateList);
		return response.build().toByteString();
	}

	/** 一键领取体力 */
	public ByteString receivePowerAll(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		FriendResultVo resultVo = player.getFriendMgr().receivePowerAll();
		response.setResultType(resultVo.resultType);
		response.setResultMsg(resultVo.resultMsg);
		response.addAllUpdateList(resultVo.updateList);

		if (resultVo.resultType != EFriendResultType.FAIL) {// 没有失败
			player.addPower(resultVo.powerCount);
		}

		return response.build().toByteString();
	}

	/** 请求添加好友 */
	public ByteString requestAddFriend(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setOtherUserId(request.getOtherUserId());

		FriendResultVo resultVo = player.getFriendMgr().requestAddFriend(request.getOtherUserId());
		response.setResultType(resultVo.resultType);
		response.setResultMsg(resultVo.resultMsg);
		response.addAllUpdateList(resultVo.updateList);
		return response.build().toByteString();
	}

	/** 删除好友 */
	public ByteString removeFriend(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setOtherUserId(request.getOtherUserId());
		FriendResultVo resultVo = player.getFriendMgr().removeFriend(request.getOtherUserId());
		response.setResultType(resultVo.resultType);
		response.setResultMsg(resultVo.resultMsg);
		return response.build().toByteString();
	}

	/** 加入黑名单 */
	public ByteString addBlack(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setOtherUserId(request.getOtherUserId());
		FriendResultVo resultVo = player.getFriendMgr().addBlack(request.getOtherUserId());
		response.setResultType(resultVo.resultType);
		response.setResultMsg(resultVo.resultMsg);
		response.addAllUpdateList(resultVo.updateList);
		return response.build().toByteString();
	}

	/** 移出黑名单 */
	public ByteString removeBlack(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setOtherUserId(request.getOtherUserId());
		FriendResultVo resultVo = player.getFriendMgr().removeBlack(request.getOtherUserId());
		response.setResultType(resultVo.resultType);
		response.setResultMsg(resultVo.resultMsg);
		if (resultVo.resultType == EFriendResultType.FAIL) {// 如果不在黑名单列表中，推送黑名单列表
			pushBlackList(player);
		}
		return response.build().toByteString();
	}

	/** 同意添加好友 */
	public ByteString consentAddFriend(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setOtherUserId(request.getOtherUserId());
		FriendResultVo resultVo = player.getFriendMgr().consentAddFriend(request.getOtherUserId());
		response.setResultType(resultVo.resultType);
		response.setResultMsg(resultVo.resultMsg);
		response.addAllUpdateList(resultVo.updateList);
		if (resultVo.resultType == EFriendResultType.FAIL_2) {
			pushRequestList(player);
		}
		return response.build().toByteString();
	}

	/** 拒绝添加好友 */
	public ByteString refusedAddFriend(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setOtherUserId(request.getOtherUserId());
		FriendResultVo resultVo = player.getFriendMgr().refusedAddFriend(request.getOtherUserId());
		response.setResultType(resultVo.resultType);
		response.setResultMsg(resultVo.resultMsg);
		if (resultVo.resultType == EFriendResultType.FAIL) {
			pushRequestList(player);
		}
		return response.build().toByteString();
	}

	/** 同意添加好友(全部) */
	public ByteString consentAddFriendAll(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setOtherUserId(request.getOtherUserId());
		FriendResultVo resultVo = player.getFriendMgr().consentAddFriendAll();
		response.setResultType(resultVo.resultType);
		response.addAllUpdateList(resultVo.updateList);
		response.setResultMsg(resultVo.resultMsg);
		return response.build().toByteString();
	}

	/** 拒绝添加好友(全部) */
	public ByteString refusedAddFriendAll(FriendRequest request, Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(request.getRequestType());
		response.setOtherUserId(request.getOtherUserId());
		FriendResultVo resultVo = player.getFriendMgr().refusedAddFriendAll();
		response.setResultType(resultVo.resultType);
		response.setResultMsg(resultVo.resultMsg);
		return response.build().toByteString();
	}

	/** 推送请求添加好友 */
	public void pushRequestAddFriend(Player player, FriendItem friendItem) {
		if (player == null) {
			return;
		}
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(EFriendRequestType.REQUEST_ADD_FRIEND);
		response.setResultType(EFriendResultType.SUCCESS);
		response.setOtherUserId(friendItem.getUserId());
		response.addAllUpdateList(player.getFriendMgr().friendItemToInfoList(friendItem));
		PlayerMgr.getInstance().SendToPlayer(Command.MSG_FRIEND, response.build().toByteString(), player);
	}

	/** 推送同意添加的好友 */
	public void pushConsentAddFriend(Player player, FriendItem friendItem) {
		if (player == null) {
			return;
		}
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(EFriendRequestType.CONSENT_ADD_FRIEND);
		response.setResultType(EFriendResultType.SUCCESS);
		response.setOtherUserId(friendItem.getUserId());
		response.addAllUpdateList(player.getFriendMgr().friendItemToInfoList(friendItem));
		PlayerMgr.getInstance().SendToPlayer(Command.MSG_FRIEND, response.build().toByteString(), player);
	}

	/** 推送移除好友 */
	public void pushRemoveFriend(Player player, String otherUserId) {
		if (player == null) {
			return;
		}
		FriendUtils.checkHasNotReceive(player, player.getFriendMgr().getTableFriend());
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(EFriendRequestType.REMOVE_FRIEND);
		response.setResultType(EFriendResultType.SUCCESS);
		response.setOtherUserId(otherUserId);
		PlayerMgr.getInstance().SendToPlayer(Command.MSG_FRIEND, response.build().toByteString(), player);
	}

	/** 推送好友列表 */
	public void pushFriendList(Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(EFriendRequestType.FRIEND_LIST);
		response.setResultType(EFriendResultType.SUCCESS);
		response.addAllList(player.getFriendMgr().getFriendList());
		player.SendMsg(Command.MSG_FRIEND, response.build().toByteString());
	}

	/** 推送请求列表 */
	public void pushRequestList(Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(EFriendRequestType.REQUEST_LIST);
		response.setResultType(EFriendResultType.SUCCESS);
		response.addAllList(player.getFriendMgr().getRequestList());
		player.SendMsg(Command.MSG_FRIEND, response.build().toByteString());
	}

	/** 推送黑名单列表 */
	public void pushBlackList(Player player) {
		FriendResponse.Builder response = FriendResponse.newBuilder();
		response.setRequestType(EFriendRequestType.BLACK_LIST);
		response.setResultType(EFriendResultType.SUCCESS);
		response.addAllList(player.getFriendMgr().getBlackList());
		player.SendMsg(Command.MSG_FRIEND, response.build().toByteString());
	}
}
