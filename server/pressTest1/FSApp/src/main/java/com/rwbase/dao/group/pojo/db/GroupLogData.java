package com.rwbase.dao.group.pojo.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import com.rwbase.dao.group.pojo.cfg.dao.GroupConfigCfgDAO;

/*
 * @author HC
 * @date 2016年1月26日 上午11:03:34
 * @Description 帮派日志
 */
@Table(name = "group_log")
public class GroupLogData {
	@Id
	private String groupId;// 帮派Id
	private List<GroupLog> logList;// 日志的列表

	public GroupLogData() {
		logList = new ArrayList<GroupLog>();
	}

	public GroupLogData(String groupId) {
		this();
		this.groupId = groupId;
	}

	public String getGroupId() {
		return groupId;
	}

	/**
	 * 获取日志的列表
	 * 
	 * @return
	 */
	public List<GroupLog> getLogList() {
		return new ArrayList<GroupLog>(logList);
	}

	/**
	 * 增加帮派日志
	 * 
	 * @param log
	 */
	public synchronized void addLog(GroupLog log) {
		int groupLogMaxSize = GroupConfigCfgDAO.getDAO().getGroupLogMaxCacheSize();
		logList.add(log);

		int size = logList.size();
		if (size > groupLogMaxSize) {
			logList.remove(0);
		}
	}
}