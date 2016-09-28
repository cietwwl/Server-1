package com.playerdata.groupcompetition.stageimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.data.IGCompStage;
import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.holder.GCompSelectionDataMgr;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompCommonTask;
import com.playerdata.groupcompetition.util.GCompEventsStatus;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.playerdata.groupcompetition.util.GCompTips;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.playerdata.groupcompetition.util.IConsumer;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rwbase.dao.groupcompetition.GroupCompetitionAgainstCfgDAO;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

/**
 * 
 * 帮会争霸：赛事阶段
 * 管理不同赛事之间的切换（16强 ---> 8强 ---> 4强 ---> 决赛）
 * 管理具体赛事的状态切换（备战 ---> 组队战 ---> 休息 ---> 个人战）
 * 
 * @author CHEN.P
 *
 */
public class GCompEventsStage implements IGCompStage {
	
	private static final java.text.SimpleDateFormat _dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private GCEventsType _currentEventsType; // 当前的赛事类型
	private GCompEvents _events; // 当前的争霸赛事
	private EventsTypeSwitcher _eventsTypeSwitcher; // 赛事类型的切换器
	private EventStatusSwitcher _eventStatusSwitcher; // 当前赛事的状态切换
	private boolean _stageEnd; // 阶段是否已经结束
	private long _stageEndTime; // 本阶段结束的时间
	private String _stageCfgId; // 阶段的配置id
	
	public GCompEventsStage(GroupCompetitionStageCfg cfg) {
		_eventsTypeSwitcher = new EventsTypeSwitcher(this);
		_eventStatusSwitcher = new EventStatusSwitcher();
		_stageCfgId = cfg.getCfgId();
	}
	
	private void beforeEventsStart(GCEventsType eventsType, List<String> groupIds) {
		GroupCompetitionMgr.getInstance().updateCurrenEventstData(eventsType, groupIds);
	}
	
	private void scheduleEventsStatusSwitchTask() {
		// 提交赛事控制任务
		int lastMinutes = _events.getCurrentStatus().getLastMinutes();
		long lastMillis;
		if (lastMinutes > 0) {
			lastMillis = TimeUnit.MINUTES.toMillis(lastMinutes);
		} else {
			lastMillis = 1000;
		}
		long endTimeMillis = System.currentTimeMillis() + lastMillis;
		GCompUtil.log("---------- 提交赛事状态控制任务！当前状态：{}， deadLine：{} ---------", _events.getCurrentStatus(), _dateFormatter.format(new Date(endTimeMillis)));
		GCompCommonTask.scheduleCommonTask(_eventStatusSwitcher, this, endTimeMillis); // 结束的时效任务，等待回调
	}
	
	private void startEvents(GCEventsType eventsType, List<String> groupIds, List<String> loseGroupIds) {
		// 切换到某个赛事类型
		List<IReadOnlyPair<Integer, Integer>> againstInfo;
		if(this._currentEventsType == null) {
			// 初赛
			againstInfo = GroupCompetitionAgainstCfgDAO.getInstance().getCfgById(String.valueOf(eventsType.sign)).getAgainstInfoList();
		} else {
			againstInfo = Collections.emptyList();
		}
		if(eventsType == GCEventsType.FINAL) {	
			groupIds = new ArrayList<String>(groupIds);
			groupIds.addAll(loseGroupIds);
		}
		this.beforeEventsStart(eventsType, groupIds);
		_currentEventsType = eventsType;
		_events = new GCompEvents.Builder(groupIds, eventsType).setAgainstsInfo(againstInfo).build();
		_events.start();
		scheduleEventsStatusSwitchTask(); // 具体赛事的状态切换任务
	}
	
	/**
	 * 
	 * 创建一个赛事开始通知的时效任务
	 * 
	 * @param groupIds
	 * @param eventsType
	 * @return 是否在隔天开始
	 */
	private boolean scheduleEventsTypeSwitcher(List<String> groupIds, List<String> loseGroupIds, GCEventsType eventsType) {
		boolean startOnNextDay = false;
		GroupCompetitionStageCfg cfg = GroupCompetitionStageCfgDAO.getInstance().getCfgById(_stageCfgId);
		GCompEventsStageContext context = new GCompEventsStageContext(groupIds, loseGroupIds, eventsType);
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
		GCompUtil.log("---------- 帮派争霸-赛事阶段-创建赛事类型控制任务, 赛事类型：{}，deadline：{}，groupIds : {} ----------", eventsType, _dateFormatter.format(instance.getTime()), groupIds);
		GCompCommonTask.scheduleCommonTask(_eventsTypeSwitcher, context, instance.getTimeInMillis()); // 提交一个定时任务，到了赛事正式开始的时间，会初始化
		return startOnNextDay;
	}
	
	/**
	 * 通知本轮赛事结束
	 */
	private void onEventsTypeEnd() {
		this._events.onEnd();
		if (this._currentEventsType.hasNext()) {
			// 推进到下一轮赛事
			this.scheduleEventsTypeSwitcher(this._events.getWinGroups(), this._events.getLoseGroups(), _currentEventsType.getNext());
		} else {
			// 赛事阶段结束
			this.allEventsFinished();
		}
	}
	
	private void switchEventsStatus() {
		// 切换某场具体赛事的状态
		boolean success = this._events.switchToNextStatus();
		if (!success) {
			this.onEventsTypeEnd();
		} else {
			this.scheduleEventsStatusSwitchTask();
		}
	}
	
	private void allEventsFinished() {
		// 所有的赛事完结
		this._stageEnd = true;
		GCompUtil.log("所有的赛事已经完结！");
	}
	
	/**
	 * 
	 * 计算本阶段的结束时间
	 * 
	 * @param startType
	 * @param startOnNextDay
	 * @return
	 */
	private long calculateEndTime(GCEventsType startType, boolean startOnNextDay) {
		int lastDays = startType.getDaysNeededToFinal();
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
		GCompEventsDataMgr.getInstance().onEventStageStart(startType); // 清理上一次的数据，需要在开始前调用
		List<String> loseGroupIds = Collections.emptyList();
		this.startEvents(startType, topCountGroups, loseGroupIds); // 切换到具体赛事类型
		this._stageEndTime = calculateEndTime(startType, false);
		GCompUtil.sendMarquee(GCompTips.getTipsEnterEventsStage());
	}
	
	@Override
	public GCompStageType getStageType() {
		return GCompStageType.EVENTS;
	}
	
	@Override
	public void onStageEnd() {
		GCompUtil.log("收到stageEnd事件！");
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
	private static class EventsTypeSwitcher implements IConsumer<GCompEventsStageContext> {

		private GCompEventsStage _stage;
		
		public EventsTypeSwitcher(GCompEventsStage pStage) {
			this._stage = pStage;
		}
		
		@Override
		public void accept(GCompEventsStageContext context) {
			_stage.startEvents(context.getStatus(), context.getGroupIds(), context.getLoseGroupIds());
		}
		
	}
	
	private static class EventStatusSwitcher implements IConsumer<GCompEventsStage> {
		
		@Override
		public void accept(GCompEventsStage stage) {
			stage.switchEventsStatus();
		}
		
	}
}
