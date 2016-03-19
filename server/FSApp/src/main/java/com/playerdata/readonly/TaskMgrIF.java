package com.playerdata.readonly;

import java.util.List;

import com.rwbase.dao.task.pojo.TaskItem;

public interface TaskMgrIF {
	public List<TaskItem> getTaskEnumeration();
}
