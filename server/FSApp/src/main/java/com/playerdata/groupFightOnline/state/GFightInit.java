package com.playerdata.groupFightOnline.state;

public class GFightInit implements IGFightState{

	@Override
	public void Enter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canExit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IGFightState getNext() {
		return new GFightRest();
	}

	
	
}
