package com.playerdata.groupcompetition.stageimpl;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.data.IGCompStage;
import com.playerdata.groupcompetition.holder.GCTeamDataMgr;
import com.playerdata.groupcompetition.holder.GCompMatchDataMgr;
import com.playerdata.groupcompetition.holder.GCompSelectionDataMgr;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompCommonTask;
import com.playerdata.groupcompetition.util.GCompEventsStatus;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.playerdata.groupcompetition.util.IConsumer;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rwbase.dao.groupcompetition.GroupCompetitionAgainstCfgDAO;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

/**
 * 
 * 帮会争霸：赛事阶段
 * 
 * @author CHEN.P
 *
 */
public class GCompEventsStage implements IGCompStage {
	
	private static final java.text.SimpleDateFormat _dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private GCEventsType _currentEventsType; // 当前的比赛状态
	private GCompEvents _events; // 争霸赛事
	private EventsTypeControlTask _eventsTypeControlTask; // 赛事类型控制任务
	private EventStatusSwitcher _eventStatusSwitcher; // 当前赛事的状态切换
	private boolean _stageEnd; // 阶段是否已经结束
	private long _stageEndTime; // 本阶段结束的时间
	private String _stageCfgId;
	
	public GCompEventsStage(GroupCompetitionStageCfg cfg) {
		_eventsTypeControlTask = new EventsTypeControlTask(this);
		_eventStatusSwitcher = new EventStatusSwitcher();
		_stageCfgId = cfg.getCfgId();
	}
	
	private void submitEventsStatusSwitchTask() {
		long endTimeMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(_events.getCurrentStatus().getLastMinutes());
		System.err.println("提交赛事状态控制任务！当前状态：" + _events.getCurrentStatus() + "，deadLine：" + _dateFormatter.format(new Date(endTimeMillis)));
		GCompCommonTask.scheduleCommonTask(_eventStatusSwitcher, this, endTimeMillis); // 结束的时效任务，等待回调
	}
	
	private void moveToEventsType(GCEventsType eventsType, List<String> groupIds) {
		// 移到下一个赛事
		List<IReadOnlyPair<Integer, Integer>> againstInfo;
		if(this._currentEventsType == null) {
			// 初赛
			againstInfo = GroupCompetitionAgainstCfgDAO.getInstance().getCfgById(String.valueOf(eventsType.sign)).getAgainstInfoList();
		} else {
			againstInfo = Collections.emptyList();
		}
		GroupCompetitionMgr.getInstance().updateCurrenEventstData(eventsType, groupIds);
		_currentEventsType = eventsType;
		_events = new GCompEvents.Builder(groupIds, eventsType).setAgainstsInfo(againstInfo).build();
		_events.start();
		this.submitEventsStatusSwitchTask();
		GCTeamDataMgr.getInstance().onEventsStart(this._currentEventsType); // 通知赛事开始
	}
	
	/**
	 * 通知本轮赛事结束
	 */
	private void evnetsEnd() {
		this._events.onEnd();
		if (this._currentEventsType.hasNext()) {
			// 推进到下一轮赛事
			this.createEventsTypeControlTask(this._events.getWinGroups(), _currentEventsType.getNext());
		} else {
			// 赛事阶段结束
			this.allEventsFinished();
		}
	}
	
	private void switchEventsStatus() {
		boolean success = this._events.switchToNextStatus();
		if (!success) {
			this.evnetsEnd();
		} else {
			this.submitEventsStatusSwitchTask();
		}
	}
	
	private void allEventsFinished() {
		// 所有的赛事完结
		this._stageEnd = true;
		System.err.println("所有的赛事已经完结！");
	}
	
	/**
	 * 
	 * 计算本阶段的结束时间
	 * 
	 * @param startStatus
	 * @param startOnNextDay
	 * @return
	 */
	private long calculateEndTime(GCEventsType startStatus, boolean startOnNextDay) {
		int lastDays = startStatus.getDaysNeededToFinal();
		if(startOnNextDay) {
			lastDays++;
		}
		GroupCompetitionStageCfg cfg = GroupCompetitionStageCfgDAO.getInstance().getCfgById(_stageCfgId);
		IReadOnlyPair<Integer, Integer> startTimeInfo = cfg.getStartTimeInfo();
		int hour = startTimeInfo.getT1().intValue();
		int minute = startTimeInfo.getT2().intValue();
		Calendar currentDateTime = Calendar.getInstance();
		currentDateTime.add(Calendar.DAY_OF_YEAR, lastDays);
		currentDateTime.set(Calendar.HOUR_OF_DAY, hour);
		currentDateTime.set(Calendar.MINUTE, 0);
		if (minute > 0) {
			currentDateTime.set(Calendar.MINUTE, minute);
		}
		currentDateTime.add(Calendar.MINUTE, GCompEventsStatus.getTotalLastMinutes());
		currentDateTime.add(Calendar.MINUTE, 1); // 延迟1分钟
		return currentDateTime.getTimeInMillis();
	}
	
