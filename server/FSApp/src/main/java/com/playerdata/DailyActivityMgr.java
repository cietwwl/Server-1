package com.playerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.log.GameLog;
import com.playerdata.common.PlayerEventListener;
import com.rw.service.dailyActivity.DailyActivityHandler;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.task.DailyActivityCfgDAO;
import com.rwbase.dao.task.DailyActivityHolder;
import com.rwbase.dao.task.DailyFinishType;
import com.rwbase.dao.task.pojo.DailyActivityCfg;
import com.rwbase.dao.task.pojo.DailyActivityCfgEntity;
import com.rwbase.dao.task.pojo.DailyActivityData;
import com.rwbase.dao.task.pojo.DailyActivityTaskItem;

/**
 * 任务的数据管理类。
 * */
public class DailyActivityMgr {

	private Player player = null;

	private DailyActivityHolder holder;

	// 初始化
	public void init(Player playerP) {
		player = playerP;
		holder = new DailyActivityHolder(playerP);
	}



	public void onLogin() {
		DailyActivityHandler.getInstance().sendTaskList(player);
	}

	// /刷新任务红点
	public void resRed() {
		DailyActivityHandler.getInstance().sendTaskList(player);
	}
	
	

	/**
	 * 新的获取当前角色的任务列表方法    --by Alex
	 * @return
	 */
	public List<DailyActivityData> getTaskList(){
		//---------准备数据----------------//
		DailyActivityCfgDAO cfgDAO = DailyActivityCfgDAO.getInstance();
		HashSet<Integer> taskType = cfgDAO.getAllTaskType();
		DailyActivityTaskItem taskItem = holder.getTaskItem();
		List<DailyActivityData> currentList = taskItem.getTaskList();
		List<Integer> firstInitTaskIds = taskItem.getFirstIncrementTaskIds();
		
		Map<Integer, Integer> finishMap = transferFinishList2Map(taskItem.getRemoveTaskList(), cfgDAO);
		
		List<DailyActivityData> scanList = new ArrayList<DailyActivityData>(currentList);

		int playerLevel = player.getLevel();
		int playerVip = player.getVip();
		String userId = player.getUserId();
		boolean change = false;
		
		
		//------先检查已经存在的任务类型------//
		for (DailyActivityData data : scanList) {
			//检查一下是否已经完成
			DailyActivityCfgEntity entity = cfgDAO.getCfgEntity(data.getTaskId());
			if(entity == null){
				//找不到配置,策划可能改表了，不同步这个任务，注意这里不可以做删除记录操作，避免策划填错表后，角色登录后目标任务数据被删除
				currentList.remove(data);
				continue;
			}
			boolean matchCondition = entity.getFinishCondition().isMatchCondition(userId, playerLevel, playerVip, data);
			if (data.getCanGetReward() == 0) {
				if (matchCondition) {
					// 这个任务之前没有完成，但满足完成条件，可以设置为完成
					data.setCanGetReward(1);
					change = true;
				} else if (entity.getCheckOutDateCondition().isOutDate()) {
					// 修复BUG#4854 BY PERRY @ 2016-11-07
					GameLog.info("DailyActivityMgr#getTaskList", userId, String.format("日常任务超时！任务id：%d", data.getTaskId()));
					currentList.remove(data);
					change = true;
				}
			} else if (!matchCondition) {
				// 之前是可以领取，但检查的时候发现条件不满足，说明策划可能改了条件了，或者是时间类的任务已经超时，要把它移除
				currentList.remove(data);
				change = true;

				// 检查一下同类型的任务有没有合适开放的任务
				List<DailyActivityCfgEntity> subTypeList = cfgDAO.getCfgEntrisByType(entity.getCfg().getTaskType());
				if (subTypeList != null) {
					checkAndAddNewMission(subTypeList, playerVip, playerLevel, userId, finishMap, firstInitTaskIds, currentList);
				}

			}
			//移除已经检查的任务类型
			taskType.remove(entity.getCfg().getTaskType());
		}
		
		//移除已经完成的任务类型
		taskType.removeAll(finishMap.values());
		
		//------检查剩下的任务类型，看看有没有新任务------//
		for (Integer type : taskType) {
			List<DailyActivityCfgEntity> subTypeList = cfgDAO.getCfgEntrisByType(type);
			
			boolean add = checkAndAddNewMission(subTypeList, playerVip, playerLevel, userId, finishMap, firstInitTaskIds, currentList);
			if(add){
				change = true;
			}
			
		}
		
		if(change){
			holder.save();
		}
		
//		StringBuffer sb = new StringBuffer("新任务列表：");
//		for (DailyActivityData d : currentList) {
//			DailyActivityCfgEntity entity = cfgDAO.getCfgEntity(d.getTaskId());
//			sb.append("任务id：").append(d.getTaskId()).append(",任务类型：").append(entity.getCfg().getTaskType()).append("，任务名:")
//			.append(entity.getCfg().getTitle()).append(",是否完成：").append(d.getCanGetReward()).append("任务进度：").append(d.getCurrentProgress()).append("\n");
//		}
//		System.err.println(sb.toString());
		
		return new ArrayList<DailyActivityData>(currentList);
	}
	
	
	private boolean checkAndAddNewMission(List<DailyActivityCfgEntity> subTypeList, 
			int playerVip, int playerLevel, String userId, Map<Integer, Integer> finishMap, 
			List<Integer> firstInitTaskIds, List<DailyActivityData> currentList){
		
		DailyActivityCfgEntity newMission = null;
		for (DailyActivityCfgEntity cfgEntity : subTypeList) {
			if(cfgEntity.getStartCondition().isMatchCondition(userId, playerLevel, playerVip) 
					&& !finishMap.containsKey(cfgEntity.getCfg().getId()) && !hasNoRight(cfgEntity.getCfg(), playerLevel, playerVip)){
				
				//按策划要求，加入关卡开启条件  ---by Alex 10.22.2016
				if(cfgEntity.getCfg().getMapId() != 0 && !player.getCopyRecordMgr().isCopyLevelPassed(cfgEntity.getCfg().getMapId())){
					continue;
				}
				
				newMission = cfgEntity;
				break;
			}
		}
		
		if(newMission != null){
			DailyActivityData newData = new DailyActivityData();
			newData.setTaskId(newMission.getCfg().getId());
			setInitNum(newData, newMission.getCfg(), firstInitTaskIds);
			// 检查完成条件
			if (newMission.getFinishCondition().isMatchCondition(userId, playerLevel, playerVip, newData)) {
				newData.setCanGetReward(1);
			}
			currentList.add(newData);
			return true;
		}
		return false;
		
	}
	
