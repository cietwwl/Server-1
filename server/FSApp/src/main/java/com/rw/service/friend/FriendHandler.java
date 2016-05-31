package com.rw.service.friend;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import com.rwbase.dao.friend.FriendUtils;
import com.rwbase.dao.friend.vo.FriendItem;
import com.rwbase.dao.friend.vo.FriendResultVo;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
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
//			response.setIsSearchValue(false);
//			response.setResultMsg("没有找到该玩家");
//			response.setResultType(EFriendResultType.FAIL);
//
//			System.err.println(response.build());
//			return response.build().toByteString();
			FriendResultVo resultVo = new FriendResultVo();
			resultVo.updateList = recommandFriends(player);
			resultVo.resultMsg = "为你推荐以下玩家";
			resultVo.resultType = EFriendResultType.SUCCESS;
			return response.build().toByteString();
		} else {
			FriendResultVo resultVo = player.getFriendMgr().searchFriend(searchKey);
			response.setIsSearchValue(!resultVo.updateList.isEmpty());
			response.addAllList(resultVo.updateList);
			response.setResultMsg(resultVo.resultMsg);
			response.setResultType(resultVo.resultType);

			System.err.println(response.build());
			return response.build().toByteString();
		}

		// FriendResultVo resultVo =
		// player.getFriendMgr().searchFriend(searchKey);
		// if (resultVo.updateList.isEmpty()) {
		// resultVo.updateList = player.getFriendMgr().searchNearFriend();
		// if (resultVo.updateList.isEmpty()) {
		// response.setIsSearchValue(false);
		// resultVo.resultMsg = "没有找到该玩家，为你推荐以下玩家";
		// } else {
		// response.setIsSearchValue(true);
		// }
		// } else {
		// response.setIsSearchValue(true);
		// }
		//
		// response.setResultType(resultVo.resultType);
		// response.setResultMsg(resultVo.resultMsg);
		// response.addAllList(resultVo.updateList);
		// response.setResultType(EFriendResultType.SUCCESS);
		// return response.build().toByteString();
	}

	// 需要做成配置
	private int recommandCount = 10;

	private List<FriendInfo> recommandFriends(Player player) {
		Ranking<LevelComparable, RankingLevelData> ranking = RankingFactory.getRanking(RankType.LEVEL_ALL);
		int level = player.getLevel();
		int start = Math.max(1, level - 10);
		int end = level + 10;
		SegmentList<? extends MomentRankingEntry<LevelComparable, RankingLevelData>> segmentList = ranking.getSegmentList(new LevelComparable(start, 0), new LevelComparable(end, Integer.MAX_VALUE));
		int size = segmentList.getRefSize();
		boolean findMemory = size > recommandCount;
		ArrayList<Player> playerList = new ArrayList<Player>(size);
		PlayerMgr playerMgr = PlayerMgr.getInstance();
		for (int i = 0; i < size; i++) {
			MomentRankingEntry<LevelComparable, RankingLevelData> momentRankingEntry = segmentList.get(i);
			String userId = momentRankingEntry.getKey();
			Player otherPlayer;
			if (findMemory) {
				otherPlayer = playerMgr.findPlayerFromMemory(userId);
			} else {
				otherPlayer = playerMgr.find(userId);
			}
			if (otherPlayer != null) {
				playerList.add(otherPlayer);
			}
		}
		int currentSize = playerList.size();
		if (currentSize <= recommandCount) {
			ArrayList<FriendInfo> resultList = new ArrayList<FriendServiceProtos.FriendInfo>(currentSize);
			for (int i = 0; i < currentSize; i++) {
				Player otherPlayer = playerList.get(i);
				FriendItem friendItem = FriendItem.newInstance(otherPlayer.getUserId());
				resultList.add(otherPlayer.getFriendMgr().friendItemToInfo(friendItem));
			}
			return resultList;
		}
		size = playerList.size();
		RecommandCfgDAO cfgDAO = RecommandCfgDAO.getInstance();
		ArrayList<TempFriendItem> tempList = new ArrayList<TempFriendItem>(size);
		int total = 0;
		for (int i = size; --i >= 0;) {
			Player other = playerList.get(i);
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
					//删除最后一个元素
					tempList.set(j, tempList.get(tempListSize - 1));
					tempList.remove(tempListSize);
					break;
				}
			}
		}
		return resultList;
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
