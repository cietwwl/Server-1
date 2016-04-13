package com.playerdata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.playerdata.common.PlayerEventListener;
import com.rw.service.dailyActivity.DailyActivityHandler;
import com.rw.service.dailyActivity.Enum.DailyActivityClassifyType;
import com.rw.service.dailyActivity.Enum.DailyActivityFinishType;
import com.rwbase.dao.task.DailyActivityCfgDAO;
import com.rwbase.dao.task.DailyActivityHolder;
import com.rwbase.dao.task.TableDailyActivityItemDAO;
import com.rwbase.dao.task.pojo.DailyActivityCfg;
import com.rwbase.dao.task.pojo.DailyActivityData;
import com.rwbase.dao.task.pojo.DailyActivityTaskItem;
import com.rwbase.dao.user.UserGameData;
import com.rwbase.dao.user.UserGameDataDao;

/**
 * 任务的数据管理类。
 * */
public class DailyActivityMgr implements PlayerEventListener{

	private boolean isRefresh = false;
	private Player player = null;

	private DailyActivityHolder holder;
	
	// 初始化
	public void init(Player playerP) {
		player = playerP;
		holder = new DailyActivityHolder(playerP);
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		DailyActivityTaskItem taskItem = new DailyActivityTaskItem();
		taskItem.setUserId(player.getUserId());
		TableDailyActivityItemDAO.getInstance().update(taskItem);
	}

	@Override
	public void notifyPlayerLogin(Player player) {
	}

	public void onLogin() {
		DailyActivityHandler.getInstance().sendTaskList(player);

		// HotPointMgr.changeHotPointState(m_pPlayer.getUserId(),hotPointType,boolean);
	}

	// /刷新任务红点
	public void resRed() {
		DailyActivityHandler.getInstance().sendTaskList(player);
	}

