package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import com.playerdata.groupcompetition.data.CompetitionStage;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

/**
 * 
 * 帮派争霸阶段控制器，负责切换不同的阶段
 * 
 * @author CHEN.P
 *
 */
public class CompetitionStageController implements IGameTimerTask {

	private List<CompetitionStage> _stageQueueOrigin = new ArrayList<CompetitionStage>(); // 原生的阶段队列
	private LinkedList<CompetitionStage> _stageQueue = new LinkedList<CompetitionStage>(); // 阶段队列
	private CompetitionStage _currentStage = null; // 当前的阶段
	
	public CompetitionStageController(List<CompetitionStage> stageQueueOrigin, List<CompetitionStage> stageQueue) {
		this._stageQueueOrigin.addAll(stageQueueOrigin);
		this._stageQueue.addAll(stageQueue);
	}
	
	/**
	 * 
	 * 第一阶段开始的时间
	 * 
	 * @param firstStageStartTime
	 */
	public void start(long firstStageStartTime) {
		createTimerTask(firstStageStartTime);
	}
	
	private void createTimerTask(long deadline) {
		// 开始一个时效任务
		long delay = System.currentTimeMillis() - deadline;
		int second = (int) TimeUnit.MILLISECONDS.toSeconds(delay);
		long millis = TimeUnit.SECONDS.toMillis(second);
		if (delay - millis > 500) {
			second++;
		}
		FSGameTimerMgr.getInstance().submitSecondTask(this, second);
	}
	
	private void notifyCurrentStageEnd() {
		// 通知当前阶段结束
		if (_currentStage != null) {
			System.out.println("阶段结束：" + _currentStage);
			_currentStage.onStageEnd();
		}
	}
	
	private void startNewRound() {
		// 开始新的一轮
		this._stageQueue.clear();
		this._stageQueue.addAll(_stageQueueOrigin);
	}
	
	private void moveToNextStage() {
		if(_stageQueue.isEmpty()) {
			// 没有下一个了
			this.startNewRound();
			return;
		}
		// 移到下一个阶段
		CompetitionStage pre = _currentStage;
		CompetitionStage next = _stageQueue.removeFirst();
		next.onStageStart(pre);
		_currentStage = pre;
		long endTime = _currentStage.getStageEndTime();
		createTimerTask(endTime);
	}

	@Override
	public String getName() {
		return "CompetitionStageController";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		notifyCurrentStageEnd();
		moveToNextStage();
		return "SUCCESS";
	}

	@Override
	public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
		
	}

	@Override
	public void rejected(RejectedExecutionException e) {
		
	}

	@Override
	public boolean isContinue() {
		return false;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		return Collections.emptyList();
	}
}
