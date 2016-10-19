package com.playerdata.groupcompetition.quiz;

import java.util.List;

import com.rw.fsutil.cacheDao.DataKVDao;

public class GroupQuizEventItemDAO extends DataKVDao<GCQuizEventItem>{
	
	private static GroupQuizEventItemDAO instance = new GroupQuizEventItemDAO();

	public static GroupQuizEventItemDAO getInstance() {
		return instance;
	}

	private GroupQuizEventItemDAO() { }
	
	public GCQuizEventItem getQuizInfo(int matchId){
		return this.get(String.valueOf(matchId));
	}
	
	public void updateQuizInfo(int matchId){
		update(String.valueOf(matchId));
	}
	
	/**
	 * 删除上一届所有的记录
	 * @param matchList 所有的比赛
	 */
	public void removeAllRecord(List<Integer> matchList){
		for(int matchId : matchList){
			delete(String.valueOf(matchId));
		}
	}
}