	// 从配置文件中重新刷新任务列表
	public List<DailyActivityData> GetTaskListByCfg(boolean formCfg) {
		List<DailyActivityCfg> taskCfgList = DailyActivityCfgDAO.getInstance().getAllCfg();
		List<DailyActivityData> taskList = new ArrayList<DailyActivityData>();
		
		
		// 根据开启条件将任务加入任务列表,主要是时间和等级;
		for (DailyActivityCfg cfg : taskCfgList) {
			boolean isRemove = false;
			
			for (DailyActivityData td : holder.getTaskItem().getRemoveTaskList())
			{
				DailyActivityCfg saveDailyActivityCfg = (DailyActivityCfg) DailyActivityCfgDAO.getInstance().getCfgById(td.getTaskId() + "");
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
			if (isRemove)
				continue; // 不加入已经领取过奖励的任务

			if (hasNoRight(cfg)) {
				continue;
			}

			DailyActivityData tempData = getActivityDataById(cfg.getTaskType());
			if (cfg.getTaskClassify() == DailyActivityClassifyType.Time_Type) {
				// 获得开启的时间;
				String[] timeArray = cfg.getStartCondition().split("_");
				int beginHour = Integer.parseInt(timeArray[0].split(":")[0]);
				int beginMinute = Integer.parseInt(timeArray[0].split(":")[1]);
				int endHour = Integer.parseInt(timeArray[1].split(":")[0]);
				int endMinute = Integer.parseInt(timeArray[1].split(":")[1]);
				// 当前时间
				Calendar c = Calendar.getInstance();
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				if (((beginHour < hour) || (beginHour == hour && beginMinute <= minute)) && ((endHour > hour) || (endHour == hour && endMinute > minute))) {
					
					DailyActivityData data = new DailyActivityData();
					data.setTaskId(cfg.getId());
					// 判断是否可以领奖
					String[] timeArrayFinish = cfg.getFinishCondition().split("_");
					int beginHourFinish = Integer.parseInt(timeArrayFinish[0].split(":")[0]);
					int beginMinuteFinish = Integer.parseInt(timeArrayFinish[0].split(":")[1]);
					int endHourFinish = Integer.parseInt(timeArrayFinish[1].split(":")[0]);
					int endMinuteFinish = Integer.parseInt(timeArrayFinish[1].split(":")[1]);
					if (((beginHourFinish < hour) || (beginHourFinish == hour && beginMinuteFinish <= minute)) && ((endHourFinish > hour) || (endHourFinish == hour && endMinuteFinish > minute))) {
						data.setCanGetReward(1);
					} else {
						data.setCanGetReward(0);
					}
					
					if(tempData!=null)
					{
						data.setCanGetReward(tempData.getCanGetReward());
						data.setCurrentProgress(tempData.getCurrentProgress());
					}
						
					taskList.add(data);
				}
			} else if (cfg.getTaskClassify() == DailyActivityClassifyType.Function_Type) {
				// 判断等级是否达到,初始化主角的时候还没给主角等级赋值，所以人为加1;
				if ((player.getLevel()) >= Integer.parseInt(cfg.getStartCondition())) {
					DailyActivityData data = new DailyActivityData();
					data.setTaskId(cfg.getId());
					data.setCanGetReward(0);
					if (cfg.getTaskFinishType() == DailyActivityFinishType.Many_Time) {
						data.setCurrentProgress(0);
					}
					// 判断是否可以领奖;
					if (formCfg == false) {
						for (DailyActivityData td : holder.getTaskItem().getTaskList()) {
							if (td.getTaskId() == cfg.getId()) {
								if (cfg.getTaskFinishType() == DailyActivityFinishType.Many_Time) {
									data.setCurrentProgress(td.getCurrentProgress());
									if (td.getCurrentProgress() >= Integer.parseInt(cfg.getFinishCondition())) {
										data.setCanGetReward(1);
									}
								}
								break;
							}
						}
					}
					
					if(tempData!=null)
					{
						data.setCanGetReward(tempData.getCanGetReward());
						data.setCurrentProgress(tempData.getCurrentProgress());
					}
					taskList.add(data);
				}
			}
		}
		return taskList;
	}
	// 从配置文件中重新刷新任务列表
	public List<DailyActivityData> getResetTaskList() {
		List<DailyActivityCfg> taskCfgList = DailyActivityCfgDAO.getInstance().getAllCfg();
		List<DailyActivityData> taskList = new ArrayList<DailyActivityData>();
		
		
		// 根据开启条件将任务加入任务列表,主要是时间和等级;
		for (DailyActivityCfg cfg : taskCfgList) {			
			
			if (hasNoRight(cfg)) {
				continue;
			}			

			if (cfg.getTaskClassify() == DailyActivityClassifyType.Time_Type) {
				// 获得开启的时间;
				String[] timeArray = cfg.getStartCondition().split("_");
				int beginHour = Integer.parseInt(timeArray[0].split(":")[0]);
				int beginMinute = Integer.parseInt(timeArray[0].split(":")[1]);
				int endHour = Integer.parseInt(timeArray[1].split(":")[0]);
				int endMinute = Integer.parseInt(timeArray[1].split(":")[1]);
				// 当前时间
				Calendar c = Calendar.getInstance();
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				if (((beginHour < hour) || (beginHour == hour && beginMinute <= minute)) && ((endHour > hour) || (endHour == hour && endMinute > minute))) {
					
					DailyActivityData data = new DailyActivityData();
					data.setTaskId(cfg.getId());
					// 判断是否可以领奖
					String[] timeArrayFinish = cfg.getFinishCondition().split("_");
					int beginHourFinish = Integer.parseInt(timeArrayFinish[0].split(":")[0]);
					int beginMinuteFinish = Integer.parseInt(timeArrayFinish[0].split(":")[1]);
					int endHourFinish = Integer.parseInt(timeArrayFinish[1].split(":")[0]);
					int endMinuteFinish = Integer.parseInt(timeArrayFinish[1].split(":")[1]);
					if (((beginHourFinish < hour) || (beginHourFinish == hour && beginMinuteFinish <= minute)) && ((endHourFinish > hour) || (endHourFinish == hour && endMinuteFinish > minute))) {
						data.setCanGetReward(1);
					} else {
						data.setCanGetReward(0);
					}
				
					
					taskList.add(data);
				}
			} else if (cfg.getTaskClassify() == DailyActivityClassifyType.Function_Type) {
				// 判断等级是否达到,初始化主角的时候还没给主角等级赋值，所以人为加1;
				if ((player.getLevel()) >= Integer.parseInt(cfg.getStartCondition())) {
					DailyActivityData data = new DailyActivityData();
					data.setTaskId(cfg.getId());
					data.setCanGetReward(0);
					if (cfg.getTaskFinishType() == DailyActivityFinishType.Many_Time) {
						data.setCurrentProgress(0);
					}									
				
					taskList.add(data);
				}
			}
		}
		return taskList;
	}

	public boolean hasNoRight(DailyActivityCfg cfg) {
		return cfg.getMaxLevel() < player.getLevel() || player.getVip() < cfg.getVip() || cfg.getMaxVip() < player.getVip();
	}

	public void save() {
		RefreshTaskNum();
		holder.save();
	}
	
	// 返回一个角色所有的任务
	public List<DailyActivityData> getAllTask() {
		holder.getTaskItem().setTaskList(GetTaskListByCfg(false));
		return holder.getTaskItem().getTaskList();
	}

	public DailyActivityCfg getCfgByTaskId(int taskId) {
		DailyActivityCfg cfgById = DailyActivityCfgDAO.getInstance().GetTaskCfgById(taskId);
		return cfgById;
	}

	// 凌晨5点刷新任务列表;
	public void UpdateTaskList() {
		// 凌晨5点刷新任务列表;
		if (isRefresh == false) {
			RefreshTaskList();
			isRefresh = true;
		}
	}

	private void RefreshTaskList() {
		holder.getTaskItem().setUserId(player.getUserId());
		holder.getTaskItem().getRemoveTaskList().clear();
		holder.getTaskItem().setTaskList(getResetTaskList());
		save();
	}

	public void ChangeRefreshVar() {
		isRefresh = false;
	}

	// 完成任务，删除指定的任务id对应的任务
	public boolean RemoveTaskById(int taskId) {
		List<DailyActivityData> curTaskList = holder.getTaskItem().getTaskList();
		int taskCount = curTaskList.size();
		boolean removeSuccess = false;
		for (int i = 0; i < taskCount; i++) {
			if (curTaskList.get(i).getTaskId() == taskId) {
				holder.getTaskItem().getRemoveTaskList().add(curTaskList.get(i));
				curTaskList.remove(i);
				removeSuccess = true;
				break;
			}
		}
		if(removeSuccess){
			holder.getTaskItem().setTaskList(curTaskList);
			save();
		}
		return removeSuccess;
	}

	/**** 增加叠加任务的完成数量 *****/
	public void AddTaskTimesByType(int taskType, int count) {
		try {
			DailyActivityCfg taskCfg;
			int taskCount = holder.getTaskItem().getTaskList().size();
			for (int i = 0; i < taskCount; i++) {
				taskCfg = DailyActivityCfgDAO.getInstance().GetTaskCfgById(holder.getTaskItem().getTaskList().get(i).getTaskId());
				
				if(taskCfg.getTaskClassify()==DailyActivityClassifyType.Function_Type && Integer.parseInt(taskCfg.getStartCondition())>player.getLevel())continue;
				
				if (taskCfg.getTaskType() == taskType) {
					if (taskCfg.getTaskFinishType() == DailyActivityFinishType.Many_Time) {
						int curProgress = holder.getTaskItem().getTaskList().get(i).getCurrentProgress();

						if (curProgress + count >= Integer.parseInt(taskCfg.getFinishCondition())) {
							holder.getTaskItem().getTaskList().get(i).setCurrentProgress(Integer.parseInt(taskCfg.getFinishCondition()));
							holder.getTaskItem().getTaskList().get(i).setCanGetReward(1);
						} else {
							holder.getTaskItem().getTaskList().get(i).setCurrentProgress(curProgress + count);
						}
					}
					break;
				}
			}
			DailyActivityHandler.getInstance().sendTaskList(player);
			save();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	private DailyActivityData getActivityDataById(int type)
	{
		DailyActivityCfg tempCfg;
		for(DailyActivityData td : holder.getTaskItem().getTaskList())
		{
			tempCfg = (DailyActivityCfg) DailyActivityCfgDAO.getInstance().getCfgById(td.getTaskId() + "");
			if (tempCfg.getTaskType() == type) 
				return td;
		}
		return null;
	}
	
	// 判断剩余任务的数量
	private void RefreshTaskNum() {
		int curRemainTaskNum = 0;
		int taskCount = holder.getTaskItem().getTaskList().size();
		for (int i = 0; i < taskCount; i++) {
			DailyActivityCfg taskCfg = DailyActivityCfgDAO.getInstance().GetTaskCfgById(holder.getTaskItem().getTaskList().get(i).getTaskId());
			if(taskCfg==null)continue;
			if(holder.getTaskItem()!=null && holder.getTaskItem().getTaskList().get(i)!=null)
			{
				if ((taskCfg.getTaskClassify() != DailyActivityClassifyType.Time_Type) || (taskCfg.getTaskClassify() == DailyActivityClassifyType.Time_Type && holder.getTaskItem().getTaskList().get(i).getCanGetReward() == 1)) {
					curRemainTaskNum++;
				}
			}
		}
		UserGameData m_BaseData = UserGameDataDao.getInstance().get(player.getUserId());
		m_BaseData.setTaskNum(curRemainTaskNum);
	}
	
}
