package com.playerdata.groupcompetition;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.playerdata.groupcompetition.data.IGCompStage;
import com.playerdata.groupcompetition.holder.GCompBaseInfoMgr;
import com.playerdata.groupcompetition.util.GCompCommonTask;
import com.playerdata.groupcompetition.util.GCompStageType;
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
	
	public GCompStageController(List<IGCompStage> stageQueue) {
		this._stageQueue.addAll(stageQueue);
	}
	
	private void createTimerTask(IConsumer<GCompStageController> consumer, long deadline) {
		GCompCommonTask.scheduleCommonTask(consumer, this, deadline);
		System.err.println("----------帮派争霸-阶段控制器-提交时效任务, deadLine : " + _dateFormatter.format(new java.util.Date(deadline)) + "----------");
	}
	
	private void notifyCurrentStageEnd() {
		// 通知当前阶段结束
		if (_currentStage != null) {
			System.err.println("----------阶段结束：" + _currentStage.getStageType() + "----------");
			_currentStage.onStageEnd();
		}
	}
	
	private void fireStageChangeEvent(GCompStageType currentStageType) {
		GCompBaseInfoMgr.getInstance().update(currentStageType);
		if (currentStageType == GCompStageType.SELECTION) {
			GroupCompetitionMgr.getInstance().updateLaseHeldTime(System.currentTimeMillis());
		}
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
				Calendar instance = Calendar.getInstance();
				instance.setTimeInMillis(_currentStage.getStageEndTime());
				int hour = startTimeInfo.getT1().intValue();
				if(instance.get(Calendar.HOUR_OF_DAY) > hour) {
					instance.add(Calendar.DAY_OF_YEAR, 1);
				}
				instance.set(Calendar.HOUR_OF_DAY, hour);
				instance.set(Calendar.MINUTE, startTimeInfo.getT2().intValue());
				instance.set(Calendar.SECOND, 0);
				endTime = instance.getTimeInMillis();
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
			_currentStage.onStageStart(pre);
			System.err.println("----------新阶段开始，当前阶段：" + _currentStage.getStageType() + "----------");
			long endTime = _currentStage.getStageEndTime();
			createTimerTask(new StageEndMonitorConsumer(), endTime);
			scheduleNextStageStartTask();
			fireStageChangeEvent(_currentStage.getStageType());
		} else {
			// 没有下一个了，即本轮的所有阶段都已经结束了
			GroupCompetitionMgr.getInstance().allStageEndOfCurrentRound();
		}
	}
	
	/**
	 * 
	 * 第一阶段开始的时间
	 * 
	 * @param firstStageStartTime
	 */
	public void start(long firstStageStartTime) {
		createTimerTask(new StageStartConsumer(), firstStageStartTime);
		GCompBaseInfoMgr.getInstance().update(firstStageStartTime);
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
