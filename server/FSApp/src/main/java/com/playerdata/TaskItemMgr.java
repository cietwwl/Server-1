package com.playerdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.common.Utils;
import com.log.GameLog;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.readonly.TaskMgrIF;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BITaskType;
import com.rw.service.log.template.BilogItemInfo;
import com.rwbase.common.enu.TaskState;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.common.enu.eTaskSuperType;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.task.TaskCfgDAO;
import com.rwbase.dao.task.TaskItemHolder;
import com.rwbase.dao.task.pojo.TaskCfg;
import com.rwbase.dao.task.pojo.TaskItem;
import com.rwproto.TaskProtos.OneKeyResultType;

public class TaskItemMgr implements TaskMgrIF {

	private TaskItemHolder taskItemHolder;

	private Player m_pPlayer = null;

	private static final int TASK_STATE_NOT_DONE = TaskState.NOT_DONE.sign; // 未完成
	private static final int TASK_STATE_CAN_DRAW = TaskState.CAN_DRAW.sign; // 可领取奖励
	private static final int TASK_STATE_REWARD_DRAWED = TaskState.DRAWED.sign; // 已领取奖励

	// 初始化
	public boolean init(Player pOwner) {
		m_pPlayer = pOwner;
		taskItemHolder = new TaskItemHolder(pOwner.getUserId());
		initTask();
		return true;
	}

	public void synData(int version) {
		taskItemHolder.synAllData(m_pPlayer, version);
	}

	public void checkAndAddList() {
		List<TaskCfg> cfgList = TaskCfgDAO.getInstance().getInitList();
		int size = cfgList.size();
		ArrayList<TaskItem> itemList = initTask();
		if (itemList != null) {
			size = itemList.size();
			for (int i = 0; i < size; i++) {
				TaskItem task = itemList.get(i);
				BILogMgr.getInstance().logTaskBegin(m_pPlayer, task.getTaskId(), BITaskType.Main);
			}
		}
	}

	public ArrayList<TaskItem> initTask() {
		List<TaskCfg> cfgList = TaskCfgDAO.getInstance().getInitList();
		int size = cfgList.size();
		ArrayList<TaskItem> itemList = new ArrayList<TaskItem>(size);
		for (int i = 0; i < size; i++) {
			TaskCfg cfg = cfgList.get(i);

			if (cfg.getOpenLevel() <= m_pPlayer.getLevel() && !taskItemHolder.containsTask(cfg.getId())) {
				itemList.add(createTaskItem(cfg));
			}
		}
		if (!taskItemHolder.addItemList(m_pPlayer, itemList, false)) {
			return itemList;
		}
		return null;
	}

	private TaskItem addItemTask(TaskCfg cfg, boolean doSyn) {
		TaskItem task = createTaskItem(cfg);
		taskItemHolder.addItem(m_pPlayer, task, doSyn);
		BILogMgr.getInstance().logTaskBegin(m_pPlayer, task.getTaskId(), BITaskType.Main);
		return task;
	}

	/** 构造一个任务 **/
	private TaskItem createTaskItem(TaskCfg cfg) {
		TaskItem task = new TaskItem();
		task.setTaskId(cfg.getId());
		task.setFinishType(eTaskFinishDef.getDef(cfg.getFinishType()));
		task.setSuperType(cfg.getSuperType());
		setTaskInfo(task, cfg.getFinishParamList());
		return task;
	}

	private void setTaskInfo(TaskItem task, List<String> finishParamList) {
		int curplan = 0;
		int value = Integer.parseInt(finishParamList.get(0));
		int total = value;
		int value1 = 0;

		switch (task.getFinishType()) {
		case Section_Star:
			if (finishParamList.size() > 1) {
				total = Integer.parseInt(finishParamList.get(1));
			}
			break;
		case Hero_Quality:
		case Hero_Star:
			// value:佣兵个数 -- value1:佣兵品质/佣兵星数
			value1 = Integer.parseInt(finishParamList.get(1));
			break;
		case Change_Career:
		case Finish_Copy_Normal:
		case Finish_Section:
		case Transfer:
		case Finish_Copy_Elite:
		case Finish_Copy_Hero:
			total = 1;
			break;
		default:
			break;
		}
		curplan = getCurProgress(task.getFinishType(), value, value1);
		task.setCurProgress(curplan);
		task.setTotalProgress(total);
		// task.setDrawState(curplan >= total ? 1 : 0);
		task.setDrawState(curplan >= total ? TASK_STATE_CAN_DRAW : TASK_STATE_NOT_DONE);
	}

