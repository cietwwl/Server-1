package com.rwbase.dao.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.Player;
import com.rwbase.dao.task.pojo.DailyActivityCfgEntity;
import com.rwbase.dao.task.pojo.DailyActivityData;
import com.rwbase.dao.task.pojo.DailyActivityTaskItem;

public class DailyActivityHolder {

	private final String userId;
	private TableDailyActivityItemDAO dao = TableDailyActivityItemDAO.getInstance();

	public DailyActivityHolder(Player playerP) {
		this.userId = playerP.getUserId();
	}

	public DailyActivityTaskItem getTaskItem() {
		return dao.get(userId);
	}

	/**
	 * 获取日常任务列表
	 * 
	 * @return
	 */
	public List<DailyActivityData> getTaskList() {
		DailyActivityTaskItem taskItem = getTaskItem();
		if (taskItem == null) {
			return Collections.emptyList();
		}

		List<DailyActivityData> taskList = taskItem.getTaskList();
		if (taskList == null || taskList.isEmpty()) {
			return Collections.emptyList();
		}

		DailyActivityCfgDAO cfgDAO = DailyActivityCfgDAO.getInstance();

		List<DailyActivityData> list = new ArrayList<DailyActivityData>();
		for (int i = taskList.size() - 1; i >= 0; --i) {
			DailyActivityData data = taskList.get(i);
			if (data == null) {
				continue;
			}

			int taskId = data.getTaskId();
			DailyActivityCfgEntity cfg = cfgDAO.getCfgEntity(taskId);
			if (cfg == null) {
				continue;
			}

			list.add(data);
		}
		return list;
	}

	public void save() {
		dao.update(userId);
	}

	public String getUserId() {
		return userId;
	}
}