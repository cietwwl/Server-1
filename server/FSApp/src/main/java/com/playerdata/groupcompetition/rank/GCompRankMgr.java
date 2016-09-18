package com.playerdata.groupcompetition.rank;

import java.util.List;

import com.bm.rank.groupCompetition.GCompRankDataIF;
import com.bm.rank.groupCompetition.killRank.GCompKillItem;
import com.bm.rank.groupCompetition.killRank.GCompKillRankMgr;
import com.bm.rank.groupCompetition.scoreRank.GCompScoreItem;
import com.bm.rank.groupCompetition.scoreRank.GCompScoreRankMgr;
import com.bm.rank.groupCompetition.winRank.GCompContinueWinItem;
import com.bm.rank.groupCompetition.winRank.GCompContinueWinRankMgr;
import com.playerdata.Player;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.holder.GCompMemberMgr;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.rwbase.dao.groupcompetition.pojo.UserGroupCompetitionScoreRecord;
import com.rwproto.GroupCompetitionProto.GCompRankItem;
import com.rwproto.GroupCompetitionProto.CommonGetDataRspMsg.Builder;


public class GCompRankMgr {
	
	private static GCompRankMgr instance = new GCompRankMgr();

	public static GCompRankMgr getInstance() {
		return instance;
	}
	
	/**
	 * 争霸赛某个阶段结束，需要保存阶段的排行榜
	 * 并清空当前的排行榜
	 * @param stageId
	 */
	public void stageEnd(GCEventsType stage){
		List<GCompKillItem> killRank = GCompKillRankMgr.stageEnd();
		GCompRankReordData killRecord = new GCompRankReordData();
		killRecord.setRankID(getRecordId(stage, GCompRankType.Kill));
		killRecord.setKillRecord(killRank);
		GCompRankReordDAO.getInstance().update(killRecord);
		
		List<GCompScoreItem> scoreRank = GCompScoreRankMgr.stageEnd();
		GCompRankReordData scoreRecord = new GCompRankReordData();
		scoreRecord.setRankID(getRecordId(stage, GCompRankType.Score));
		scoreRecord.setScoreRecord(scoreRank);
		GCompRankReordDAO.getInstance().update(scoreRecord);
		
		List<GCompContinueWinItem> winRank = GCompContinueWinRankMgr.stageEnd();
		GCompRankReordData winRecord = new GCompRankReordData();
		winRecord.setRankID(getRecordId(stage, GCompRankType.Win));
		winRecord.setWinRecord(winRank);
		GCompRankReordDAO.getInstance().update(winRecord);
	}
	
	/**
	 * 争霸赛开始时，需要清空争霸赛的历史排行榜
	 */
	public void competitionStart(){
		for(GCEventsType stage : GCEventsType.values()){
			for(GCompRankType rankType : GCompRankType.values()){
				GCompRankReordDAO.getInstance().delete(getRecordId(stage, rankType));
			}
		}
		GCompKillRankMgr.clearRank();
		GCompScoreRankMgr.clearRank();
		GCompContinueWinRankMgr.clearRank();
	}
	
	/**
	 * 获取击杀排行榜
	 * @param stageId
	 * @return 
	 */
	public List<GCompKillItem> getKillRank(GCEventsType currentEvent){
		if(getCurrentStage() == currentEvent){
			return GCompKillRankMgr.getKillRankList();
		}
		GCompRankReordData record = GCompRankReordDAO.getInstance().get(getRecordId(currentEvent, GCompRankType.Kill));
		if(null == record){
			return null;
		}
		return record.getKillRecord();
	}
	
	/**
	 * 获取连胜排行榜
	 * @param stageId
	 * @return 
	 */
	public List<GCompContinueWinItem> getWinRank(GCEventsType currentEvent){
		if(getCurrentStage() == currentEvent){
			return GCompContinueWinRankMgr.getContinueWinRankList();
		}
		GCompRankReordData record = GCompRankReordDAO.getInstance().get(getRecordId(currentEvent, GCompRankType.Win));
		if(null == record){
			return null;
		}
		return record.getWinRecord();
	}

