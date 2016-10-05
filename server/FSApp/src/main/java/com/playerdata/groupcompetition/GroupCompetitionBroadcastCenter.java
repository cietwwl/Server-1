package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;

import com.playerdata.groupcompetition.util.GCompCommonConfig;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;
import com.rwproto.MainMsgProtos.EMsgType;

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
	
	public void addBroadcastMsg(Integer pmdId, List<String> arr) {
		GCompUtil.log("添加一条广播消息:{}, {}", pmdId, arr);
		_task._broadcastQueue.add(Pair.CreateReadonly(pmdId, arr));
	}
	
	private static class GroupCompetitionBroadcastTask implements IGameTimerTask {
		
		private boolean _on = false;
		
		private Queue<IReadOnlyPair<Integer, List<String>>> _broadcastQueue = new ConcurrentLinkedQueue<IReadOnlyPair<Integer, List<String>>>();

		@Override
		public String getName() {
			return "GroupCompetitionBroadcastTask";
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			if (_broadcastQueue.size() > 0) {
				List<IReadOnlyPair<Integer, List<String>>> copy = new ArrayList<IReadOnlyPair<Integer, List<String>>>(_broadcastQueue);
				_broadcastQueue.clear();
				for (IReadOnlyPair<Integer, List<String>> msg : copy) {
					MainMsgHandler.getInstance().sendMainCityMsg(msg.getT1().intValue(), EMsgType.GroupCompetitionMsg, msg.getT2());
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
