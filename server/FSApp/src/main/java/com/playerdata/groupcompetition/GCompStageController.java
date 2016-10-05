package com.playerdata.groupcompetition;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.playerdata.groupcompetition.data.IGCompStage;
import com.playerdata.groupcompetition.util.GCompCommonTask;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.playerdata.groupcompetition.util.IConsumer;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

/**
 * 
 * <pre>
 * 帮派争霸阶段控制器，负责切换不同的阶段
 * {@link #start(long)}：controller启动，参数是第一个stage的启动时间。
 * 内部机制是提交一个{@link StageStartConsumer}到{@link GCompCommonTask}中
 * {@link StageStartConsumer}到时间回调{@link #moveToNextStage()}方法
 * 
 * {@link #moveToNextStage()}会提交一个{@link StageEndMonitorConsumer}任务，监控当前stage的结束
 * 如果还有下一个stage的话，会再提交一个{@link StageStartConsumer}任务，监控下一个stage的开始
 * 
 * {@link #moveToNextStage()}会重复被执行，直到所有阶段都结束
 * 
 * ------------------------
 * |        start         |
 * ------------------------
 *            |
 *            |
 * ------------------------
 * |   StageStartConsumer |
 * ------------------------
 *            |
 *            |
 *            V
 * ------------------------          ------------------------
 * |    moveToNextStage   | -------- |StageEndMonitorConsumer|                      
 * ------------------------ <-|      ------------------------ 
 *            |               |                 |
 *            |               |                 |
 *            V               |                 V
 * ------------------------   |      ------------------------
 * |   StageStartConsumer | --       | notifyCurrentStageEnd|
 * ------------------------          ------------------------
 * </pre>
 * 
 * @author CHEN.P
 *
 */
public class GCompStageController {
	
	private static final java.text.SimpleDateFormat _dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private LinkedList<IGCompStage> _stageQueue = new LinkedList<IGCompStage>(); // 阶段队列
	private IGCompStage _currentStage = null; // 当前的阶段
	private int _sessionId; // 第几届
	private Object _firstStageStartPara;
	
	public GCompStageController(List<IGCompStage> stageQueue, int pSessionId, Object firstStageStartPara) {
		this._stageQueue.addAll(stageQueue);
		this._sessionId = pSessionId;
		this._firstStageStartPara = firstStageStartPara;
	}
	
	private void createTimerTask(IConsumer<GCompStageController> consumer, long deadline) {
		GCompCommonTask.scheduleCommonTask(consumer, this, deadline);
		GCompUtil.log("---------- 帮派争霸-阶段控制器-提交时效任务, 执行时间 : {}, 任务名称：{} ----------" , _dateFormatter.format(new java.util.Date(deadline)), consumer.getClass().getSimpleName());
	}
	
	private void notifyCurrentStageEnd() {
		// 通知当前阶段结束
		if (_currentStage != null) {
			GCompUtil.log("---------- 【{}】结束 ----------", _currentStage.getStageType().getDisplayName());
			_currentStage.onStageEnd();
		}
	}
	
	private void fireStageChangeEvent() {
		GroupCompetitionMgr.getInstance().notifyStageChange(_currentStage, _sessionId);
	}
	
	private void scheduleNextStageStartTask() {
		if (_stageQueue.size() > 0) {
			IGCompStage temp = _stageQueue.getFirst();
			GroupCompetitionStageCfg stageCfg = GroupCompetitionStageCfgDAO.getInstance().getCfgById(temp.getStageCfgId());
			long endTime;
			if (stageCfg.isStartImmediately()) {
				endTime = _currentStage.getStageEndTime() + 1000;
			} else {
				IReadOnlyPair<Integer, Integer> startTimeInfo = stageCfg.getStartTimeInfo();
				endTime = GCompUtil.getNearTimeMillis(startTimeInfo.getT1().intValue(), startTimeInfo.getT2().intValue(), _currentStage.getStageEndTime());
			}
			this.createTimerTask(new StageStartConsumer(), endTime);
		} else {
			// 没有下一个阶段，直接提交一个任务，然后等待切换到新的一轮
			this.createTimerTask(new StageStartConsumer(), _currentStage.getStageEndTime() + 1000);
		}
	}
	
	private void moveToNextStage() {
		if (_stageQueue.size() > 0) {
			// 移到下一个阶段
			IGCompStage pre = _currentStage;
			_currentStage = _stageQueue.removeFirst();
			// 下一阶段的相关逻辑
			_currentStage.onStageStart(pre, _firstStageStartPara);
			GCompUtil.log("---------- 【{}】开始 ----------",  _currentStage.getStageType().getDisplayName());
			long endTime = _currentStage.getStageEndTime();
			createTimerTask(new StageEndMonitorConsumer(), endTime);
			scheduleNextStageStartTask();
			fireStageChangeEvent();
		} else {
			// 没有下一个了，即本轮的所有阶段都已经结束了
			GroupCompetitionMgr.getInstance().allStageEndOfCurrentRound();
		}
	}
	
	private long calculateEndTime() {
		GroupCompetitionStageCfgDAO dao = GroupCompetitionStageCfgDAO.getInstance();
		IGCompStage stage;
		GroupCompetitionStageCfg cfg;
		int days = 0;
		IReadOnlyPair<Integer, Integer> timeInfo = null;
		for (int i = 0, size = _stageQueue.size(); i < size; i++) {
			stage = _stageQueue.get(i);
			cfg = dao.getCfgById(stage.getStageCfgId());
			days += cfg.getLastDays();
			timeInfo = cfg.getEndTimeInfo();
		}
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_YEAR, days);
		instance.set(Calendar.HOUR_OF_DAY, timeInfo.getT1());
		instance.set(Calendar.MINUTE, timeInfo.getT2());
		return instance.getTimeInMillis();
	}
	
	/**
	 * 
	 * 第一阶段开始的时间
	 * 如果firstStageStartTime比当前时间要小，则会直接开始
	 * 
	 * @param firstStageStartTime 第一次开始的时间
	 */
	public void start(long firstStageStartTime) {
		if(firstStageStartTime <= System.currentTimeMillis()) {
			this.moveToNextStage();
		} else {
			createTimerTask(new StageStartConsumer(), firstStageStartTime);
		}
		GroupCompetitionMgr.getInstance().updateEndTimeOfCurrentSession(this.calculateEndTime());
	}
	
	private static class StageEndMonitorConsumer implements IConsumer<GCompStageController> {

		@Override
		public void accept(GCompStageController controller) {
			controller.notifyCurrentStageEnd();
		}
		
	}
	
	private static class StageStartConsumer implements IConsumer<GCompStageController> {

		@Override
		public void accept(GCompStageController controller) {
			controller.moveToNextStage();
		}
		
	}
}