	/**
	 * 
	 * 创建一个赛事开始通知的时效任务
	 * 
	 * @param groupIds
	 * @param eventsType
	 * @return 是否在隔天开始
	 */
	private boolean createEventsTypeControlTask(List<String> groupIds, GCEventsType eventsType) {
		boolean startOnNextDay = false;
		GroupCompetitionStageCfg cfg = GroupCompetitionStageCfgDAO.getInstance().getCfgById(_stageCfgId);
		GCompEventsStageContext context = new GCompEventsStageContext(groupIds, eventsType);
		Calendar instance = Calendar.getInstance();
		IReadOnlyPair<Integer, Integer> timeInfos = cfg.getStartTimeInfo();
		instance.set(Calendar.HOUR_OF_DAY, timeInfos.getT1());
		if (timeInfos.getT2() > 0) {
			instance.set(Calendar.MINUTE, timeInfos.getT2());
		} else {
			instance.set(Calendar.MINUTE, 0);
		}
		instance.set(Calendar.SECOND, 0);
		long millis = instance.getTimeInMillis();
		if (millis < System.currentTimeMillis()) {
			// 如果是过了，则跨一天
			instance.add(Calendar.DAY_OF_YEAR, 1);
			startOnNextDay = true;
		}
		System.err.println("----------帮派争霸-赛事阶段-创建赛事类型控制任务, 赛事类型：" + eventsType + "，deadline : " + _dateFormatter.format(instance.getTime()) + ", groupIds : " + groupIds +  "--------");
		GCompCommonTask.scheduleCommonTask(_eventsTypeControlTask, context, instance.getTimeInMillis()); // 提交一个定时任务，到了赛事正式开始的时间，会初始化
		return startOnNextDay;
	}
	
	@Override
	public String getStageCfgId() {
		return _stageCfgId;
	}

	@Override
	public void onStageStart(IGCompStage preStage) {
		// 通知阶段开始，但这时候具体的赛事还未开始
		List<String> topCountGroups = GCompSelectionDataMgr.getInstance().getSelectedGroupIds();
		GCEventsType startType;
		if (topCountGroups.size() > 8) {
			startType = GCEventsType.TOP_16;
		} else {
			startType = GCEventsType.TOP_8;
		}
//		boolean startOnNextDay = this.createEventsTypeControlTask(topCountGroups, startType);
		GCompMatchDataMgr.getInstance().onEventStageStart(startType); // 清理上一次的数据
		this.moveToEventsType(startType, topCountGroups);
		this._stageEndTime = calculateEndTime(startType, false);
	}
	
	@Override
	public GCompStageType getStageType() {
		return GCompStageType.EVENTS;
	}
	
	@Override
	public void onStageEnd() {
		System.err.println("收到stageEnd事件！");
	}
	
	@Override
	public long getStageEndTime() {
		return this._stageEndTime;
	}
	
	/**
	 * 
	 * 赛事阶段的控制器
	 * 
	 * @author CHEN.P
	 *
	 */
	private static class EventsTypeControlTask implements IConsumer<GCompEventsStageContext> {

		private GCompEventsStage _stage;
		
		public EventsTypeControlTask(GCompEventsStage pStage) {
			this._stage = pStage;
		}
		
		@Override
		public void accept(GCompEventsStageContext context) {
			_stage.moveToEventsType(context.getStatus(), context.getGroupIds());
		}
		
	}
	
//	private static class EventsEndControlTask implements IConsumer<Boolean> {
//
//		private GCEventsStage _stage;
//		
//		public EventsEndControlTask(GCEventsStage pStage) {
//			this._stage = pStage;
//		}
//		
//		@Override
//		public void accept(Boolean present) {
//			_stage.evnetsEnd();
//		}
//		
//	}
	
	private static class EventStatusSwitcher implements IConsumer<GCompEventsStage> {
		
		@Override
		public void accept(GCompEventsStage stage) {
			stage.switchEventsStatus();
		}
		
	}
}
