package com.playerdata.groupcompetition.holder;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class SynToAllTask<T> implements IGameTimerTask  {
	
	private T _synData; // 同步的数据
	private eSynType _synType; // 同步的类型
	private eSynOpType _synOpType; // 同步的动作
	
	public static <T> void createNewTaskAndSubmit(T synData, eSynType synType, eSynOpType synOpType) {
		SynToAllTask<T> task = new SynToAllTask<T>(synData, synType, synOpType);
		FSGameTimerMgr.getInstance().createSecondTaskSubmitInfo(task, 1);
	}
	
	protected SynToAllTask(T pSynData, eSynType pSynType, eSynOpType pSynOpType) {
		this._synData = pSynData;
		this._synType = pSynType;
		this._synOpType = pSynOpType;
	}

	@Override
	public String getName() {
		return "SynToAllTask";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		ClientDataSynMgr.synDataMutiple(PlayerMgr.getInstance().getOnlinePlayers(), _synData, _synType, _synOpType);
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