	/**
	 * 返回已经完成的任务id，及对应的任务类型
	 * @param list
	 * @param cfgDAO 
	 * @return
	 */
	private Map<Integer, Integer> transferFinishList2Map(List<DailyActivityData> list, DailyActivityCfgDAO cfgDAO){
		Map<Integer, Integer> tempMap = new HashMap<Integer, Integer>();
		
		for (DailyActivityData data : list) {
			int taskId = data.getTaskId();
			DailyActivityCfg cfg = cfgDAO.getCfgEntity(taskId).getCfg();
			tempMap.put(taskId, cfg.getTaskType());
//			System.out.println("finish task id:" + taskId+", task type:" + cfg.getTaskType());
		}
		
		return tempMap;
	}
	
	// 从配置文件中重新刷新任务列表   
	public List<DailyActivityData> getTaskListByCfg(boolean refresh) {
		DailyActivityCfgDAO cfgDAO = DailyActivityCfgDAO.getInstance();
		List<DailyActivityCfgEntity> taskCfgList = cfgDAO.getAllReadOnlyEntitys();
		DailyActivityTaskItem taskItem = holder.getTaskItem();
		List<DailyActivityData> currentList;
		// 刷新
		if (refresh) {
			currentList = new ArrayList<DailyActivityData>();
		} else {
			currentList = taskItem.getTaskList();
		}
		boolean changed = false;
		List<DailyActivityData> removeList = taskItem.getRemoveTaskList();
		List<Integer> firstInitTaskIds = taskItem.getFirstIncrementTaskIds();

		
		
		
		int playerLevel = player.getLevel();
		int playerVip = player.getVip();
		String userId = player.getUserId();
		// 根据开启条件将任务加入任务列表,主要是时间和等级;
		for (DailyActivityCfgEntity entity : taskCfgList) {
			DailyActivityCfg cfg = entity.getCfg();
			if (!refresh && isRemoveTask(cfg, removeList)) {
				continue; // 不加入已经领取过奖励的任务
			}
			if (hasNoRight(cfg, playerLevel, playerVip)) {
				continue;
			}
			// 刷新的话不需要遍历检查是否存在任务，直接检查能否创建任务
			DailyActivityData tempData;
			if (refresh) {
				tempData = null;
			} else {
				tempData = getActivityDataByType(cfg.getTaskType(), cfgDAO, currentList);
			}

			if (tempData != null) {
				// 已经存在检查是否完成
				// 检查完成条件
				boolean matchCondition = entity.getFinishCondition().isMatchCondition(userId, playerLevel, playerVip, tempData);
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
				if (!entity.getStartCondition().isMatchCondition(userId, playerLevel, playerVip)) {
					continue;
				}
				//按策划要求，加入关卡开启条件  ---by Alex 10.22.2016
				if(cfg.getMapId() != 0 && !player.getCopyRecordMgr().isCopyLevelPassed(cfg.getMapId())){
					continue;
				}
				DailyActivityData data = new DailyActivityData();
				data.setTaskId(cfg.getId());
				currentList.add(data);
				setInitNum(data, cfg, firstInitTaskIds);
				// 检查完成条件
				if (entity.getFinishCondition().isMatchCondition(userId, playerLevel, playerVip, data)) {
					data.setCanGetReward(1);
				}
				changed = true;
			}
		}
		if (refresh) {
			taskItem.setTaskList(currentList);
			holder.save();
		} else if (changed) {
			holder.save();
		}

		// TODO HC 临时打个 补丁，用来解决日常任务被删除了某个配置之后，导致还出现的Bug
//		List<DailyActivityData> list = new ArrayList<DailyActivityData>();
//		Map<Integer, DailyActivityData> temMap = new HashMap<Integer, DailyActivityData>();
//		for (int i = currentList.size() - 1; i >= 0; --i) {
//			DailyActivityData data = currentList.get(i);
//			if (data == null) {
//				continue;
//			}
//
//			int taskId = data.getTaskId();
//			DailyActivityCfgEntity cfg = cfgDAO.getCfgEntity(taskId);
//			if (cfg == null) {
//				// System.out.println("------ID："+taskId+", 的任务不存在");
//				continue;
//			}
//
//			if (temMap.containsKey(taskId)) {
//				// System.out.println("======ID："+taskId+", 重复");
//				// 过滤掉重复的数据
//				continue;
//			}
//
//			temMap.put(taskId, data);
//			// System.out.println("------处理后的任务ID："+taskId+", 任务描述："+
//			// cfg.getCfg().getDescription());
//		}
//
//		list.addAll(temMap.values());

//		StringBuffer sb = new StringBuffer("新任务列表：");
//		for (DailyActivityData d : currentList) {
//			DailyActivityCfgEntity entity = cfgDAO.getCfgEntity(d.getTaskId());
//			sb.append("任务id：").append(d.getTaskId()).append(",任务类型：").append(entity.getCfg().getTaskType()).append("，任务名:")
//			.append(entity.getCfg().getTitle()).append(",是否完成：").append(d.getCanGetReward()).append("任务进度：").append(d.getCurrentProgress()).append("\n");
//		}
//		System.err.println(sb.toString());
		return currentList;
	}

