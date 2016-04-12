package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;
import com.rwbase.dao.task.pojo.TaskItem;

public class TaskColector implements RedPointCollector{

	@Override
	public void fillRedPoints(Player player,
			Map<RedPointType, List<String>> map) {
		int level = player.getLevel();
		CfgOpenLevelLimit taskOpenLevel = (CfgOpenLevelLimit) CfgOpenLevelLimitDAO.getInstance().getCfgById(String.valueOf(eOpenLevelType.TASK.getOrder()));
		if (taskOpenLevel == null || level >= taskOpenLevel.getMinLevel()) {
			// 检查任务
			List<TaskItem> taskEnumeration = player.getTaskMgr().getTaskEnumeration();
			boolean hasDrawState = false;
			for (TaskItem taskItem : taskEnumeration) {
				if (taskItem.getDrawState() == 1) {
					hasDrawState = true;
					break;
				}
			}
			if (hasDrawState) {
				map.put(RedPointType.HOME_WINDOW_TASK, Collections.EMPTY_LIST);
			}
		}
	}

}
