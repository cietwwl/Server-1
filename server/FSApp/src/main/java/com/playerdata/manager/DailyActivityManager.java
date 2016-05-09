package com.playerdata.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.log.GameLog;
import com.playerdata.Player;
import com.rw.service.dailyActivity.DailyActivityHandler;
import com.rwbase.dao.task.DailyActivityCfgDAO;
import com.rwbase.dao.task.DailyFinishType;
import com.rwbase.dao.task.TableDailyActivityItemDAO;
import com.rwbase.dao.task.pojo.DailyActivityCfg;
import com.rwbase.dao.task.pojo.DailyActivityCfgEntity;
import com.rwbase.dao.task.pojo.DailyActivityData;
import com.rwbase.dao.task.pojo.DailyActivityTaskItem;

public class DailyActivityManager {

	public void onLogin(Player player) {
		DailyActivityHandler.getInstance().sendTaskList(player);
	}

	// /刷新任务红点
	public void resRed(Player player) {
		DailyActivityHandler.getInstance().sendTaskList(player);
	}

	// 从配置文件中重新刷新任务列表
	public List<DailyActivityData> getTaskListByCfg(Player player, boolean refresh) {
		DailyActivityTaskItem dailyTask = getTaskItem(player, "#getTaskListByCfg");
		if (dailyTask == null) {
			return Collections.emptyList();
		}
		List<DailyActivityCfgEntity> taskCfgList = DailyActivityCfgDAO.getInstance().getAllReadOnlyEntitys();
		List<DailyActivityData> currentList;
		// 刷新
		if (refresh) {
			currentList = new ArrayList<DailyActivityData>();
		} else {
			currentList = dailyTask.getTaskList();
		}
		boolean changed = false;
		List<Integer> firstInitTaskIds = dailyTask.getFirstIncrementTaskIds();
		// 根据开启条件将任务加入任务列表,主要是时间和等级;
		for (DailyActivityCfgEntity entity : taskCfgList) {
			DailyActivityCfg cfg = entity.getCfg();
			if (!refresh && isRemoveTask(player, cfg)) {
				continue; // 不加入已经领取过奖励的任务
			}
			if (hasNoRight(player, cfg)) {
				continue;
			}
			// 刷新的话不需要遍历检查是否存在任务，直接检查能否创建任务
			DailyActivityData tempData;
			if (refresh) {
				tempData = null;
			} else {
				tempData = getActivityDataById(player, cfg.getTaskType());
			}

			if (tempData != null) {
				// 已经存在检查是否完成
				// 检查完成条件
				if (tempData.getCanGetReward() == 0 && entity.getFinishCondition().isMatchCondition(player, tempData)) {
					tempData.setCanGetReward(1);
					changed = true;
				}
			} else {
				// 检查开启条件
				if (!entity.getStartCondition().isMatchCondition(player)) {
					continue;
				}
				DailyActivityData data = new DailyActivityData();
				data.setTaskId(cfg.getId());
				currentList.add(data);
				setInitNum(data, cfg, firstInitTaskIds);
				// 检查完成条件
				if (entity.getFinishCondition().isMatchCondition(player, data)) {
					data.setCanGetReward(1);
				}
				changed = true;
			}
		}
		if (refresh) {
			dailyTask.setTaskList(currentList);
			update(player);
		} else if (changed) {
			update(player);
		}
		return currentList;
	}

	/** 检查该配置的任务是否已经被领取(移动到remove列表 ) **/
	private boolean isRemoveTask(Player player, DailyActivityCfg cfg) {
		DailyActivityTaskItem dailyTask = getTaskItem(player, "#isRemoveTask()");
		if (dailyTask == null) {
			return false;
		}
		boolean isRemove = false;
		DailyActivityCfgDAO cfgDAO = DailyActivityCfgDAO.getInstance();
		for (DailyActivityData td : dailyTask.getRemoveTaskList()) {
			DailyActivityCfg saveDailyActivityCfg = (DailyActivityCfg) cfgDAO.getCfgById(String.valueOf(td.getTaskId()));
			if (saveDailyActivityCfg != null) {
				if (saveDailyActivityCfg.getTaskType() == cfg.getTaskType()) {
					isRemove = true;
					break;
				}
			}
			if (td.getTaskId() == cfg.getId()) {
				isRemove = true;
				break;
			}
		}
		return isRemove;
	}

	public boolean hasNoRight(Player player, DailyActivityCfg cfg) {
		return cfg.getMaxLevel() < player.getLevel() || player.getVip() < cfg.getVip() || cfg.getMaxVip() < player.getVip();
	}

	// 返回一个角色所有的任务
	public List<DailyActivityData> getAllTask(Player player) {
		return getTaskListByCfg(player, false);
	}

	public DailyActivityCfg getCfgByTaskId(Player player, int taskId) {
		DailyActivityCfg cfgById = DailyActivityCfgDAO.getInstance().getTaskCfgById(taskId);
		return cfgById;
	}

	// 凌晨5点刷新任务列表;
	public void UpdateTaskList(Player player) {
		// 凌晨5点刷新任务列表;
		RefreshTaskList(player);
	}

