package com.bm.rank.arena;

public class FightingMember {
	private final int original;
	protected int timeout;
	private volatile boolean inFightState; // 0不在战斗，1是战斗中
	private volatile long lastFightTime; // 上一次进入战斗的时间

	public FightingMember(int timeout) {
		this.timeout = timeout;
		original = timeout;
	}
	
	protected void resetTimeout(){
		timeout = original;
	}

	/**
	 * 检查战斗状态是否超时，返回是否在战斗中
	 * 
	 * @return
	 */
	public boolean adjustTimeOutState() {
		if (!inFightState) {
			return inFightState;
		}
		long currentTime = System.currentTimeMillis();
		if ((currentTime - this.lastFightTime) < timeout) {
			return inFightState;
		}
		synchronized (this) {
			if (!inFightState) {
				return inFightState;
			}
			if ((currentTime - this.lastFightTime) < timeout) {
				return inFightState;
			}
			inFightState = false;
			this.lastFightTime = 0;
			timeout = original;
		}
		return inFightState;
	}

	public synchronized void forceSetFighting() {
		this.inFightState = true;
		this.lastFightTime = System.currentTimeMillis();
	}

	public boolean setFighting() {
		long currentTime = System.currentTimeMillis();
		synchronized (this) {
			if (this.inFightState && (currentTime - this.lastFightTime) < timeout) {
				return false;
			}
			this.lastFightTime = currentTime;
			this.inFightState = true;
			return true;
		}
	}

	/**
	 * 用队列实现
	 */
	public synchronized void setNotFighting() {
		this.inFightState = false;
		this.lastFightTime = 0;
	}

	public long getLastFightTime() {
		return lastFightTime;
	}
	
}
