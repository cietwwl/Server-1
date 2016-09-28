package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;

import com.playerdata.groupcompetition.util.GCompCommonConfig;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class GroupCompetitionBroadcastCenter {

	private static final GroupCompetitionBroadcastCenter _INSTANCE = new GroupCompetitionBroadcastCenter();
	
	private final GroupCompetitionBroadcastTask _task = new GroupCompetitionBroadcastTask();
	
	public static final GroupCompetitionBroadcastCenter getInstance() {
		return _INSTANCE;
	}
	
	GroupCompetitionBroadcastCenter() {
	}
	
	public void onEventsStart() {
		_task._on = true;
		_task._broadcastQueue.clear();
		FSGameTimerMgr.getInstance().submitSecondTask(_task, GCompCommonConfig.getBroadcastIntervalSeconds());
	}
	
	public void onEventsEnd() {
		_task._on = false;
	}
	
	public void addBroadcastMsg(String msg) {
		GCompUtil.log("添加一条广播消息:{}", msg);
		_task._broadcastQueue.add(msg);
	}
	
	private static class GroupCompetitionBroadcastTask implements IGameTimerTask {
		
		private boolean _on = false;
		
		private Queue<String> _broadcastQueue = new ConcurrentLinkedQueue<String>();

		@Override
		public String getName() {
			return "GroupCompetitionBroadcastTask";
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			if (_broadcastQueue.size() > 0) {
				List<String> copy = new ArrayList<String>(_broadcastQueue);
				_broadcastQueue.clear();
				for (String str : copy) {
					GCompUtil.sendMarquee(str);
				}
			}
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
			return _on;
		}

		@Override
		public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
			return null;
		}
		
	}
}
