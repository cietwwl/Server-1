package com.playerdata.groupcompetition.rank;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.dataSyn.sameSceneSyn.DataAutoSynMgr;
import com.playerdata.groupcompetition.holder.GCompMemberMgr;
import com.playerdata.groupcompetition.holder.data.GCompMember;
import com.playerdata.groupcompetition.prepare.PrepareAreaMgr;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;


public class ScoreInfoInPrepareMgr {
	
	private static ScoreInfoInPrepareMgr instance = new ScoreInfoInPrepareMgr();

	public static ScoreInfoInPrepareMgr getInstance() {
		return instance;
	}
	
	/**
	 * 在备战区界面，请求战斗积分等数据
	 * @param player
	 * @param latestTime
	 */
	public void getFightInfoInScene(Player player) {
		String groupId = GroupHelper.getUserGroupId(player.getUserId());
		Long sceneId = PrepareAreaMgr.getInstance().getGroupScene(groupId);
		if(null == sceneId){
			return;
		}
		List<GCompMixRankData> rankDatas = new ArrayList<GCompMixRankData>();
		List<GCompMember> gcMemsInfo = GCompMemberMgr.getInstance().getArrayCopyOfAllMembers(groupId);
		for(GCompMember mem : gcMemsInfo){
			if(mem.getScore() == 0 && mem.getMaxContinueWins() == 0 && mem.getTotalWinTimes() == 0) continue;
			GCompMixRankData data = new GCompMixRankData(mem.getUserId(), mem.getUserName(), mem.getScore(), mem.getTotalWinTimes(), mem.getMaxContinueWins());
			rankDatas.add(data);
		}
		if(!rankDatas.isEmpty()){
			ClientDataSynMgr.synDataList(player, rankDatas, eSynType.GCompFightInfoInScene, eSynOpType.UPDATE_LIST);
		}
	}
	
	/**
	 * 有战斗数据更新的时候，给备战区的人同步
	 * @param player
	 */
	public void updateNewScoreRecord(Player player){
		String groupId = GroupHelper.getUserGroupId(player.getUserId());
		Long sceneId = PrepareAreaMgr.getInstance().getGroupScene(groupId);
		if(null == sceneId){
			return;
		}
		GCompMember groupMember = GCompMemberMgr.getInstance().getGCompMember(groupId, player.getUserId());
		if(null == groupMember){
			return;
		}
		GCompMixRankData data = new GCompMixRankData(groupMember.getUserId(), groupMember.getUserName(), groupMember.getScore(), groupMember.getTotalWinTimes(), groupMember.getMaxContinueWins());
		DataAutoSynMgr.getInstance().synDataToPlayersInScene(sceneId, eSynType.GCompFightInfoInScene, data);
	}
}
