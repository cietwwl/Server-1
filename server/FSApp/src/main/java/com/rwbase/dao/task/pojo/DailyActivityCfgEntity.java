package com.rwbase.dao.task.pojo;

import java.util.ArrayList;
import java.util.List;

import com.rw.service.dailyActivity.Enum.DailyActivityClassifyType;
import com.rw.service.dailyActivity.Enum.DailyActivityFinishType;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.task.DailyStartCondition;

/**
 * 任务配置类实体，避免每次循环调用任务配置检查时间大量的切割字符串操作
 * 
 * @author Jamaz
 *
 */
public class DailyActivityCfgEntity {

	private final DailyActivityCfg cfg;
	private final DailyStartCondition startCondition; // 任务开启条件
	private final DailyFinishCondition finishCondition; // 任务完成条件
	private final List<ItemInfo> reward; // 任务奖励列表
	private final int totalProgress;

	public DailyActivityCfgEntity(DailyActivityCfg taskCfg) {
		this.cfg = taskCfg;
		// 解析完成类型
		String startConditionText = taskCfg.getStartCondition();
		String finishConditionText = taskCfg.getFinishCondition();
		// 简单解析下
		if (taskCfg.getTaskClassify() == DailyActivityClassifyType.Time_Type) {
			this.startCondition = new DailyTimeCondition(startConditionText);
			this.finishCondition = new DailyTimeCondition(finishConditionText);
		} else if (taskCfg.getTaskClassify() == DailyActivityClassifyType.Time_Card_Type) {
			this.startCondition = new DailyTimeCardCondition(startConditionText);
			this.finishCondition = new DailyTimeCardProgressCondition();
		} else {
			this.startCondition = new DailyLevelCondition(startConditionText);
			this.finishCondition = new DailyProgressCondition(finishConditionText);
		}
		
		String[] reward = taskCfg.getReward().split(";");
		this.reward = new ArrayList<ItemInfo>(reward.length);
		for (int i = 0; i < reward.length; i++) {
			String[] rewardItem = reward[i].split(":");
			ItemInfo info = new ItemInfo();
			info.setItemID(Integer.parseInt(rewardItem[0]));
			info.setItemNum(Integer.parseInt(rewardItem[1]));
			this.reward.add(info);
		}
		if (taskCfg.getTaskFinishType() == DailyActivityFinishType.Many_Time) {
			totalProgress = Integer.parseInt(finishConditionText);
		} else {
			totalProgress = 1;
		}
	}

	public DailyActivityCfg getCfg() {
		return cfg;
	}

	public int getTotalProgress() {
		return totalProgress;
	}

	public DailyStartCondition getStartCondition() {
		return startCondition;
	}

	public DailyFinishCondition getFinishCondition() {
		return finishCondition;
	}

	public List<ItemInfo> getReward() {
		return reward;
	}

}
