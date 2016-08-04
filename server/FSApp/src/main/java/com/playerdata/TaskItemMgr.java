package com.playerdata;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.readonly.TaskMgrIF;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BITaskType;
import com.rw.service.log.template.BilogItemInfo;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.common.enu.eTaskSuperType;
import com.rwbase.dao.hotPoint.EHotPointType;
import com.rwbase.dao.task.TaskCfgDAO;
import com.rwbase.dao.task.TaskItemHolder;
import com.rwbase.dao.task.pojo.TaskCfg;
import com.rwbase.dao.task.pojo.TaskItem;

public class TaskItemMgr implements TaskMgrIF {

	private TaskItemHolder taskItemHolder;

	private Player m_pPlayer = null;

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

	public void initTask() {
		List<TaskCfg> cfgList = TaskCfgDAO.getInstance().getInitList();
		ArrayList<TaskItem> itemList = new ArrayList<TaskItem>(cfgList.size());
		int size = cfgList.size();
		for (int i = 0; i < size; i++) {
			TaskCfg cfg = cfgList.get(i);
			
			if (cfg.getOpenLevel() <= m_pPlayer.getLevel() && !taskItemHolder.containsTask(cfg.getId())) {
				itemList.add(createTaskItem(cfg));
			}
		}
		if (!taskItemHolder.addItemList(m_pPlayer, itemList, false)) {
			return;
		}
		size = itemList.size();
		for (int i = 0; i < size; i++) {
			TaskItem task = itemList.get(i);			
			BILogMgr.getInstance().logTaskBegin(m_pPlayer, task.getTaskId(), BITaskType.Main);
		}
	}

	private void addItemTask(TaskCfg cfg, boolean doSyn) {
		TaskItem task = createTaskItem(cfg);
		taskItemHolder.addItem(m_pPlayer, task, doSyn);
		BILogMgr.getInstance().logTaskBegin(m_pPlayer, task.getTaskId(), BITaskType.Main);
	}

	/** 构造一个任务 **/
	private TaskItem createTaskItem(TaskCfg cfg) {
		TaskItem task = new TaskItem();
		task.setTaskId(cfg.getId());
		task.setFinishType(eTaskFinishDef.getDef(cfg.getFinishType()));
		task.setSuperType(cfg.getSuperType());
		setTaskInfo(task, cfg.getFinishParam());
		return task;
	}

	private void setTaskInfo(TaskItem task, String valueStr) {
		int curplan = 0;
		int value = Integer.parseInt(valueStr.split("_")[0]);
		int total = value;
		int value1 = 0;

		switch (task.getFinishType()) {
		case Section_Star:
			String[] arr = valueStr.split("_");
			if (arr.length > 1) {
				total = Integer.parseInt(valueStr.split("_")[1]);
			}
			break;
		case Hero_Quality:
		case Hero_Star:
			// value:佣兵个数 -- value1:佣兵品质/佣兵星数
			value1 = Integer.parseInt(valueStr.split("_")[1]);
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
		task.setDrawState(curplan >= total ? 1 : 0);
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
			curplan = m_pPlayer.getHeroMgr().getHerosSize();
			break;
		case Hero_Quality:
			curplan = m_pPlayer.getHeroMgr().checkQuality(value1);
			break;
		case Hero_Star:
			curplan = m_pPlayer.getHeroMgr().isHasStar(value1);
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
			curplan = m_pPlayer.getFriendMgr().getFriendList().size();
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
				if(cfg == null){//避免找不到配置表，暂时把这个给跳过
					continue;
				}
				int value = Integer.parseInt(cfg.getFinishParam().split("_")[0]);
				int value1 = 0;
				int finishType = cfg.getFinishType();
				if (finishType == eTaskFinishDef.Hero_Quality.getOrder() || finishType == eTaskFinishDef.Hero_Star.getOrder()) {
					// value:佣兵个数 -- value1:佣兵品质/佣兵星数
					value1 = Integer.parseInt(cfg.getFinishParam().split("_")[1]);
				}
				int curProgress = getCurProgress(task.getFinishType(), value, value1);
				// fix bug#2000 任务模块，精英任务奖励逻辑有问题
				// if(task.getFinishType() == eTaskFinishDef.Finish_Copy_Elite){
				// curProgress++;
				// }
				if (curProgress != task.getCurProgress()) {
					task.setCurProgress(curProgress);
					if (task.getCurProgress() >= task.getTotalProgress()) {
						task.setDrawState(1);						
												
						BILogMgr.getInstance().logTaskEnd(m_pPlayer, task.getTaskId(), BITaskType.Main, true,BILogTemplateHelper.getString(BilogItemInfo.fromStr(cfg.getReward())));
					} else {
						task.setDrawState(0);
						
						BILogMgr.getInstance().logTaskEnd(m_pPlayer, task.getTaskId(), BITaskType.Main, false,BILogTemplateHelper.getString(BilogItemInfo.fromStr(cfg.getReward())));
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
			if (task.getFinishType() == taskType && task.getDrawState() == 0 && task.getSuperType() == eTaskSuperType.More.ordinal()) {
				task.setCurProgress(count + task.getCurProgress());
				if (task.getCurProgress() >= task.getTotalProgress()) {
					task.setDrawState(1);					
					BILogMgr.getInstance().logTaskEnd(m_pPlayer, task.getTaskId(), BITaskType.Main, true,BILogTemplateHelper.getString(BilogItemInfo.fromStr(cfg.getReward())));
				} else {
					task.setDrawState(0);
					BILogMgr.getInstance().logTaskEnd(m_pPlayer, task.getTaskId(), BITaskType.Main, false,BILogTemplateHelper.getString(BilogItemInfo.fromStr(cfg.getReward())));
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

		if (task.getDrawState() == 0 || task.getDrawState() == 2) {
			return -3;
		}

		String[] rewards = cfg.getReward().split(",");
		for (String reward : rewards) {
			int itemId = Integer.parseInt(reward.split("_")[0]);
			int count = Integer.parseInt(reward.split("_")[1]);
			m_pPlayer.getItemBagMgr().addItem(itemId, count);
		}
		task.setDrawState(2);
		if (cfg.getPreTask() != -1) {
			taskItemHolder.removeItem(m_pPlayer, task);
		} else {
			taskItemHolder.updateItem(m_pPlayer, task);
		}
		TaskCfg nextCfg = TaskCfgDAO.getInstance().getCfgByPreId(taskId);
		if (nextCfg != null) {
			final boolean doSyn = true;
			addItemTask(nextCfg, doSyn);
		}
		return 1;
	}

	public void checkHot() {
		boolean hasHot = false;
		List<TaskItem> tasklist = taskItemHolder.getItemList();
		for (TaskItem taskItem : tasklist) {
			if (taskItem.getDrawState() == 1) {
				hasHot = true;
				break;
			}

		}
		HotPointMgr.changeHotPointState(m_pPlayer.getUserId(), EHotPointType.Task, hasHot);
	}

	public boolean save() {
		taskItemHolder.flush();
		return true;
	}

	public List<TaskItem> getTaskEnumeration() {
		return taskItemHolder.getItemList();
	}

}