	private int getCurProgress(eTaskFinishDef taskType, int value, int value1) {
		int curplan = 0;
		switch (taskType) {
		case Section_Star:
			curplan = m_pPlayer.getCopyRecordMgr().getMapCurrentStar(value);
			break;
		case Finish_Copy_Normal:
			curplan = m_pPlayer.getCopyRecordMgr().getLevelRecord(value).getPassStar() > 0 ? 1 : 0;
			break;
		case Finish_Copy_Elite:
			// fix bug#2000 任务模块，精英任务奖励逻辑有问题
			curplan = m_pPlayer.getCopyRecordMgr().getLevelRecord(value).getPassStar() > 0 ? 1 : 0;
			break;
		case Finish_Section:
			curplan = m_pPlayer.getCopyRecordMgr().isMapClear(value) ? 1 : 0;
			break;
		case Hero_Count:
			// curplan = m_pPlayer.getHeroMgr().getHerosSize();
			curplan = m_pPlayer.getHeroMgr().getHerosSize(m_pPlayer);
			break;
		case Hero_Quality:
			// curplan = m_pPlayer.getHeroMgr().checkQuality(value1);
			curplan = m_pPlayer.getHeroMgr().checkQuality(m_pPlayer, value1);
			break;
		case Hero_Star:
			// curplan = m_pPlayer.getHeroMgr().isHasStar(value1);
			curplan = m_pPlayer.getHeroMgr().isHasStar(m_pPlayer, value1);
			break;
		case Player_Level:
			curplan = m_pPlayer.getLevel();
			break;
		case Player_Quality:
			curplan = m_pPlayer.getStarLevel();
			break;
		case Recharge:
			curplan = ChargeMgr.getInstance().getChargeInfo(m_pPlayer.getUserId()).getTotalChargeGold();
			break;
		case Transfer:
			curplan = m_pPlayer.getCareer() > 0 ? 1 : 0;
			break;
		case Add_Friend:
			curplan = m_pPlayer.getFriendMgr().getFriendCount();
			break;
		case Challage_BattleTower:
			curplan = m_pPlayer.getBattleTowerMgr().getTableBattleTower().getHighestFloor();
			break;
		default:
			break;
		}
		return curplan;
	}

	public void AddTaskTimes(eTaskFinishDef taskType) {
		List<TaskItem> itemList = taskItemHolder.getItemList();

		for (TaskItem task : itemList) {

			if (task.getFinishType() == taskType && task.getDrawState() == 0 && task.getSuperType() == eTaskSuperType.Once.ordinal()) {
				TaskCfg cfg = TaskCfgDAO.getInstance().getCfg(task.getTaskId());
				if (cfg == null) {// 避免找不到配置表，暂时把这个给跳过
					continue;
				}
				int value = Integer.parseInt(cfg.getFinishParamList().get(0));
				int value1 = 0;
				int finishType = cfg.getFinishType();
				if (finishType == eTaskFinishDef.Hero_Quality.getOrder() || finishType == eTaskFinishDef.Hero_Star.getOrder()) {
					// value:佣兵个数 -- value1:佣兵品质/佣兵星数
					value1 = Integer.parseInt(cfg.getFinishParamList().get(1));
				}
				int curProgress = getCurProgress(task.getFinishType(), value, value1);
				// fix bug#2000 任务模块，精英任务奖励逻辑有问题
				// if(task.getFinishType() == eTaskFinishDef.Finish_Copy_Elite){
				// curProgress++;
				// }
				if (curProgress != task.getCurProgress()) {
					task.setCurProgress(curProgress);
					if (task.getCurProgress() >= task.getTotalProgress()) {
						// task.setDrawState(1);
						task.setDrawState(TASK_STATE_CAN_DRAW);
						BILogMgr.getInstance().logTaskEnd(m_pPlayer, task.getTaskId(), BITaskType.Main, true, BILogTemplateHelper.getString(BilogItemInfo.fromStr(cfg.getReward())));
					} else {
						// task.setDrawState(0);
						task.setDrawState(TASK_STATE_NOT_DONE);
						if (taskType != eTaskFinishDef.Add_Friend) {
							BILogMgr.getInstance().logTaskEnd(m_pPlayer, task.getTaskId(), BITaskType.Main, false, BILogTemplateHelper.getString(BilogItemInfo.fromStr(cfg.getReward())));
						}
					}
				}
				taskItemHolder.updateItem(m_pPlayer, task);
			}
		}

	}

