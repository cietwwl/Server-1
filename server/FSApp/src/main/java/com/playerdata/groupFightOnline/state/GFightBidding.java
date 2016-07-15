package com.playerdata.groupFightOnline.state;

public class GFightBidding extends IGFightState{

	public GFightBidding(int resourceID) {
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
		return new GFightPrepare(resourceID);
	}
}
