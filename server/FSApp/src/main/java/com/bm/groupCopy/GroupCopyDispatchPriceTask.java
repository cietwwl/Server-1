package com.bm.groupCopy;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

/**
 * 帮派副本自动发送奖励定时器，原先使用TimerManager,后改为这里
 * @author Alex
 *
 * 2016年11月12日 上午11:11:08
 */
public class GroupCopyDispatchPriceTask implements IGameTimerTask{

	@Override
	public String getName() {
		return "GroupCopyAutoSendPriceMailTask";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		GroupCopyMailHelper.getInstance().dispatchGroupWarPrice();
		return null;
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
