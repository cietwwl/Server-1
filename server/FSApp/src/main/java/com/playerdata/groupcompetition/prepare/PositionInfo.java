package com.playerdata.groupcompetition.prepare;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.syn.ISameSceneDataSignal;

@SynClass
public class PositionInfo implements ISameSceneDataSignal{

	private float px;
	
	private float py;
	
	@IgnoreSynField
	private boolean isNewAdd;
	
	@IgnoreSynField
	private boolean isChanged;
	
	@IgnoreSynField
	private boolean isRemoved;

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
	public void setChangedSignal(boolean signal) {
		isChanged = signal;
	}

	@Override
	public void setRemovedSignal(boolean signal) {
		isRemoved = signal;
	}

	@Override
	public void setNewAddSignal(boolean signal) {
		isNewAdd = signal;	
	}

	@Override
	public boolean getChangedSignal() {
		return isChanged;
	}

	@Override
	public boolean getRemovedSignal() {
		return isRemoved;
	}

	@Override
	public boolean getNewAddSignal() {
		return isNewAdd;
	}
}
