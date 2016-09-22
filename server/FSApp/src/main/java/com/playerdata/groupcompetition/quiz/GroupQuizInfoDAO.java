package com.playerdata.groupcompetition.quiz;

import java.util.ArrayList;
import java.util.List;

import com.rw.fsutil.cacheDao.DataKVDao;

public class GroupQuizInfoDAO extends DataKVDao<GroupQuizInfo>{
	private static GroupQuizInfoDAO instance = new GroupQuizInfoDAO();

	public static GroupQuizInfoDAO getInstance() {
		return instance;
	}

	private GroupQuizInfoDAO() { }
	
	public GroupQuizInfo getQuizInfo(String groupId, String stageId){
		return this.get(groupId + "_" + stageId);
	}
	
	public void updateQuizInfo(String groupId, String stageId){
		update(groupId + "_" + stageId);
	}
	
	/**
	 * 删除上一届所有的记录
	 * @param groupList
	 */
	public void removeAllRecord(List<String> groupList){
		List<String> stageList = new ArrayList<String>();
		for(String stageId : stageList){
			for(String groupId : groupList){
				delete(groupId + "_" + stageId);
			}
		}
	}
}
