package com.playerdata.groupcompetition.service;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.groupCompetition.groupRank.GCompFightingItem;
import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.holder.GCompDetailInfoMgr;
import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.holder.GCompSelectionDataMgr;
import com.playerdata.groupcompetition.holder.GCompTeamMgr;
import com.playerdata.groupcompetition.prepare.PrepareAreaMgr;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.playerdata.groupcompetition.util.GCompTips;
import com.playerdata.teambattle.data.TeamMember;
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
import com.rwproto.GroupCompetitionProto.JoinTeamReq;
import com.rwproto.GroupCompetitionProto.SelectionGroupData;
import com.rwproto.GroupCompetitionProto.SelectionRspData;
import com.rwproto.GroupCompetitionProto.TeamInvitation;
import com.rwproto.GroupCompetitionProto.TeamMemberRequest;
import com.rwproto.GroupCompetitionProto.TeamRequest;
import com.rwproto.MsgDef.Command;

public class GroupCompetitionHandler {

	private static GroupCompetitionHandler instance;	

	private GroupCompetitionHandler() {
	}

	public static GroupCompetitionHandler getInstance() {
		if (instance == null) {
			instance = new GroupCompetitionHandler();
		}
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
		groupDataBuilder.setUpNum(fightingItem.getLastRank() - ranking); // ranking比lastRank小，则是上升，反之则是下降
		return groupDataBuilder.build();
	}
	
	private SelectionGroupData createOwnGroupData(Player player, List<GCompFightingItem> gCompFightingItemList) {
		Group ownGroup = GroupHelper.getGroup(player);
		SelectionGroupData ownGroupData;
		if (ownGroup != null) {
			GroupBaseDataIF baseData = ownGroup.getGroupBaseDataMgr().getGroupData();
			int index = GCompFightingRankMgr.getRankIndex(baseData.getGroupId());
			if (index > 0) {
				// 在榜单中
				GCompFightingItem fightingItem;
				if (index < gCompFightingItemList.size()) {
					fightingItem = gCompFightingItemList.get(index);
				} else {
					fightingItem = GCompFightingRankMgr.getFightingRankList().get(index);
				}
				ownGroupData = this.createSelectionGroupData(fightingItem, index);
			} else {
				// 不在榜中
				SelectionGroupData.Builder ownGroupBuilder = SelectionGroupData.newBuilder();
				ownGroupBuilder.setName(baseData.getGroupName());
				ownGroupBuilder.setUpNum(0);
				ownGroupBuilder.setRanking(index);
				ownGroupBuilder.setFighting(this.calculateGroupFighting(ownGroup));
				ownGroupData = ownGroupBuilder.build();
			}
		} else {
			ownGroupData = null;
		}
		return ownGroupData;
	}
	
	private void packSelectionData(CommonGetDataRspMsg.Builder rspBuilder, Player player) {
		List<GCompFightingItem> list = GCompFightingRankMgr.getFightingRankList(20);
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
		builder.setReqType(GCRequestType.GetSelectionData);
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
	
	// 获取海选信息
	public ByteString getSelectionData(Player player) {
		GCResultType resultType = this.checkStageValidate(GCompStageType.SELECTION);
		CommonGetDataRspMsg.Builder builder = this.createGetDataRspBuilder(resultType, resultType == GCResultType.DATA_ERROR ? GCompTips.getTipsNotSelectionStageNow() : null);
		if (resultType == GCResultType.SUCCESS) {
			this.packSelectionData(builder, player);
		}
		GCompSelectionDataMgr.getInstance().sendLastMatchData(player);
		return builder.build().toByteString();
	}
	
	// 获取赛事对阵
	public ByteString getMatchData(Player player) {
		GCResultType resultType = this.checkStageValidate(GCompStageType.EVENTS);
		CommonGetDataRspMsg.Builder builder = this.createGetDataRspBuilder(resultType, resultType == GCResultType.DATA_ERROR ? GCompTips.getTipsNotMatchStageNow() : null);
		if (resultType == GCResultType.SUCCESS) {
			// 成功才有数据
			GCompEventsDataMgr.getInstance().sendMatchData(player);
		}
		return builder.build().toByteString();
	}
	
	public ByteString getMatchDetailInfo(Player player, CommonGetDataReqMsg request) {
		boolean success = GCompDetailInfoMgr.getInstance().sendDetailInfo(request.getMatchId(), player);
		GCResultType resultType ;
		if(success) {
			resultType = GCResultType.SUCCESS;
		} else {
			resultType = GCResultType.DATA_ERROR;
		}
		CommonGetDataRspMsg.Builder builder = this.createGetDataRspBuilder(resultType, resultType == GCResultType.DATA_ERROR ? GCompTips.getTipsNoMatchDetailData() : null);
		return builder.build().toByteString();
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
		case CreateTeam:
			processResult = GCompTeamMgr.getInstance().createTeam(player, teamRequest.getHeroIdList());
			break;
		case AdjustTeamMember:
			processResult = GCompTeamMgr.getInstance().updateHeros(player, teamRequest.getHeroIdList());
			break;
		default:
			return ByteString.EMPTY;
		}
		return this.createCommonRsp(processResult.getT1() ? GCResultType.SUCCESS : GCResultType.DATA_ERROR, processResult.getT2()).toByteString();
	}
	
	/**
	 * 
	 * 处理玩家请求加入队伍
	 * 
	 * @param player
	 * @param joinRequest
	 * @return
	 */
	public ByteString joinTeamRequest(Player player, JoinTeamReq joinRequest) {
		IReadOnlyPair<Boolean, String> processResult = GCompTeamMgr.getInstance().joinTeam(player, joinRequest.getTeamId(), joinRequest.getHeroIdList());
		return this.createCommonRsp(processResult.getT1() ? GCResultType.SUCCESS : GCResultType.DATA_ERROR, processResult.getT2()).toByteString();
	}
	
	private ByteString inviteMember(Player player, TeamMemberRequest request) {
		Player targetPlayer = PlayerMgr.getInstance().find(request.getTargetUserId());
		IReadOnlyPair<Boolean, String> processResult = GCompTeamMgr.getInstance().inviteMember(player, targetPlayer);
		return this.createCommonRsp(processResult.getT1() ? GCResultType.SUCCESS : GCResultType.DATA_ERROR, processResult.getT2()).toByteString();
	}
	
	private ByteString kickMember(Player player, TeamMemberRequest request) {
		return null;
	}
	
	public ByteString teamMemberRequest(Player player, TeamMemberRequest request) {
		switch (request.getReqType()) {
		case InviteMember:
			return inviteMember(player, request);
		case KickMember:
			return this.kickMember(player, request);
		default:
			return ByteString.EMPTY;
		}
	}
}