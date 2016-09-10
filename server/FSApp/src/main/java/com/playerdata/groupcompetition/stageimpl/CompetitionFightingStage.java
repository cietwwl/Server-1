package com.playerdata.groupcompetition.stageimpl;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.playerdata.groupcompetition.cfg.CompetitionCommonCfgDAO;
import com.playerdata.groupcompetition.data.CompetitionStage;
import com.playerdata.groupcompetition.util.CompetitionCommonTask;
import com.playerdata.groupcompetition.util.IConsumer;

/**
 * 
 * 帮会争霸：赛事阶段
 * 
 * @author CHEN.P
 *
 */
public class CompetitionFightingStage implements CompetitionStage {
	
	private CompetitionEventsStatus _currentFightingStatus; // 当前的比赛状态
	private CompetitionCommonCfgDAO _competitionCommonCfgDAO; // 配置读取
	private CompetitionEvents _events; // 争霸赛事
	private EventsStatusControlTask _eventsStatusControlTask; // 赛事状态控制任务
	private EventsEndControlTask _eventsEndControlTask; // 每一轮赛事结束的控制任务
	private boolean _stageEnd;
	
	public CompetitionFightingStage() {
		_competitionCommonCfgDAO = CompetitionCommonCfgDAO.getInstance();
		_eventsStatusControlTask = new EventsStatusControlTask(this);
		_eventsEndControlTask = new EventsEndControlTask(this);
	}
	
	private List<String> getTopCountGroupsFromRank() {
		// 从排行榜获取排名靠前的N个帮派数据
		return Collections.emptyList();
	}
	
	private void moveToStatus(CompetitionEventsStatus status, List<String> groupIds) {
		// 移到下一个状态
		_currentFightingStatus = status;
		_events = new CompetitionEvents(groupIds, _currentFightingStatus);
		_events.start();
		long delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(_competitionCommonCfgDAO.getCfg().getMinutesPerCompetition());
		CompetitionCommonTask.scheduleCommonTask(_eventsEndControlTask, Boolean.TRUE, delay); // 结束的时效任务，等待回调
	}
	
	/**
	 * 通知本轮赛事结束
	 */
	private void evnetsEnd() {
		this._events.onEnd();
		if (this._currentFightingStatus.hasNext()) {
			// 推进到下一轮赛事
			this.createEventsStatusControlTask(this._events.getWinGroups(), _currentFightingStatus.getNex());
		} else {
			// 阶段结束
			this.onStageEnd();
		}
	}
	
	private void onStageEnd() {
		this._stageEnd = true;
	}
	
	private void createEventsStatusControlTask(List<String> groupIds, CompetitionEventsStatus status) {
		CompetitionFightingStageContext context = new CompetitionFightingStageContext(groupIds, status);
		Calendar instance = Calendar.getInstance();
		List<Integer> timeInfos = _competitionCommonCfgDAO.getCfg().getFightingStartTimeInfos();
		instance.set(Calendar.HOUR, timeInfos.get(0));
		if (timeInfos.size() > 1) {
			instance.set(Calendar.MINUTE, timeInfos.get(1));
		} else {
			instance.set(Calendar.MINUTE, 0);
		}
		long millis = instance.getTimeInMillis();
		if (millis < System.currentTimeMillis()) {
			// 如果是过了，则跨一天
			instance.add(Calendar.DAY_OF_YEAR, 1);
		}
		CompetitionCommonTask.scheduleCommonTask(_eventsStatusControlTask, context, instance.getTimeInMillis()); // 提交一个定时任务，到了赛事正式开始的时间，会初始化
	}

	@Override
	public void onStageStart(CompetitionStage preStage) {
		// 通知阶段开始，但这时候具体的赛事还未开始
		List<String> topCountGroups = getTopCountGroupsFromRank();
		CompetitionEventsStatus status;
		if (topCountGroups.size() > 8) {
			status = CompetitionEventsStatus.ROUND_OF_16;
		} else {
			status = CompetitionEventsStatus.ROUND_OF_8;
		}
		this.createEventsStatusControlTask(topCountGroups, status);
	}

	@Override
	public boolean isStageEnd() {
		return _stageEnd;
	}
	
	/**
	 * 
	 * 赛事阶段的控制器
	 * 
	 * @author CHEN.P
	 *
	 */
	private static class EventsStatusControlTask implements IConsumer<CompetitionFightingStageContext> {

		private CompetitionFightingStage _stage;
		
		public EventsStatusControlTask(CompetitionFightingStage pStage) {
			this._stage = pStage;
		}
		
		@Override
		public void accept(CompetitionFightingStageContext context) {
			_stage.moveToStatus(context.getStatus(), context.getGroupIds());
		}
		
	}
	
	private static class EventsEndControlTask implements IConsumer<Boolean> {

		private CompetitionFightingStage _stage;
		
		public EventsEndControlTask(CompetitionFightingStage pStage) {
			this._stage = pStage;
		}
		
		@Override
		public void accept(Boolean present) {
			_stage.evnetsEnd();
		}
		
	}
}
