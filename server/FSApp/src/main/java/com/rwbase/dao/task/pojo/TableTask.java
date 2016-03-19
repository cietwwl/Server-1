package com.rwbase.dao.task.pojo;

import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_table_task")
public class TableTask {
	@Id
	private String userId;
	private ConcurrentHashMap<Integer, Task> taskList = new ConcurrentHashMap<Integer, Task>();
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public ConcurrentHashMap<Integer, Task> getTaskList() {
		return taskList;
	}
	public void setTaskList(ConcurrentHashMap<Integer, Task> taskList) {
		this.taskList = taskList;
	}
}
