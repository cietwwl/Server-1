package com.playerdata.groupFightOnline.state;

public class GFightRest extends IGFightState{

	public GFightRest(int resourceID) {
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
