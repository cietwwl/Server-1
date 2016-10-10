package com.playerdata.groupcompetition.rank;

import com.playerdata.Player;
import com.playerdata.dataSyn.sameSceneSyn.DataAutoSynMgr;
import com.playerdata.groupcompetition.holder.GCompMemberMgr;
import com.playerdata.groupcompetition.prepare.PrepareAreaMgr;
import com.rw.service.group.helper.GroupHelper;
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
	public void getFightInfoInScene(Player player, long latestTime) {
		String groupId = GroupHelper.getUserGroupId(player.getUserId());
		Long sceneId = PrepareAreaMgr.getInstance().getGroupScene(groupId);
		if(null == sceneId){
			return;
		}
		GCompMemberMgr.getInstance().getArrayCopyOfAllMembers(groupId);
		
		
		
		
	}
	
	public void updateNewScoreRecord(GCompMixRankData mixRankData){
		String groupId = GroupHelper.getUserGroupId(mixRankData.getUserId());
		Long sceneId = PrepareAreaMgr.getInstance().getGroupScene(groupId);
		if(null == sceneId){
			return;
		}
		DataAutoSynMgr.getInstance().synDataToPlayersInScene(sceneId, eSynType.GCompFightInfoInScene, mixRankData);
	}
}
