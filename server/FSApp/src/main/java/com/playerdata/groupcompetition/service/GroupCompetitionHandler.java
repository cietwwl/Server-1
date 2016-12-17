package com.playerdata.groupcompetition.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bm.rank.groupCompetition.groupRank.GCompFightingItem;
import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.bm.rank.groupCompetition.groupRank.groupRankStatic.GroupStaticRankMgr;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.holder.GCompDetailInfoMgr;
import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.holder.GCompFightingRecordMgr;
import com.playerdata.groupcompetition.holder.GCompGroupScoreRankingMgr;
import com.playerdata.groupcompetition.holder.GCompHistoryDataMgr;
import com.playerdata.groupcompetition.holder.GCompTeamMgr;
import com.playerdata.groupcompetition.holder.data.GCompGroupScoreRecord;
import com.playerdata.groupcompetition.holder.data.GCompGroupTotalScoreRecord;
import com.playerdata.groupcompetition.holder.data.GCompHistoryData;
import com.playerdata.groupcompetition.prepare.PrepareAreaMgr;
import com.playerdata.groupcompetition.quiz.GCompQuizMgr;
import com.playerdata.groupcompetition.rank.GCompRankMgr;
import com.playerdata.groupcompetition.rank.ScoreInfoInPrepareMgr;
import com.playerdata.groupcompetition.stageimpl.GCGroup;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.playerdata.groupcompetition.util.GCompTips;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwproto.GroupCompetitionProto.CommonGetDataReqMsg;
import com.rwproto.GroupCompetitionProto.CommonGetDataRspMsg;
import com.rwproto.GroupCompetitionProto.CommonReqMsg;
import com.rwproto.GroupCompetitionProto.CommonRsp;
import com.rwproto.GroupCompetitionProto.CommonRspMsg;
import com.rwproto.GroupCompetitionProto.GCRequestType;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.GroupCompetitionProto.GCompGroupScoreRankItem;
import com.rwproto.GroupCompetitionProto.GCompGroupScoreRankRspData;
import com.rwproto.GroupCompetitionProto.GCompHistoryChampion;
import com.rwproto.GroupCompetitionProto.GroupScoreData;
import com.rwproto.GroupCompetitionProto.JoinTeamReq;
import com.rwproto.GroupCompetitionProto.ReqAllGuessInfo;
import com.rwproto.GroupCompetitionProto.ReqNewGuess;
import com.rwproto.GroupCompetitionProto.RspAllGuessInfo;
import com.rwproto.GroupCompetitionProto.RsqNewGuess;
import com.rwproto.GroupCompetitionProto.SelectionGroupData;
import com.rwproto.GroupCompetitionProto.SelectionRspData;
import com.rwproto.GroupCompetitionProto.TeamMemberRequest;
import com.rwproto.GroupCompetitionProto.TeamRequest;
import com.rwproto.GroupCompetitionProto.TeamStatusRequest;

public class GroupCompetitionHandler {

	private static GroupCompetitionHandler instance = new GroupCompetitionHandler();

	protected GroupCompetitionHandler() {
	}

	public static GroupCompetitionHandler getInstance() {
		return instance;
	}

	private int calculateGroupFighting(Group group) {
		int fighting = 0;
		List<? extends GroupMemberDataIF> members = group.getGroupMemberMgr().getMemberSortList(null);
		for (int i = 0, size = members.size(); i < size; i++) {
			fighting += members.get(i).getFighting();
		}
		return fighting;
	}

	private SelectionGroupData createSelectionGroupData(GCompFightingItem fightingItem, int ranking) {
		SelectionGroupData.Builder groupDataBuilder = SelectionGroupData.newBuilder();
		groupDataBuilder.setRanking(ranking);
		groupDataBuilder.setFighting(fightingItem.getGroupFight());
		groupDataBuilder.setName(fightingItem.getGroupName());
		if (fightingItem.getLastRank() > 0) {
			groupDataBuilder.setUpNum(fightingItem.getLastRank() - ranking); // ranking比lastRank小，则是上升，反之则是下降
		} else {
			groupDataBuilder.setUpNum(0);
		}
		return groupDataBuilder.build();
	}

