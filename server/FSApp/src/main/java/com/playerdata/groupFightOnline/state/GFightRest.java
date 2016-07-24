package com.playerdata.groupFightOnline.state;

import com.playerdata.groupFightOnline.bm.GFightFinalBM;
import com.playerdata.groupFightOnline.enums.GFResourceState;

public class GFightRest extends IGFightState{

	public GFightRest(int resourceID, GFResourceState resState) {
		super(resourceID, resState);
	}

	@Override
	public void Enter() {
		GFightFinalBM.getInstance().handleGFightResult(resourceID);
	}

	@Override
	public IGFightState getNext() {
		return new GFightBidding(resourceID, GFResourceState.BIDDING);
	}
}
