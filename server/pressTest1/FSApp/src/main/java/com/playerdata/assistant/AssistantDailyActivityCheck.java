package com.playerdata.assistant;

import java.util.List;

import com.playerdata.DailyActivityMgr;
import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.task.pojo.DailyActivityCfg;
import com.rwbase.dao.task.pojo.DailyActivityData;

public class AssistantDailyActivityCheck implements IAssistantCheck {

	@Override
	public AssistantEventID doCheck(Player player) {

		if (check(player)) {
			return AssistantEventID.DailyQuest;
		}
		return null;
	}

	private boolean check(Player player) {
		boolean hasTaskToDo = false;

		if(CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.DAILY, player.getLevel())){
			
			DailyActivityMgr dailyActivityMgr = player.getDailyActivityMgr();
			List<DailyActivityData> allTask = dailyActivityMgr.getAllTask();
			
			for (DailyActivityData dailyActivityData : allTask) {
				if (dailyActivityData.notFinish()) {
					
					DailyActivityCfg cfgByTaskId = dailyActivityMgr.getCfgByTaskId(dailyActivityData.getTaskId());
					// 2是功能类的任务
					if (cfgByTaskId.getTaskClassify() == 2 && !dailyActivityMgr.hasNoRight(cfgByTaskId)) {
						hasTaskToDo = true;
						break;
					}
				}
				
			}
		}
		
		
		return hasTaskToDo;
	}

}
