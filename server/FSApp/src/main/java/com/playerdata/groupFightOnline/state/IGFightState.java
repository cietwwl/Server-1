package com.playerdata.groupFightOnline.state;

import com.playerdata.groupFightOnline.enums.GFResourceState;

public abstract class IGFightState {
	protected int resourceID = 0;
	protected GFResourceState resState = GFResourceState.INIT;
	
	public IGFightState(int resourceID, GFResourceState resState){
		this.resourceID = resourceID;
		this.resState = resState;
	}
	
	public int getStateValue(){
		return resState.getValue();
	}

	public abstract void Enter();

	public boolean canExit(GFResourceState currentState){
		return !currentState.equals(resState);
	}
	
	public abstract IGFightState getNext();

}