	private SelectionGroupData createOwnGroupData(Player player, List<GCompFightingItem> gCompFightingItemList) {
		Group ownGroup = GroupHelper.getInstance().getGroup(player);
		SelectionGroupData ownGroupData;
		if (ownGroup != null) {
			GroupBaseDataIF baseData = ownGroup.getGroupBaseDataMgr().getGroupData();
			int ranking = GCompFightingRankMgr.getRankIndex(baseData.getGroupId());
			if (ranking > 0 && ranking <= GCompFightingRankMgr.MAX_RANK_COUNT) {
				// 在榜单中
				GCompFightingItem fightingItem;
				if (ranking < gCompFightingItemList.size()) {
					fightingItem = gCompFightingItemList.get(ranking - 1);
				} else {
					fightingItem = GCompFightingRankMgr.getFightingRankList().get(ranking - 1);
				}
				ownGroupData = this.createSelectionGroupData(fightingItem, ranking);
			} else {
				// 不在榜中
				SelectionGroupData.Builder ownGroupBuilder = SelectionGroupData.newBuilder();
				ownGroupBuilder.setName(baseData.getGroupName());
				ownGroupBuilder.setUpNum(0);
				ownGroupBuilder.setRanking(ranking);
				ownGroupBuilder.setFighting(this.calculateGroupFighting(ownGroup));
				ownGroupData = ownGroupBuilder.build();
			}
		} else {
			ownGroupData = null;
		}
		return ownGroupData;
	}

	private void packSelectionData(CommonGetDataRspMsg.Builder rspBuilder, Player player) {
		List<GCompFightingItem> list;
		GCompStageType stageType = GroupCompetitionMgr.getInstance().getCurrentStageType();
		if (stageType == GCompStageType.SELECTION && GroupCompetitionMgr.getInstance().getCurrentStageEndTime() > System.currentTimeMillis()) {
			list = GCompFightingRankMgr.getFightingRankList(20);
		} else {
			list = GroupStaticRankMgr.getInstance().getStaticGroupRank();
		}
		GCompFightingItem fightingItem;
		List<SelectionGroupData> selectionGroupDataList = new ArrayList<SelectionGroupData>();
		for (int i = 0, size = list.size(); i < size; i++) {
			fightingItem = list.get(i);
			selectionGroupDataList.add(this.createSelectionGroupData(fightingItem, i + 1));
		}
		SelectionGroupData ownGroupData = this.createOwnGroupData(player, list);
		SelectionRspData.Builder selectionRspDataBuilder = SelectionRspData.newBuilder();
		selectionRspDataBuilder.addAllRankings(selectionGroupDataList);
		if (ownGroupData != null) {
			selectionRspDataBuilder.setOwnGroupData(ownGroupData);
		}
		selectionRspDataBuilder.setSelectionEndTime(GroupCompetitionMgr.getInstance().getCurrentStageEndTime());
		rspBuilder.setSelectionData(selectionRspDataBuilder.build());
	}

	private CommonGetDataRspMsg.Builder createGetDataRspBuilder(GCResultType resultType, String tips) {
		CommonGetDataRspMsg.Builder builder = CommonGetDataRspMsg.newBuilder();
		builder.setRstType(resultType);
		if (tips != null) {
			builder.setTipMsg(tips);
		}
		return builder;
	}

	private CommonRsp createCommonRsp(GCResultType resultType, String tips) {
		CommonRsp.Builder builder = CommonRsp.newBuilder();
		builder.setResultType(resultType);
		if (tips != null) {
			builder.setTips(tips);
		}
		return builder.build();
	}

	public ByteString enterPrepareArea(Player player, CommonReqMsg request) {
		CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
		PrepareAreaMgr.getInstance().enterPrepareArea(player, gcRsp, request.getPosition());
		return gcRsp.build().toByteString();
	}

