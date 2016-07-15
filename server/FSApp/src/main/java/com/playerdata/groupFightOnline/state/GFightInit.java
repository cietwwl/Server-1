package com.playerdata.groupFightOnline.state;

public class GFightInit extends IGFightState{

	public GFightInit(int resourceID) {
		super(resourceID);
	}

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
		return new GFightBidding(resourceID);
	}
}
