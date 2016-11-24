package com.bm.targetSell.param;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.RejectedExecutionException;

import com.bm.targetSell.TargetSellManager;
import com.rw.manager.ServerSwitch;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class TargetSellSendTask implements IGameTimerTask{

	private final static long ONE_MINUTE = 60*1000;
	
	@Override
	public String getName() {
		return "TargetSellSendTask";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		
		long current = System.currentTimeMillis();
		for (Iterator<Entry<String, TargetSellRoleChange>> iterator = TargetSellManager.RoleAttrChangeMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, TargetSellRoleChange> entry = iterator.next();
			TargetSellRoleChange value = entry.getValue();
			if(current - value.getStartTime() >= ONE_MINUTE){
				iterator.remove();
				TargetSellManager.getInstance().packHeroChangeAttr(entry.getKey(), value);
				TargetSellManager.getInstance().packAndSendMsg(value);
			}
		}
		
		//检查一下角色退出列表
		TargetSellManager.getInstance().checkLogOutRoleList();
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
		return true;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		return null;
	}

}