	public ByteString informPreparePosition(Player player, CommonReqMsg request) {
		CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
		PrepareAreaMgr.getInstance().informPreparePosition(player, gcRsp, request.getPosition());
		return gcRsp.build().toByteString();
	}

	public ByteString leavePrepareArea(Player player, CommonReqMsg request) {
		CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
		PrepareAreaMgr.getInstance().leavePrepareArea(player, gcRsp);
		return gcRsp.build().toByteString();
	}

	public ByteString getPlayersBaseInfo(Player player, CommonReqMsg request) {
		CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
		PrepareAreaMgr.getInstance().applyUsersBaseInfo(player, gcRsp, request.getPlayersIdListList());
		return gcRsp.build().toByteString();
	}

	// 检查是否处于某个阶段
	private GCResultType checkStageValidate(GCompStageType targetStageType) {
		GCompStageType stageType = GroupCompetitionMgr.getInstance().getCurrentStageType();
		GCResultType resultType;
		if (stageType != targetStageType) {
			resultType = GCResultType.DATA_ERROR;
		} else {
			resultType = GCResultType.SUCCESS;
		}
		return resultType;
	}

	/**
	 * 
	 * 获取海选信息
	 * 
	 * @param player
	 * @return
	 */
	public ByteString getSelectionData(Player player) {
		GCResultType resultType = this.checkStageValidate(GCompStageType.SELECTION);
		CommonGetDataRspMsg.Builder builder = this.createGetDataRspBuilder(resultType, resultType == GCResultType.DATA_ERROR ? GCompTips.getTipsNotSelectionStageNow() : null);
		if (resultType == GCResultType.SUCCESS) {
			this.packSelectionData(builder, player);
		}
		GCompHistoryDataMgr.getInstance().sendLastMatchData(player);
		return builder.build().toByteString();
	}

	/**
	 * 
	 * 获取赛事对阵
	 * 
	 * @param player
	 * @return
	 */
	public ByteString getMatchData(Player player) {
		GCResultType resultType = this.checkStageValidate(GCompStageType.EVENTS);
		// GCResultType resultType = GCResultType.SUCCESS;
		CommonGetDataRspMsg.Builder builder = this.createGetDataRspBuilder(resultType, resultType == GCResultType.DATA_ERROR ? GCompTips.getTipsNotMatchStageNow() : null);
		if (resultType == GCResultType.SUCCESS) {
			// 成功才有数据
			GCompEventsDataMgr.getInstance().sendMatchData(player);
		}
		return builder.build().toByteString();
	}

	public ByteString haveNewGuess(Player player, ReqNewGuess request) {
		RsqNewGuess.Builder gcRsp = RsqNewGuess.newBuilder();
		GCompQuizMgr.getInstance().createNewQuiz(player, gcRsp, request.getMatchId(), request.getGroupId(), request.getCoin());
		return gcRsp.build().toByteString();
	}

	public ByteString getCanGuessMatch(Player player, ReqAllGuessInfo request) {
		RspAllGuessInfo.Builder gcRsp = RspAllGuessInfo.newBuilder();
		GCompQuizMgr.getInstance().getCanQuizMatch(player, gcRsp);
		return gcRsp.build().toByteString();
	}