	private void RefreshTaskList(Player player) {
		DailyActivityTaskItem dailyTask = getTaskItem(player, "#RefreshTaskList()");
		if (dailyTask == null) {
			return;
		}
		dailyTask.getRemoveTaskList().clear();
		getTaskListByCfg(player, true);
		update(player);
	}

	public void ChangeRefreshVar() {
	}

	// 完成任务，删除指定的任务id对应的任务
	public boolean RemoveTaskById(Player player, int taskId) {
		DailyActivityTaskItem dailyTask = getTaskItem(player, "#RemoveTaskById()");
		if (dailyTask == null) {
			return false;
		}
		List<DailyActivityData> curTaskList = dailyTask.getTaskList();
		int taskCount = curTaskList.size();
		for (int i = 0; i < taskCount; i++) {
			DailyActivityData data = curTaskList.get(i);
			if (data.getTaskId() != taskId) {
				continue;
			}
			// 未完成的任务不能直接领取
			if (data.getCanGetReward() != 1) {
				return false;
			}
			dailyTask.getRemoveTaskList().add(curTaskList.get(i));
			curTaskList.remove(i);
			update(player);
			return true;
		}
		return false;
	}

	/**** 增加叠加任务的完成数量 *****/
	public void AddTaskTimesByType(Player player, int taskType, int count) {
		try {
			DailyActivityTaskItem dailyTask = getTaskItem(player, "#AddTaskTimesByType()");
			if (dailyTask == null) {
				return;
			}
			DailyActivityCfgDAO activityDAO = DailyActivityCfgDAO.getInstance();
			List<DailyActivityData> taskList = dailyTask.getTaskList();
			int size = taskList.size();
			for (int i = 0; i < size; i++) {
				DailyActivityData taskData = taskList.get(i);
				DailyActivityCfgEntity entity = activityDAO.getCfgEntity(taskData.getTaskId());
				if (entity.getCfg().getTaskType() != taskType) {
					continue;
				}
				// 保留原逻辑先
				if (!entity.getStartCondition().isMatchCondition(player)) {
					continue;
				}

				int currentProgress = taskData.getCurrentProgress();
				currentProgress += count;
				int totalProgress = entity.getTotalProgress();
				if (currentProgress > totalProgress) {
					currentProgress = totalProgress;
				}
				taskData.setCurrentProgress(currentProgress);
				// 检查是否完成任务
				if (entity.getFinishCondition().isMatchCondition(player, taskData)) {
					taskData.setCanGetReward(1);
				}
				update(player);
				DailyActivityHandler.getInstance().sendTaskList(player);
				break;
			}
		} catch (Exception e) {
			GameLog.error("DailyActivityMgr", "#AddTaskTimesByType()", "添加任务发生异常:userId=" + player.getUserId() + ",taskType=" + taskType + ",count=" + count, e);
		}
	}

	private DailyActivityData getActivityDataById(Player player, int type) {
		DailyActivityTaskItem dailyTask = getTaskItem(player, "#getActivityDataById()");
		if (dailyTask == null) {
			return null;
		}
		for (DailyActivityData td : dailyTask.getTaskList()) {
			DailyActivityCfg tempCfg = (DailyActivityCfg) DailyActivityCfgDAO.getInstance().getCfgById(String.valueOf(td.getTaskId()));
			if (tempCfg.getTaskType() == type)
				return td;
		}
		return null;
	}

	private DailyActivityTaskItem getTaskItem(Player player, String methodName) {
		String userId = player.getUserId();
		TableDailyActivityItemDAO dao = TableDailyActivityItemDAO.getInstance();
		DailyActivityTaskItem dailyTask = dao.get(userId);
		if (dailyTask == null) {
			GameLog.error("DailyActivityManager", methodName, "获取日常任务失败：" + userId);
			return null;
		} else {
			return dailyTask;
		}
	}

	private void update(Player player) {
		TableDailyActivityItemDAO.getInstance().update(player.getUserId());
	}

	private void setInitNum(DailyActivityData data, DailyActivityCfg cfg, List<Integer> firstInitTaskIds) {
		if (cfg.getTaskFinishType() == DailyFinishType.SINGLE.getType()) {
			return;
		}
		int total = parseInt(cfg.getFinishCondition(), 0);
		if (total == 0) {
			return;
		}
		int initNum = cfg.getTaskInitNum();
		if (initNum == 0) {
			return;
		}
		Integer taskId = cfg.getId();
		if (firstInitTaskIds.contains(taskId)) {
			return;
		}

		// TODO 这个判断应该在启动服务器的时候做检查而不应该运行时，临时方案~~
		if (initNum > total) {
			GameLog.error("DailyActivityMgr", "#setInitNum()", "初始数量配置大于完成所需数量：id=" + cfg.getId() + ",initNum=" + initNum + ",total=" + total);
			initNum = total;
		}
		data.setCurrentProgress(initNum);
		firstInitTaskIds.add(taskId);
	}

	/**
	 * 把文本解析成int，解析出错时返回默认值
	 * 
	 * @param text
	 * @param defualtValue
	 * @return
	 */
	public static int parseInt(String text, int defualtValue) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			return defualtValue;
		}
	}

}
