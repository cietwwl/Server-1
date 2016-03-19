package com.common;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;

public class TimeAction {

	private List<TimeActionTask> taskList = new ArrayList<TimeActionTask>();
	
	private String id;
	
	public TimeAction(String id){
		this.id = id;
	}
	
	public void addTask(TimeActionTask task){
		taskList.add(task);		
	}
	
	public void doAction(){
		for (TimeActionTask timeActionTask : taskList) {
			try {
				timeActionTask.doTask();
			} catch (Throwable e) {
				GameLog.error(LogModule.TimeAction, id, "TimeAction[doAction] error", e);
			}
		}
		
		
	}
}