	/**
	 * 
	 * 处理玩家创建队伍或者调整队伍阵容
	 * 
	 * @param player
	 * @param teamRequest
	 * @return
	 */
	public ByteString teamRequest(Player player, TeamRequest teamRequest) {
		IReadOnlyPair<Boolean, String> processResult;
		switch (teamRequest.getReqType()) {
		case CreateTeam: // 创建队伍
			processResult = GCompTeamMgr.getInstance().createTeam(player, teamRequest.getHeroIdList());
			break;
		case AdjustTeamMember: // 调整出阵英雄
			processResult = GCompTeamMgr.getInstance().updateHeros(player, teamRequest.getHeroIdList());
			break;
		case StartRandomMatching: // 随机匹配
			processResult = GCompTeamMgr.getInstance().randomMatching(player, teamRequest.getHeroIdList());
			break;
		case CancelRandomMatching: // 取消随机匹配
			processResult = GCompTeamMgr.getInstance().cancelRandomMatching(player);
			break;
		case PersonalMatching: // 个人匹配
			processResult = GCompTeamMgr.getInstance().personalMatching(player, teamRequest.getHeroIdList());
			break;
		case PersonalCancelMatching: // 取消个人匹配
			processResult = GCompTeamMgr.getInstance().cancelTeamMatching(player);
			break;
		case CancelMatching: // 取消匹配
			processResult = GCompTeamMgr.getInstance().cancelTeamMatching(player);
			break;
		default:
			return ByteString.EMPTY;
		}
		GCompUtil.log("帮派争霸，teamRequest，userId：{}, 请求类型 : {}, 结果：{}, {}", player.getUserId(), teamRequest.getReqType(), processResult.getT1(), processResult.getT2());
		return this.createCommonRsp(processResult.getT1() ? GCResultType.SUCCESS : GCResultType.DATA_ERROR, processResult.getT2()).toByteString();
	}

