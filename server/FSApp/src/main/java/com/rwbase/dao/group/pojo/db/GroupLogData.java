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
		// ArrayList<GroupLog> arrayList = new ArrayList<GroupLog>();
		// for (int i = logList.size() - 1; i >= 0; --i) {
		// GroupLog groupLog = logList.get(i);
		// arrayList.add(groupLog);
		//
		// System.err.println(groupLog.toString());
		// }
		// return arrayList;
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

			int removeIndex = -1;// 要删除的索引
			long smallTime = -1;// 最小的时间
			for (int i = 0; i < size; i++) {
				GroupLog gLog = logList.get(i);
				long time = gLog.getTime();
				if (i == 0 || time < smallTime) {
					smallTime = time;
					removeIndex = i;
				}
			}

			if (removeIndex > -1) {
				logList.remove(removeIndex);
			}
		}
	}
}