	/**
	 * 获取积分排行榜
	 * @param stageId
	 * @return 
	 */
	public List<GCompScoreItem> getScoreRank(GCEventsType currentEvent){
		if(getCurrentStage() == currentEvent){
			return GCompScoreRankMgr.getScoreRankList();
		}
		GCompRankReordData record = GCompRankReordDAO.getInstance().get(getRecordId(currentEvent, GCompRankType.Score));
		if(null == record){
			return null;
		}
		return record.getScoreRecord();
	}
	
	public String getRecordId(GCEventsType stage, GCompRankType type){
		return stage.toString() + "_" + type.toString();
	}
	
	public GCEventsType getCurrentStage(){
		if(GroupCompetitionMgr.getInstance().isCurrentEventsEnd()) return null;
		return GroupCompetitionMgr.getInstance().getCurrentEventsType();
	}

	public void getKillRank(Player player, Builder builder, GCEventsType eventsType) {
		List<GCompKillItem> killRank = getKillRank(eventsType);
		if(null == killRank){
			return;
		}
		for(GCompKillItem item : killRank){
			builder.addRankData(toClientRankItem(item));
		}
		getSelfRankItem(player, builder, eventsType, GCompRankType.Kill);
	}
	
	public void getWinRank(Player player, Builder builder, GCEventsType eventsType) {
		List<GCompContinueWinItem> winRank = getWinRank(eventsType);
		if(null == winRank){
			return;
		}
		for(GCompContinueWinItem item : winRank){
			builder.addRankData(toClientRankItem(item));
		}
		getSelfRankItem(player, builder, eventsType, GCompRankType.Win);
	}
	
	public void getScoreRank(Player player, Builder builder, GCEventsType eventsType) {
		List<GCompScoreItem> scoreRank = getScoreRank(eventsType);
		if(null == scoreRank){
			return;
		}
		for(GCompScoreItem item : scoreRank){
			builder.addRankData(toClientRankItem(item));
		}
		getSelfRankItem(player, builder, eventsType, GCompRankType.Score);
	}
	
	/**
	 * 将排行榜数据转成protobuff结构
	 * @param rankData
	 * @return
	 */
	private GCompRankItem toClientRankItem(GCompRankDataIF rankData){
		GCompRankItem.Builder builder = GCompRankItem.newBuilder();
		builder.setGroupName(rankData.getGroupName());
		builder.setHeadImage(rankData.getHeadImage());
		builder.setUserId(rankData.getUserId());
		builder.setUserName(rankData.getUserName());
		builder.setValue(rankData.getValue());
		return builder.build();
	}
	
	/**
	 * 获取个人的排行
	 * @param player
	 * @param builder
	 * @param eventsType
	 * @param rankType
	 */
	private void getSelfRankItem(Player player, Builder builder, GCEventsType eventsType, GCompRankType rankType){
		UserGroupCompetitionScoreRecord gcRecord = GCompMemberMgr.getInstance().getRecord(player.getUserId(), eventsType);
		if(null == gcRecord) return;
		GCompRankItem.Builder selfRankbuilder = GCompRankItem.newBuilder();
		selfRankbuilder.setGroupName(gcRecord.getGroupName());
		selfRankbuilder.setHeadImage(player.getHeadImage());
		selfRankbuilder.setUserId(player.getUserId());
		selfRankbuilder.setUserName(player.getUserName());
		switch (rankType) {
		case Kill:
			selfRankbuilder.setValue(gcRecord.getTotalWinTimes());
			break;
		case Score:
			selfRankbuilder.setValue(gcRecord.getScore());
			break;
		case Win:
			selfRankbuilder.setValue(gcRecord.getMaxContinueWins());
			break;
		default:
			break;
		}
		builder.setSelfRankData(selfRankbuilder.build());
	}
}
