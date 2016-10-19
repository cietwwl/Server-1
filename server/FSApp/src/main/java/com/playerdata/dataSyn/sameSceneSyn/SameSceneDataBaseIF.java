package com.playerdata.dataSyn.sameSceneSyn;

public abstract class SameSceneDataBaseIF {

	protected boolean isChanged = true;

	protected boolean isRemoved = false;
	
	protected boolean isNewAdd = true;
	
	protected long disConnTime = 0;
	
	public synchronized boolean isChanged() {
		return isChanged;
	}

	public synchronized void setChanged(boolean changed) {
		this.isChanged = changed;
	}

	public synchronized boolean isRemoved() {
		return this.isRemoved;
	}

	public synchronized void setRemoved(boolean removed) {
		this.isRemoved = removed;
	}

	public synchronized boolean isNewAdd() {
		return isNewAdd;
	}

	public synchronized void setNewAdd(boolean newAdd) {
		this.isNewAdd = newAdd;
	}

	/**
	 *  判断是否断线
	 * @return
	 */
	public boolean isDisConn(long currentTime) {	
		return disConnTime > 0 && currentTime - disConnTime > 60 * 1000;
	}

	/**
	 * 设置断线时间
	 * @param disConnTime 0表示未断线
	 */
	public void setDisConnTime(long disConnTime) {
		if(disConnTime != 0 && this.disConnTime != 0) {
			return;
		}
		this.disConnTime = disConnTime;
	}
}
