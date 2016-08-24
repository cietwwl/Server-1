package com.playerdata.groupcompetition.rank;

import java.util.List;

import com.bm.rank.groupCompetition.killRank.GCompKillItem;
import com.bm.rank.groupCompetition.killRank.GCompKillRankMgr;
import com.bm.rank.groupCompetition.scoreRank.GCompScoreItem;
import com.bm.rank.groupCompetition.scoreRank.GCompScoreRankMgr;
import com.bm.rank.groupCompetition.winRank.GCompContinueWinItem;
import com.bm.rank.groupCompetition.winRank.GCompContinueWinRankMgr;


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
	public void stageEnd(GCompFightStage stage){
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
		for(GCompFightStage stage : GCompFightStage.values()){
			for(GCompRankType rankType : GCompRankType.values()){
				GCompRankReordDAO.getInstance().delete(getRecordId(stage, rankType));
			}
		}
	}
	
	/**
	 * 获取击杀排行榜
	 * @param stageId
	 * @return 
	 */
	public List<GCompKillItem> getKillRank(GCompFightStage stage){
		if(getCurrentStage() == stage){
			return GCompKillRankMgr.getKillRankList();
		}
		GCompRankReordData record = GCompRankReordDAO.getInstance().get(getRecordId(stage, GCompRankType.Kill));
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
	public List<GCompContinueWinItem> getWinRank(GCompFightStage stage){
		if(getCurrentStage() == stage){
			return GCompContinueWinRankMgr.getContinueWinRankList();
		}
		GCompRankReordData record = GCompRankReordDAO.getInstance().get(getRecordId(stage, GCompRankType.Win));
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
	public List<GCompScoreItem> getScoreRank(GCompFightStage stage){
		if(getCurrentStage() == stage){
			return GCompScoreRankMgr.getScoreRankList();
		}
		GCompRankReordData record = GCompRankReordDAO.getInstance().get(getRecordId(stage, GCompRankType.Score));
		if(null == record){
			return null;
		}
		return record.getScoreRecord();
	}
	
	public String getRecordId(GCompFightStage stage, GCompRankType type){
		return stage.toString() + "_" + type.toString();
	}
	
	public GCompFightStage getCurrentStage(){
		//TODO 需要另外实现
		return GCompFightStage.Final;
	}
}