	public void AddTaskTimes(eTaskFinishDef taskType, int count) {
		List<TaskItem> itemList = taskItemHolder.getItemList();
		for (TaskItem task : itemList) {
			TaskCfg cfg = TaskCfgDAO.getInstance().getCfg(task.getTaskId());
			if (cfg == null) {
				GameLog.info("Task", String.valueOf(task.getTaskId()), "TaskCfg配置表错误：没有ID为" + task.getTaskId() + "的任务", null);
				continue;
			}
			if (task.getFinishType() == taskType && task.getDrawState() == TASK_STATE_NOT_DONE && task.getSuperType() == eTaskSuperType.More.ordinal()) {
				task.setCurProgress(count + task.getCurProgress());
				if (task.getCurProgress() >= task.getTotalProgress()) {
					// task.setDrawState(1);
					task.setDrawState(TASK_STATE_CAN_DRAW);
					BILogMgr.getInstance().logTaskEnd(m_pPlayer, task.getTaskId(), BITaskType.Main, true, BILogTemplateHelper.getString(BilogItemInfo.fromStr(cfg.getReward())));
				} else {
					// task.setDrawState(0);
					task.setDrawState(TASK_STATE_NOT_DONE);
					BILogMgr.getInstance().logTaskEnd(m_pPlayer, task.getTaskId(), BITaskType.Main, false, BILogTemplateHelper.getString(BilogItemInfo.fromStr(cfg.getReward())));
				}
				taskItemHolder.updateItem(m_pPlayer, task);
				break;
			}
		}
	}

	public int getReward(int taskId) {
		TaskItem task = taskItemHolder.getByTaskId(taskId);
		if (task == null) {
			GameLog.info("Task", String.valueOf(taskId), "数据错误：没有ID为" + taskId + "的任务", null);
			return -1;
		}
		TaskCfg cfg = TaskCfgDAO.getInstance().getCfg(taskId);
		if (cfg == null) {
			GameLog.info("Task", String.valueOf(taskId), "TaskCfg配置表错误：没有ID为" + taskId + "的任务", null);
			return -2;
		}

		if (task.getDrawState() == TASK_STATE_NOT_DONE || task.getDrawState() == TASK_STATE_REWARD_DRAWED) {
			return -3;
		}

		Map<Integer, Integer> taskRewards = cfg.getRewardMap();
		List<ItemInfo> items = new ArrayList<ItemInfo>(taskRewards.size());
		for (Iterator<Integer> keyItr = taskRewards.keySet().iterator(); keyItr.hasNext();) {
			Integer itemId = keyItr.next();
			Integer count = taskRewards.get(itemId);
			items.add(new ItemInfo(itemId.intValue(), count.intValue()));
		}
		ItemBagMgr.getInstance().addItem(m_pPlayer, items);
		task.setDrawState(TASK_STATE_REWARD_DRAWED);
		if (cfg.getPreTask() != -1) { // 这里为什么不删除？BY PERRY @ 2016-11-07
			taskItemHolder.removeItem(m_pPlayer, task);
		} else {
			taskItemHolder.updateItem(m_pPlayer, task, false);
			taskItemHolder.synRemove(m_pPlayer, task);
		}
		TaskCfg nextCfg = TaskCfgDAO.getInstance().getCfgByPreId(taskId);
		if (nextCfg != null) {
			final boolean doSyn = true;
			addItemTask(nextCfg, doSyn);
		}
		return 1;
	}

