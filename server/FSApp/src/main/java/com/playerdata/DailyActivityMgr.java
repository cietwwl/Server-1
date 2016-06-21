package com.playerdata;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.playerdata.common.PlayerEventListener;
import com.rw.service.dailyActivity.DailyActivityHandler;
import com.rwbase.dao.task.DailyActivityCfgDAO;
import com.rwbase.dao.task.DailyActivityHolder;
import com.rwbase.dao.task.DailyFinishType;
import com.rwbase.dao.task.pojo.DailyActivityCfg;
import com.rwbase.dao.task.pojo.DailyActivityCfgEntity;
import com.rwbase.dao.task.pojo.DailyActivityData;

/**
 * 任务的数据管理类。
 * */
public class DailyActivityMgr implements PlayerEventListener {

	private Player player = null;

	private DailyActivityHolder holder;

	// 初始化
	public void init(Player playerP) {
		player = playerP;
		holder = new DailyActivityHolder(playerP);
	}

	@Override
	public void notifyPlayerCreated(Player player) {
	}

	@Override
	public void notifyPlayerLogin(Player player) {
	}

	public void onLogin() {
		DailyActivityHandler.getInstance().sendTaskList(player);
	}

	// /刷新任务红点
	public void resRed() {
		DailyActivityHandler.getInstance().sendTaskList(player);
	}

	// 从配置文件中重新刷新任务列表
	public List<DailyActivityData> getTaskListByCfg(boolean refresh) {
		DailyActivityCfgDAO cfgDAO = DailyActivityCfgDAO.getInstance();
		List<DailyActivityCfgEntity> taskCfgList = cfgDAO.getAllReadOnlyEntitys();
		List<DailyActivityData> currentList;
		// 刷新
		if (refresh) {
			currentList = new ArrayList<DailyActivityData>();
		} else {
			currentList = holder.getTaskItem().getTaskList();
		}
		boolean changed = false;
		List<Integer> firstInitTaskIds = holder.getTaskItem().getFirstIncrementTaskIds();
		// 根据开启条件将任务加入任务列表,主要是时间和等级;
		for (DailyActivityCfgEntity entity : taskCfgList) {
			DailyActivityCfg cfg = entity.getCfg();
			if (!refresh && isRemoveTask(cfg)) {
				continue; // 不加入已经领取过奖励的任务
			}
			if (hasNoRight(cfg)) {
				continue;
			}
			// 刷新的话不需要遍历检查是否存在任务，直接检查能否创建任务
			DailyActivityData tempData;
			if (refresh) {
				tempData = null;
			} else {
				tempData = getActivityDataById(cfg.getTaskType());
			}

			if (tempData != null) {
				// 已经存在检查是否完成
				// 检查完成条件
				boolean matchCondition = entity.getFinishCondition().isMatchCondition(player, tempData);
				if (tempData.getCanGetReward() == 0) {
					if (matchCondition) {
						tempData.setCanGetReward(1);
						changed = true;
					}
				} else if (!matchCondition) {
					// 不符合条件，删除任务
					for (int i = currentList.size(); --i >= 0;) {
						DailyActivityData data = currentList.get(i);
						if (data.getTaskId() == tempData.getTaskId()) {
							currentList.remove(i);
							changed = true;
							break;
						}
					}
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
			holder.getTaskItem().setTaskList(currentList);
			holder.save();
		} else if (changed) {
			holder.save();
		}

		// TODO HC 临时打个 补丁，用来解决日常任务被删除了某个配置之后，导致还出现的Bug
		List<DailyActivityData> list = new ArrayList<DailyActivityData>(currentList.size());
		for (int i = currentList.size() - 1; i >= 0; --i) {
			DailyActivityData data = currentList.get(i);
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

	/** 检查该配置的任务是否已经被领取(移动到remove列表 ) **/
	private boolean isRemoveTask(DailyActivityCfg cfg) {
		boolean isRemove = false;
		for (DailyActivityData td : holder.getTaskItem().getRemoveTaskList()) {
			DailyActivityCfg saveDailyActivityCfg = (DailyActivityCfg) DailyActivityCfgDAO.getInstance().getCfgById(String.valueOf(td.getTaskId()));
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

	public boolean hasNoRight(DailyActivityCfg cfg) {
		return cfg.getMaxLevel() < player.getLevel() || player.getVip() < cfg.getVip() || cfg.getMaxVip() < player.getVip();
	}

	// 返回一个角色所有的任务
	public List<DailyActivityData> getAllTask() {
		return getTaskListByCfg(false);
	}

	public DailyActivityCfg getCfgByTaskId(int taskId) {
		DailyActivityCfg cfgById = DailyActivityCfgDAO.getInstance().getTaskCfgById(taskId);
		return cfgById;
	}

	// 凌晨5点刷新任务列表;
	public void UpdateTaskList() {
		// 凌晨5点刷新任务列表;
		RefreshTaskList();
	}

	private void RefreshTaskList() {
		holder.getTaskItem().setUserId(player.getUserId());
		holder.getTaskItem().getRemoveTaskList().clear();
		getTaskListByCfg(true);
		holder.save();
	}

	public void ChangeRefreshVar() {
	}

	// 完成任务，删除指定的任务id对应的任务
	public boolean RemoveTaskById(int taskId) {
		List<DailyActivityData> curTaskList = holder.getTaskItem().getTaskList();
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
			holder.getTaskItem().getRemoveTaskList().add(curTaskList.get(i));
			curTaskList.remove(i);
			holder.save();
			return true;
		}
		return false;
	}

	/**** 增加叠加任务的完成数量 *****/
	public void AddTaskTimesByType(int taskType, int count) {
		try {
			DailyActivityCfgDAO activityDAO = DailyActivityCfgDAO.getInstance();
			List<DailyActivityData> taskList = holder.getTaskItem().getTaskList();
			int size = taskList.size();
			for (int i = 0; i < size; i++) {
				DailyActivityData taskData = taskList.get(i);
				DailyActivityCfgEntity entity = activityDAO.getCfgEntity(taskData.getTaskId());
				if (entity == null) {
					continue;
				}

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
				this.holder.save();
				DailyActivityHandler.getInstance().sendTaskList(player);
				break;
			}
		} catch (Exception e) {
			GameLog.error("DailyActivityMgr", "#AddTaskTimesByType()", "添加任务发生异常:userId=" + this.holder.getUserId() + ",taskType=" + taskType + ",count=" + count, e);
		}
	}

	private DailyActivityData getActivityDataById(int type) {
		for (DailyActivityData td : holder.getTaskItem().getTaskList()) {
			DailyActivityCfg tempCfg = (DailyActivityCfg) DailyActivityCfgDAO.getInstance().getCfgById(String.valueOf(td.getTaskId()));
			if (tempCfg == null) {
				continue;
			}

			if (tempCfg.getTaskType() == type)
				return td;
		}
		return null;
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