	/**
	 * 
	 * 成员操作：处理{@link GCRequestType#InviteMember}和{@link GCRequestType#KickMember}
	 * 
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString teamMemberRequest(Player player, TeamMemberRequest request) {
		IReadOnlyPair<Boolean, String> processResult;
		switch (request.getReqType()) {
		case InviteMember: // 邀请队友
			processResult = GCompTeamMgr.getInstance().inviteMember(player, PlayerMgr.getInstance().find(request.getTargetUserId()));
			break;
		case KickMember: // 踢除队友
			processResult = GCompTeamMgr.getInstance().kickMember(player, request.getTargetUserId());
			break;
		default:
			return ByteString.EMPTY;
		}
		GCompUtil.log("帮派争霸，teamMemberRequest，请求类型 : {}, 结果：{}, {}", request.getReqType(), processResult.getT1(), processResult.getT2());
		return this.createCommonRsp(processResult.getT1() ? GCResultType.SUCCESS : GCResultType.DATA_ERROR, processResult.getT2()).toByteString();
	}

	/**
	 * 
	 * 加入队伍
	 * 
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString joinTeamRequest(Player player, JoinTeamReq request) {
		IReadOnlyPair<Boolean, String> processResult = GCompTeamMgr.getInstance().joinTeam(player, request.getTeamId(), request.getHeroIdList());
		return this.createCommonRsp(processResult.getT1() ? GCResultType.SUCCESS : GCResultType.DATA_ERROR, processResult.getT2()).toByteString();
	}

	/**
	 * 
	 * 队员状态操作：处理{@link GCRequestType#LeaveTeam}、{@link GCRequestType#SetTeamReady}以及{@link GCRequestType#CancelTeamReady}
	 * 
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString teamStatusRequest(Player player, TeamStatusRequest request) {
		IReadOnlyPair<Boolean, String> processResult;
		switch (request.getReqType()) {
		case LeaveTeam: // 离开队伍
			processResult = GCompTeamMgr.getInstance().leaveTeam(player);
			break;
		case SetTeamReady: // 设置准备状态
			processResult = GCompTeamMgr.getInstance().switchMemberStatus(player, true);
			break;
		case CancelTeamReady: // 取消准备状态
			processResult = GCompTeamMgr.getInstance().switchMemberStatus(player, false);
			break;
		case StartMatching: // 开始匹配
			processResult = GCompTeamMgr.getInstance().startTeamMatching(player);
			break;
		default:
			return ByteString.EMPTY;
		}
		GCompUtil.log("帮派争霸，teamStatusRequest，userId：{}, 请求类型 : {}, 结果：{}, {}", player.getUserId(), request.getReqType(), processResult.getT1(), processResult.getT2());
		return this.createCommonRsp(processResult.getT1() ? GCResultType.SUCCESS : GCResultType.DATA_ERROR, processResult.getT2()).toByteString();
	}

	/**
	 * 
	 * 获取赛事的详细信息
	 * 
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString getMatchDetailInfo(Player player, CommonGetDataReqMsg request) {
		CommonGetDataRspMsg.Builder builder = CommonGetDataRspMsg.newBuilder();
		GCompFightingRecordMgr.getInstance().getFightRecord(player, builder, request.getLivePara().getMatchId(), request.getLivePara().getLatestTime());
		GCompDetailInfoMgr.getInstance().sendDetailInfo(request.getLivePara().getMatchId(), player);
		return builder.build().toByteString();
	}

	/**
	 * 
	 * 获取赛事的详细信息
	 * 
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString getFightRecordLive(Player player, CommonGetDataReqMsg request) {
		CommonGetDataRspMsg.Builder builder = CommonGetDataRspMsg.newBuilder();
		GCompFightingRecordMgr.getInstance().getFightRecordLive(player, builder, request.getLivePara().getMatchId(), request.getLivePara().getLatestTime());
		GCompDetailInfoMgr.getInstance().sendDetailInfo(request.getLivePara().getMatchId(), player);
		return builder.build().toByteString();
	}

	public ByteString leaveLivePage(Player player, CommonGetDataReqMsg request) {
		CommonGetDataRspMsg.Builder builder = CommonGetDataRspMsg.newBuilder();
		GCompFightingRecordMgr.getInstance().leaveLivePage(player, builder, request.getLivePara().getMatchId());
		return builder.build().toByteString();
	}

	public ByteString getKillRank(Player player, CommonGetDataReqMsg request) {
		CommonGetDataRspMsg.Builder builder = CommonGetDataRspMsg.newBuilder();
		GCEventsType event = GCEventsType.getBySign(request.getGetRankPara().getEventsType());
		if (null == event) {
			builder.setRstType(GCResultType.DATA_ERROR);
			builder.setTipMsg("参数错误");
			return builder.build().toByteString();
		}
		GCompRankMgr.getInstance().getKillRank(player, builder, event);
		builder.setRstType(GCResultType.SUCCESS);
		return builder.build().toByteString();
	}

	public ByteString getScoreRank(Player player, CommonGetDataReqMsg request) {
		CommonGetDataRspMsg.Builder builder = CommonGetDataRspMsg.newBuilder();
		GCEventsType event = GCEventsType.getBySign(request.getGetRankPara().getEventsType());
		if (null == event) {
			builder.setRstType(GCResultType.DATA_ERROR);
			builder.setTipMsg("参数错误");
			return builder.build().toByteString();
		}
		GCompRankMgr.getInstance().getScoreRank(player, builder, event);
		builder.setRstType(GCResultType.SUCCESS);
		return builder.build().toByteString();
	}

	public ByteString getWinRank(Player player, CommonGetDataReqMsg request) {
		CommonGetDataRspMsg.Builder builder = CommonGetDataRspMsg.newBuilder();
		GCEventsType event = GCEventsType.getBySign(request.getGetRankPara().getEventsType());
		if (null == event) {
			builder.setRstType(GCResultType.DATA_ERROR);
			builder.setTipMsg("参数错误");
			return builder.build().toByteString();
		}
		GCompRankMgr.getInstance().getWinRank(player, builder, event);
		builder.setRstType(GCResultType.SUCCESS);
		return builder.build().toByteString();
	}

	public ByteString getGroupScoreRank(Player player) {
		GCEventsType fisrtTypeOfCurrent = GroupCompetitionMgr.getInstance().getFisrtTypeOfCurrent();
		List<GCompGroupTotalScoreRecord> allRecords = GCompGroupScoreRankingMgr.getInstance().getAllRecord();
		GCompGroupScoreRankRspData.Builder builder = GCompGroupScoreRankRspData.newBuilder();
		GCompGroupScoreRankItem.Builder rankItemBuilder;
		GCompGroupTotalScoreRecord tempRecord;
		GCompGroupScoreRecord currentRecord;
		builder.setTotalScoreRankItemCount(fisrtTypeOfCurrent == GCEventsType.TOP_16 ? 16 : 8);
		for (int i = 0, size = allRecords.size(); i < size; i++) {
			tempRecord = allRecords.get(i);
			currentRecord = tempRecord.getCurrentRecord();
			rankItemBuilder = GCompGroupScoreRankItem.newBuilder();
			rankItemBuilder.setGroupName(currentRecord.getGroupName());
			rankItemBuilder.setGroupIcon(currentRecord.getGroupIcon());
			rankItemBuilder.setCurrentScore(currentRecord.getScore());
			rankItemBuilder.setTotalScore(tempRecord.getTotalScore());
			rankItemBuilder.setFighting(tempRecord.getFighting());
			rankItemBuilder.setRanking(tempRecord.getRanking());
			builder.addScoreRankItem(rankItemBuilder.build());
		}
		GCompHistoryData historyData = GCompHistoryDataMgr.getInstance().getHistoryData();
		List<GCGroup> list = historyData.getHistoryChampions();
		if (list.size() > 0) {
			GCompHistoryChampion.Builder championBuilder;
			for (int i = 0, size = list.size(); i < size; i++) {
				GCGroup gcG = list.get(i);
				championBuilder = GCompHistoryChampion.newBuilder();
				championBuilder.setGroupIconId(gcG.getIcon());
				championBuilder.setGroupName(gcG.getGroupName());
				championBuilder.setLeaderName(gcG.getLeaderName());
				if (gcG.getAssistantName() != null && gcG.getAssistantName().length() > 0) {
					championBuilder.addAssistantName(gcG.getAssistantName());
				}
				championBuilder.setScore(gcG.getGCompScore());
				championBuilder.setSession(i + 1);
				builder.addHistoryChampion(championBuilder.build());
			}
		}
		CommonGetDataRspMsg.Builder rspBuilder = CommonGetDataRspMsg.newBuilder();
		rspBuilder.setRstType(GCResultType.SUCCESS);
		rspBuilder.setGroupScoreRankRspData(builder.build());
		return rspBuilder.build().toByteString();
	}

	public ByteString getGroupNewestScore(Player player, CommonGetDataReqMsg request) {
		List<IReadOnlyPair<String, Integer>> list = GCompDetailInfoMgr.getInstance().getNewestScore(request.getMatchId());
		List<GroupScoreData> dataList;
		if (list.size() > 0) {
			dataList = new ArrayList<GroupScoreData>(list.size());
			for (IReadOnlyPair<String, Integer> pair : list) {
				dataList.add(GroupScoreData.newBuilder().setGroupId(pair.getT1()).setScore(pair.getT2()).build());
			}
		} else {
			dataList = Collections.emptyList();
		}
		CommonGetDataRspMsg.Builder rspBuilder = CommonGetDataRspMsg.newBuilder();
		rspBuilder.setRstType(GCResultType.SUCCESS);
		rspBuilder.addAllGroupScoreData(dataList);
		return rspBuilder.build().toByteString();
	}

	public ByteString inPrepareArea(Player player) {
		PrepareAreaMgr.getInstance().inPrepareArea(player);
		CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
		gcRsp.setRstType(GCResultType.SUCCESS);
		return gcRsp.build().toByteString();
	}

	public ByteString getFightInfoInScene(Player player, CommonGetDataReqMsg request) {
		CommonGetDataRspMsg.Builder builder = CommonGetDataRspMsg.newBuilder();
		ScoreInfoInPrepareMgr.getInstance().getFightInfoInScene(player);
		builder.setRstType(GCResultType.SUCCESS);
		return builder.build().toByteString();
	}
}