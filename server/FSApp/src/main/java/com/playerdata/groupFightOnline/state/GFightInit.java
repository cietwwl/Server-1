package com.playerdata.groupFightOnline.state;

import com.playerdata.groupFightOnline.enums.GFResourceState;

public class GFightInit extends IGFightState{

	public GFightInit(int resourceID, GFResourceState resState) {
		super(resourceID, resState);
	}

	@Override
	public void Enter() {
		
	}

	@Override
	public IGFightState getNext() {
		return new GFightBidding(resourceID, GFResourceState.BIDDING);
	}
}
