package com.bm.worldBoss.state;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;


/**
 * 世界boss状态转换时效，每整分钟转换一次
 * @author Alex
 *
 * 2016年11月22日 下午2:18:04
 */
public class WBStateTranformTask implements IGameTimerTask{

	@Override
	public String getName() {
		return WBStateTranformTask.class.getName();
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		WBStateFSM.getInstance().tranfer();
		return null;
	}

	@Override
	public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rejected(RejectedExecutionException e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isContinue() {
		return true;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		// TODO Auto-generated method stub
		return null;
	}

}