	/**
	 * 领取所有已经完成的任务
	 * 
	 * @return
	 */
	public OneKeyResultType getAllReward(HashMap<Integer, Integer> rewardMap) {
		List<TaskItem> removeList = new ArrayList<TaskItem>();
		List<TaskItem> allTask = taskItemHolder.getItemList();
		if (null == allTask || allTask.isEmpty()) {
			GameLog.error("Task", "获取所有任务奖励", "数据错误：没有可以领取的奖励", null);
			return OneKeyResultType.NO_REWARD;
		}

		List<TaskItem> checkTasks = new ArrayList<TaskItem>();
		for (TaskItem task : allTask) {
			TaskCfg cfg = TaskCfgDAO.getInstance().getCfg(task.getTaskId());
			if (cfg == null) {
				GameLog.error("Task", String.valueOf(task.getTaskId()), "TaskCfg配置表错误：没有ID为" + task.getTaskId() + "的任务", null);
				continue;
			}
			if (task.getDrawState() == TASK_STATE_NOT_DONE || task.getDrawState() == TASK_STATE_REWARD_DRAWED) {
				// 已领取或者未完成
				continue;
			}
			checkTasks.add(task);
		}
		OneKeyResultType result = getAllReward(rewardMap, removeList, checkTasks);
		if (OneKeyResultType.NO_REWARD != result) {
			taskItemHolder.removeItem(m_pPlayer, removeList);
			taskItemHolder.synAllData(m_pPlayer, 0);
		}
		return result;
	}

	/**
	 * 领取所有已经完成的任务
	 * 
	 * @return
	 */
	private OneKeyResultType getAllReward(HashMap<Integer, Integer> rewardMap, List<TaskItem> removeList, List<TaskItem> taskList) {
		boolean hasNewLoop = false;
		List<TaskItem> checkTasks = new ArrayList<TaskItem>();

		if (null == taskList || taskList.isEmpty()) {
			GameLog.error("Task", "获取所有任务奖励", "数据错误：没有可以领取的奖励", null);
			return OneKeyResultType.NO_REWARD;
		}
		for (TaskItem task : taskList) {
			TaskCfg cfg = TaskCfgDAO.getInstance().getCfg(task.getTaskId());
			if (cfg == null) {
				GameLog.error("Task", String.valueOf(task.getTaskId()), "TaskCfg配置表错误：没有ID为" + task.getTaskId() + "的任务", null);
				continue;
			}
			if (task.getDrawState() == TASK_STATE_NOT_DONE || task.getDrawState() == TASK_STATE_REWARD_DRAWED) {
				// 已领取或者未完成
				continue;
			}
			Map<Integer, Integer> taskRewards = cfg.getRewardMap();
			Utils.combineAttrMap(taskRewards, rewardMap);
			task.setDrawState(TASK_STATE_REWARD_DRAWED);
			if (cfg.getPreTask() != -1) {
				removeList.add(task);
			} else {
				taskItemHolder.updateItem(m_pPlayer, task, false);
			}
			TaskCfg nextCfg = TaskCfgDAO.getInstance().getCfgByPreId(task.getTaskId());
			if (nextCfg != null) {
				checkTasks.add(addItemTaskNonSaveIfFinish(nextCfg));
				hasNewLoop = true;
			}
		}
		if (hasNewLoop)
			getAllReward(rewardMap, removeList, checkTasks);
		return OneKeyResultType.OneKey_SUCCESS;
	}

	/**
	 * 添加一条新的任务，如果完成，就不添加到数据库（因为会立刻被领取并删除掉） 用于一键领取功能
	 * 
	 * @param cfg
	 * @return
	 */
	private TaskItem addItemTaskNonSaveIfFinish(TaskCfg cfg) {
		TaskItem task = createTaskItem(cfg);
		if (task.getDrawState() == TASK_STATE_NOT_DONE) {
			// 未完成
			taskItemHolder.addItem(m_pPlayer, task, false);
		}
		BILogMgr.getInstance().logTaskBegin(m_pPlayer, task.getTaskId(), BITaskType.Main);
		return task;
	}

	public boolean save() {
		taskItemHolder.flush();
		return true;
	}

	public List<TaskItem> getTaskEnumeration() {
		return taskItemHolder.getItemList();
	}
}
