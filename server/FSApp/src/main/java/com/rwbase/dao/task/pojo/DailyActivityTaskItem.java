package com.rwbase.dao.task.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_table_daily_activity")
@SynClass
public class DailyActivityTaskItem implements IMapItem {
	@Id
	private String userId;
	private List<DailyActivityData> taskList = new ArrayList<DailyActivityData>();
	private List<DailyActivityData> removeTaskList = new ArrayList<DailyActivityData>();
	private List<Integer> firstIncrementTaskIds = new ArrayList<Integer>();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<DailyActivityData> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<DailyActivityData> taskList) {
		this.taskList = taskList;
	}

	public List<DailyActivityData> getRemoveTaskList() {
		return removeTaskList;
	}

	public void setRemoveTaskList(List<DailyActivityData> removeTaskList) {
		this.removeTaskList = removeTaskList;
	}

	public List<Integer> getFirstIncrementTaskIds() {
		return firstIncrementTaskIds;
	}

	public void setFirstIncrementTaskIds(List<Integer> firstIncrementTaskIds) {
		this.firstIncrementTaskIds = firstIncrementTaskIds;
	}

	@Override
	public String getId() {
		return userId;
	}
}
