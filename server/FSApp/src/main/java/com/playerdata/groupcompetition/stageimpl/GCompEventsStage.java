package com.playerdata.groupcompetition.stageimpl;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.cfg.GCCommonCfgDAO;
import com.playerdata.groupcompetition.data.IGCStage;
import com.playerdata.groupcompetition.holder.GCompMatchDataMgr;
import com.playerdata.groupcompetition.holder.GCTeamDataMgr;
import com.playerdata.groupcompetition.util.GCompCommonTask;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.playerdata.groupcompetition.util.IConsumer;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

/**
 * 
 * 帮会争霸：赛事阶段
 * 
 * @author CHEN.P
 *
 */
public class GCompEventsStage implements IGCStage {
	
	private GCEventsType _currentEventsType; // 当前的比赛状态
	private GCCommonCfgDAO _competitionCommonCfgDAO; // 配置读取
	private GCompEvents _events; // 争霸赛事
	private EventsStatusControlTask _eventsStatusControlTask; // 赛事状态控制任务
	private EventStatusSwitcher _eventStatusSwitcher;
	private boolean _stageEnd; // 阶段是否已经结束
	private long _stageEndTime; // 本阶段结束的时间
	
	public GCompEventsStage(GroupCompetitionStageCfg cfg) {
		_competitionCommonCfgDAO = GCCommonCfgDAO.getInstance();
		_eventsStatusControlTask = new EventsStatusControlTask(this);
	}
	
	private List<String> getTopCountGroupsFromRank() {
		// 从排行榜获取排名靠前的N个帮派数据
		return Collections.emptyList();
	}
	
	private void moveToEventsType(GCEventsType eventsType, List<String> groupIds) {
		// 移到下一个赛事
		_currentEventsType = eventsType;
		_events = new GCompEvents.Builder(groupIds, eventsType).build();
		_events.start();
		GroupCompetitionMgr.getInstance().updateCurrentData(eventsType, groupIds);
		long executeTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(_events.getCurrentStatus().getLastMinutes());
		GCompCommonTask.scheduleCommonTask(_eventStatusSwitcher, this, executeTime); // 结束的时效任务，等待回调
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
		}
	}
	
	private void allEventsFinished() {
		// 所有的赛事完结
		this._stageEnd = true;
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
		IReadOnlyPair<Integer, Integer> timeInfo = _competitionCommonCfgDAO.getCfg().getFightingStageEndTime();
		int hour = timeInfo.getT1().intValue();
		int minute = timeInfo.getT2().intValue();
		Calendar currentDateTime = Calendar.getInstance();
		currentDateTime.add(Calendar.DAY_OF_YEAR, lastDays);
		currentDateTime.set(Calendar.HOUR_OF_DAY, hour);
		currentDateTime.set(Calendar.MINUTE, 0);
		if(minute > 0) {
			currentDateTime.set(Calendar.MINUTE, minute);
		}
		return currentDateTime.getTimeInMillis();
	}
	
	/**
	 * 
	 * 创建一个赛事开始通知的时效任务
	 * 
	 * @param groupIds
	 * @param eventsType
	 * @return
	 */
	private boolean createEventsTypeControlTask(List<String> groupIds, GCEventsType eventsType) {
		boolean startOnNextDay = false;
		GCompEventsStageContext context = new GCompEventsStageContext(groupIds, eventsType);
		Calendar instance = Calendar.getInstance();
		IReadOnlyPair<Integer, Integer> timeInfos = _competitionCommonCfgDAO.getCfg().getFightingStartTime();
		instance.set(Calendar.HOUR, timeInfos.getT1());
		if (timeInfos.getT2() > 0) {
			instance.set(Calendar.MINUTE, timeInfos.getT2());
		} else {
			instance.set(Calendar.MINUTE, 0);
		}
		long millis = instance.getTimeInMillis();
		if (millis < System.currentTimeMillis()) {
			// 如果是过了，则跨一天
			instance.add(Calendar.DAY_OF_YEAR, 1);
			startOnNextDay = true;
		}
		GCompCommonTask.scheduleCommonTask(_eventsStatusControlTask, context, instance.getTimeInMillis()); // 提交一个定时任务，到了赛事正式开始的时间，会初始化
		return startOnNextDay;
	}

	@Override
	public void onStageStart(IGCStage preStage) {
		// 通知阶段开始，但这时候具体的赛事还未开始
		List<String> topCountGroups = getTopCountGroupsFromRank();
		GCEventsType startType;
		if (topCountGroups.size() > 8) {
			startType = GCEventsType.TOP_16;
		} else {
			startType = GCEventsType.TOP_8;
		}
		boolean startOnNextDay = this.createEventsTypeControlTask(topCountGroups, startType);
		this._stageEndTime = calculateEndTime(startType, startOnNextDay);
		GCompMatchDataMgr.getInstance().onEventStageStart(startType);
	}
	
	@Override
	public GCompStageType getStageType() {
		return GCompStageType.EVENTS;
	}
	
	@Override
	public void onStageEnd() {
		
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
	private static class EventsStatusControlTask implements IConsumer<GCompEventsStageContext> {

		private GCompEventsStage _stage;
		
		public EventsStatusControlTask(GCompEventsStage pStage) {
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