	/** 检查该配置的任务是否已经被领取(移动到remove列表 ) **/
	private boolean isRemoveTask(DailyActivityCfg cfg, List<DailyActivityData> removeList) {
		int cfgId = cfg.getId();
		for (int i = removeList.size(); --i >= 0;) {
			if (removeList.get(i).getTaskId() == cfgId) {
				return true;
			}
		}
		return false;
	}

	public boolean hasNoRight(DailyActivityCfg cfg, int playerLevel, int playerVip) {
		// return cfg.getMaxLevel() < player.getLevel() || player.getVip() <
		// cfg.getVip() || cfg.getMaxVip() < player.getVip();
		//根据策划要求，加入
		return cfg.getMaxLevel() < playerLevel || playerVip < cfg.getVip() || cfg.getMaxVip() < playerVip;
	}

	// 返回一个角色所有的任务
	public List<DailyActivityData> getAllTask() {
//		return getTaskListByCfg(false);
		return getTaskList();
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
			String userId = player.getUserId();
			int playerLevel = player.getLevel();
			int playerVip = player.getVip();
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
				if (!entity.getStartCondition().isMatchCondition(userId, playerLevel, playerVip)) {
					continue;
				}
				//按策划要求，加入关卡开启条件  ---by Alex 10.22.2016
				if(entity.getCfg().getMapId() != 0 && !player.getCopyRecordMgr().isCopyLevelPassed(entity.getCfg().getMapId())){
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
				if (entity.getFinishCondition().isMatchCondition(userId, playerLevel, playerVip, taskData)) {
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

	
	
	private DailyActivityData getActivityDataByType(int type, DailyActivityCfgDAO cfgDAO, List<DailyActivityData> list) {
		for (DailyActivityData td : list) {
			DailyActivityCfg tempCfg = cfgDAO.getTaskCfgById(td.getTaskId());
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
