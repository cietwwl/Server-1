package com.playerdata.groupFightOnline.state;

public abstract class IGFightState {
	protected int resourceID = 0;
	
	public IGFightState(int resourceID){
		this.resourceID = resourceID;
	}

	public abstract void Enter();

	public abstract boolean canExit();	
	
	public abstract IGFightState getNext();
	
}
