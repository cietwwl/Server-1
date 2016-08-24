package com.rwbase.common.timer.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.log.GameLog;
import com.playerdata.Player;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.IPlayerGatherer;
import com.rwbase.common.timer.IPlayerOperable;

/**
 * 
 * 通用时效任务的实现
 * 
 * @author CHEN.P
 *
 */
public class FSGamePlayerOperationTask implements IGameTimerTask {

	private String _uuid; // 時效任務的uuid，用于作为标识
	private String _name; // 時效任務的名字
	private boolean _needRecordData; // 本任務的所有子任務是否需要保存最後一次執行的時間
	protected final Map<Integer, FSGamePlayerOperationSubTask> operationList; // 本时效任务的玩家操作行为
	protected final IPlayerGatherer defaultPlayerGatherer; // 默认的角色收集器
	
	FSGamePlayerOperationTask(IPlayerGatherer playerGatherer, boolean needRecordData) {
		this._uuid = java.util.UUID.randomUUID().toString();
		this._name = this.getClass().getSimpleName() + "@" + _uuid;
		this.operationList = new HashMap<Integer, FSGamePlayerOperationSubTask>();
		this.defaultPlayerGatherer = playerGatherer;
		this._needRecordData = needRecordData;
	}
	
	void notifyPlayerLogin(Player player) {
		// 处理角色登录事件
		for (FSGamePlayerOperationSubTask task : operationList.values()) {
			task.playerLogin(player);
		}
	}
	
	void addOperator(int operatorType, IPlayerOperable operator) {
		if (operator == null) {
			throw new NullPointerException("operator不能為null！類型：" + operatorType);
		}
		// 添加一個PlayerOperable到列表當中
		synchronized(operationList) {
			FSGamePlayerOperationSubTask pre = this.operationList.put(operatorType, new FSGamePlayerOperationSubTask(operator, operatorType, this._needRecordData));
			if(pre != null) {
				throw new RuntimeException("重複的operatorType：" + operatorType + "，上一個實例是：" + pre._operator + "，當前實例是：" + operator);
			}
		}
	}
	
	IPlayerOperable removeOperator(int operatorType) {
		// 從列表中移除一個IplayerOperator
		synchronized (operationList) {
			FSGamePlayerOperationSubTask target = this.operationList.remove(operatorType);
			if (target != null) {
				return target._operator;
			} else {
				return null;
			}
		}
	}
	
