package com.playerdata.dataSyn.sameSceneSyn;

public abstract class SameSceneDataBaseIF {

	protected boolean isChanged = true;

	protected boolean isRemoved = false;
	
	protected boolean isNewAdd = true;
	
	
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
}
