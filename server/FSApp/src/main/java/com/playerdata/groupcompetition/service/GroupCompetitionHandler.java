package com.playerdata.groupcompetition.service;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.groupCompetition.groupRank.GCompFightingItem;
import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.holder.GCompBaseInfoMgr;
import com.playerdata.groupcompetition.holder.GCompSelectionDataMgr;
import com.playerdata.groupcompetition.prepare.PrepareAreaMgr;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.playerdata.groupcompetition.util.GCompTips;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwproto.GroupCompetitionProto.CommonGetDataRspMsg;
import com.rwproto.GroupCompetitionProto.CommonReqMsg;
import com.rwproto.GroupCompetitionProto.CommonRspMsg;
import com.rwproto.GroupCompetitionProto.GCRequestType;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.GroupCompetitionProto.SelectionGroupData;
import com.rwproto.GroupCompetitionProto.SelectionRspData;

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
	
	// 获取海选信息
	public ByteString getSelectionData(Player player) {
		GCompStageType stageType = GCompBaseInfoMgr.getInstance().getCurrentStageType();
		String tips;
		GCResultType resultType;
		if (stageType != GCompStageType.SELECTION) {
			resultType = GCResultType.DATA_ERROR;
			tips = GCompTips.getTipsNotSelectionStageNow();
		} else {
			resultType = GCResultType.SUCCESS;
			tips = null;
		}
		CommonGetDataRspMsg.Builder builder = CommonGetDataRspMsg.newBuilder();
		builder.setReqType(GCRequestType.GetSelectionData);
		builder.setRstType(resultType);
		if (tips != null) {
			builder.setTipMsg(tips);
		}
		if (resultType == GCResultType.SUCCESS) {
			this.packSelectionData(builder, player);
		}
		GCompSelectionDataMgr.getInstance().sendLastMatchData(player);
		return builder.build().toByteString();
	}
}