	protected final List<FSGameTimeSignal> execute(Collection<FSGamePlayerOperationSubTask> taskList) {
		if (this.operationList.isEmpty()) {
			return Collections.emptyList();
		}
		List<FSGameTimeSignal> list = new ArrayList<FSGameTimeSignal>(operationList.size());
		List<Player> allPlayers = this.defaultPlayerGatherer.gatherPlayers();
		List<Player> allPlayersRO = Collections.unmodifiableList(allPlayers);
		List<FSGameTimeSignal> tasksUsingAllPlayers = new ArrayList<FSGameTimeSignal>();
		FSGamePlayerOperationSubTask subTask;
		for (Iterator<FSGamePlayerOperationSubTask> itr = taskList.iterator(); itr.hasNext();) {
			subTask = itr.next();
			boolean usingAll = false;
			if (subTask._operator instanceof IPlayerGatherer) {
				// 如果operator同时是IPlayerGatherer的实例，则按照他的规则来获取player
				subTask._players = ((IPlayerGatherer) subTask._operator).gatherPlayers();
			} else {
				subTask._players = allPlayersRO;
				usingAll = true;
			}
			// 提交一个毫秒时效任务，多线程执行，尽量不影响其他operator的执行
			FSGameTimeSignal signal = FSGameTimerMgr.getTimerInstance().newTimeSignal(subTask, 0, TimeUnit.MILLISECONDS, false);
			list.add(signal);
			if (usingAll) {
				tasksUsingAllPlayers.add(signal);
			}
		}
		if (tasksUsingAllPlayers.size() > 0) {
			AllPlayerMonitorTask monitor = new AllPlayerMonitorTask();
			monitor._monitorList = tasksUsingAllPlayers;
			monitor._monitorPlayers = allPlayers;
			FSGameTimerMgr.getTimerInstance().newTimeSignal(monitor, 0, TimeUnit.MILLISECONDS, false);
		} else {
			allPlayers.clear();
		}
		return list;
	}
	
	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		synchronized (operationList) {
			this.execute(operationList.values());
		}
		return "DONE";
	}

	@Override
	public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
		
	}

	@Override
	public void rejected(RejectedExecutionException e) {
		
	}

	@Override
	public boolean isContinue() {
		// always continue
		return true;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		return Collections.emptyList();
	}
	
	protected static class FSGamePlayerOperationSubTask implements IGameTimerTask {
		
		private IPlayerOperable _operator;
		private List<Player> _players;
		private final Queue<Player> _tempPlayers;
		private String _name;
		private long _lastExecuteTime; // 上一次执行的时间
		private final AtomicBoolean _executing = new AtomicBoolean(); // 是否正在执行中
		private final List<String> _lastExecutePlayers; // 上一次被操作的player信息
		private final boolean _needRecordData;
		private int _operatorType;
		
		public FSGamePlayerOperationSubTask(IPlayerOperable pOperator, int operatorType, boolean pNeedRecordData) {
			this._operator = pOperator;
			this._name = this.getClass().getSimpleName() + " for " + _operator;
			this._lastExecutePlayers = new ArrayList<String>();
			this._tempPlayers = new ConcurrentLinkedQueue<Player>();
			this._needRecordData = pNeedRecordData;
			this._operatorType = operatorType;
		}
		
		private void executeSingle(Player player) {
			if (player.isRobot()) {
				return;
			}
			try {
				this._operator.operate(player);
				this._lastExecutePlayers.add(player.getUserId());
			} catch (Exception e) {
				GameLog.error("FSGamePlayerOperationSubTask", "executeSingle", "执行出现错误！playerId：" + player.getUserId() + ", operator=" + _operator.getClass());
			}
		}
		
		void playerLogin(Player player) {
			if (this._operator.isInterestingOn(player)) { 
				if (_executing.get()) {
					this._tempPlayers.add(player);
				} else if (this._lastExecuteTime > 0 && !_lastExecutePlayers.contains(player.getUserId())) {
					this._operator.operate(player);
				}
			}
		}
		
		protected IPlayerOperable getOperator() {
			return _operator;
		}
		
		protected int getOperatorType() {
			return _operatorType;
		}

		@Override
		public String getName() {
			return this._name;
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			this._executing.getAndSet(true);
			this._lastExecutePlayers.clear();
			if(_players instanceof RandomAccess) {
				for (int i = _players.size(); i-- > 0;) {
					this.executeSingle(_players.get(i));
				}
			} else {
				for(Iterator<Player> itr = _players.iterator(); itr.hasNext();) {
					this.executeSingle(itr.next());
				}
			}
			if (_tempPlayers.size() > 0) {
				Player temp;
				while ((temp = _tempPlayers.poll()) != null) {
					this.executeSingle(temp);
				}
			}
			this._lastExecuteTime = System.currentTimeMillis();
			this._executing.getAndSet(false);
			this._players = null;
			if(_needRecordData) {
				FSGameTimerSaveData.getInstance().updateLastExecuteTimeOfPlayerTask(_operatorType, System.currentTimeMillis());
			}
			return "DONE";
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
	
	protected static class AllPlayerMonitorTask implements IGameTimerTask {
		
		private List<FSGameTimeSignal> _monitorList;
		private List<Player> _monitorPlayers;

		@Override
		public String getName() {
			return "AllPlayerMonitorTask@" + this.hashCode();
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			if (_monitorList != null && _monitorList.size() > 0) {
				while (_monitorList.size() > 0) {
					for (Iterator<FSGameTimeSignal> itr = _monitorList.iterator(); itr.hasNext();) {
						if (itr.next().isDone()) {
							itr.remove();
						}
					}
					if (Thread.currentThread().isInterrupted()) {
						Thread.currentThread().interrupt();
						break;
					}
				}
				GameLog.info("FSGamePlayerOperationTask", "onTimeSignal", "清理monitorPlayers！清理前的size=" + _monitorPlayers.size());
				_monitorPlayers.clear();
			}
			return "DONE";
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
	
}
