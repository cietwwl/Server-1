package com.playerdata.randomname;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class RandomNameGenerateTask implements IGameTimerTask {

	private List<String> _allNames;
	private AtomicInteger _currentId;
	private Queue<String> _usableNames;

	public RandomNameGenerateTask(List<String> pAllNames, AtomicInteger pCurrentId, Queue<String> pUsableNames) {
		this._allNames = pAllNames;
		this._currentId = pCurrentId;
		this._usableNames = pUsableNames;
	}

	@Override
	public String getName() {
		return "RandomNameGenerateTask@" + this.hashCode();
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		List<String> names = RandomNameMgr.getInstance().generateNames(_allNames, _currentId);
		Collections.shuffle(names, new Random());
		_usableNames.addAll(names);
		return "finished";
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
		return null;
	}

}
