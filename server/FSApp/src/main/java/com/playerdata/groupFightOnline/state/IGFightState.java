package com.playerdata.groupFightOnline.state;

public interface IGFightState {

	public void Enter();

	public boolean canExit();	
	
	public IGFightState getNext();
	
}
