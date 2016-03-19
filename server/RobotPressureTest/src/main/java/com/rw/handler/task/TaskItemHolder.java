package com.rw.handler.task;

import java.util.List;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class TaskItemHolder {
	
	
	private SynDataListHolder<TaskItem> listHolder = new SynDataListHolder<TaskItem>(TaskItem.class);
	
	public void syn(MsgDataSyn msgDataSyn){
		listHolder.Syn(msgDataSyn);
	}
	

	public TaskItem getFinishItem(){
		List<TaskItem> itemList = listHolder.getItemList();
		TaskItem target = null;
		for (TaskItem taskItem : itemList) {
			if(taskItem.getDrawState() == 1){
				target = taskItem;
				break;
			}
		}
		return target;
		
	}
	
	
	
}
