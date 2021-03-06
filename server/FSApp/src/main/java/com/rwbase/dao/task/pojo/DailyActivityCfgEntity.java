package com.rwbase.dao.task.pojo;

import java.util.ArrayList;
import java.util.List;

import com.rw.service.dailyActivity.Enum.DailyActivityClassifyType;
import com.rw.service.dailyActivity.Enum.DailyActivityFinishType;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.task.DailyCheckOutDateCondition;
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
	private final DailyCheckOutDateCondition checkOutDateCondition; // 检查是否已经过时的条件
	private final List<ItemInfo> reward; // 任务奖励列表
	private final int totalProgress;

	public DailyActivityCfgEntity(DailyActivityCfg taskCfg) {
		this.cfg = taskCfg;
		// 解析完成类型
		String startConditionText = taskCfg.getStartCondition();
		String finishConditionText = taskCfg.getFinishCondition();
//		DailyStartCondition dailyTimeCondition = null;
//		DailyFinishCondition dailyTimeCondition2 = null;
//		try {
//			
//			// 简单解析下
//			if (taskCfg.getTaskClassify() == DailyActivityClassifyType.Time_Type) {
//				dailyTimeCondition = new DailyTimeCondition(startConditionText);
//				dailyTimeCondition2 = new DailyTimeCondition(finishConditionText);
//			} else if (taskCfg.getTaskClassify() == DailyActivityClassifyType.Time_Card_Type) {
//				dailyTimeCondition = new DailyTimeCardCondition(startConditionText);
//				dailyTimeCondition2 = new DailyTimeCardProgressCondition();
//			} else {
//				dailyTimeCondition = new DailyLevelCondition(startConditionText);
//				dailyTimeCondition2 = new DailyProgressCondition(finishConditionText);
//			}
//			
//		} catch (Exception e) {
//			System.err.println("解析日常任务配置表有错，"
//					+ "配置表id：" + cfg.getId() + ",配置类型：" + taskCfg.getTaskClassify() + ", 开启条件：" + startConditionText);
//			System.exit(1);
//		} 
//		this.startCondition = dailyTimeCondition;
//		this.finishCondition = dailyTimeCondition2;

		
		if (taskCfg.getTaskClassify() == DailyActivityClassifyType.Time_Type) {
			startCondition = new DailyTimeCondition(startConditionText);
			finishCondition = new DailyTimeCondition(finishConditionText);
			checkOutDateCondition = new DailyTimeCheckOutDateCondition(finishConditionText);
		} else if (taskCfg.getTaskClassify() == DailyActivityClassifyType.Time_Card_Type) {
			startCondition = new DailyTimeCardCondition(startConditionText);
			finishCondition = new DailyTimeCardProgressCondition();
			checkOutDateCondition = new DailyAlwaysNotOutDateCondition();
		} else {
			startCondition = new DailyLevelCondition(startConditionText);
			finishCondition = new DailyProgressCondition(finishConditionText);
			checkOutDateCondition = new DailyAlwaysNotOutDateCondition();
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
	
	public DailyCheckOutDateCondition getCheckOutDateCondition() {
		return checkOutDateCondition;
	}

	public List<ItemInfo> getReward() {
		return reward;
	}

}
