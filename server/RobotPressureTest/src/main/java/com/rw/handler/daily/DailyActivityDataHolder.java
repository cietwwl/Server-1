package com.rw.handler.daily;

import java.util.ArrayList;
import java.util.List;

import com.rwproto.DailyActivityProtos.DailyActivityInfo;

public class DailyActivityDataHolder {
	List<DailyActivityInfo> taskList = new ArrayList<DailyActivityInfo>();
	int num ;
	public List<DailyActivityInfo> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<DailyActivityInfo> taskList) {
		this.taskList = taskList;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	
	
}
