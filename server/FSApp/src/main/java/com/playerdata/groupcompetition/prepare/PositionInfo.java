package com.playerdata.groupcompetition.prepare;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.dataSyn.sameSceneSyn.SameSceneDataBaseIF;

@SynClass
public class PositionInfo implements SameSceneDataBaseIF{

	private float px;
	
	private float py;
	
	@IgnoreSynField
	private boolean isChanged = true;
	
	@IgnoreSynField
	private boolean isRemoved = false;
	
	@IgnoreSynField
	private boolean isNewAdd = true;

	public float getPx() {
		return px;
	}

	public void setPx(float px) {
		this.px = px;
	}

	public float getPy() {
		return py;
	}

	public void setPy(float py) {
		this.py = py;
	}

	@Override
	public synchronized boolean isChanged() {
		return isChanged;
	}

	@Override
	public synchronized void setChanged(boolean changed) {
		this.isChanged = changed;
	}

	@Override
	public synchronized boolean isRemoved() {
		return this.isRemoved;
	}

	@Override
	public synchronized void setRemoved(boolean removed) {
		this.isRemoved = removed;
	}

	@Override
	public boolean isNewAdd() {
		return isNewAdd;
	}

	@Override
	public void setNewAdd(boolean newAdd) {
		this.isNewAdd = newAdd;
	}
}
