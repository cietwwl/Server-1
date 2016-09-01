package com.playerdata.groupcompetition.util;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class GCompCommonTask<T> implements IGameTimerTask {

	private IConsumer<T> _task;
	private T _para;
	
	public static <T> void scheduleCommonTask(IConsumer<T> consumer, T para, long executeTime) {
		if (consumer == null) {
			throw new NullPointerException("consumer is null!");
		}
		long systemCurrentMillis = System.currentTimeMillis();
		if (executeTime < systemCurrentMillis) {
			throw new IllegalArgumentException("executeTime在当前时间之前！");
		}
		GCompCommonTask<T> task = new GCompCommonTask<T>();
		task._task = consumer;
		task._para = para;
		long delay = executeTime - systemCurrentMillis;
		long second = TimeUnit.MILLISECONDS.toSeconds(delay);
		long millis = TimeUnit.SECONDS.toMillis(second);
		long sub = delay - millis;
		if (sub >= 500) {
			second++;
		}
		GCompUtil.log("---------- 帮派争霸-一般时效任务-提交时效任务, consumer : {}, 延迟秒数 = {} ----------" , consumer , second);
		FSGameTimerMgr.getInstance().submitSecondTask(task, (int) second);
	}
	
	@Override
	public String getName() {
		return "CompetitionEventsStartCaller";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		_task.accept(_para);
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
