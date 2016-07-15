package com.playerdata.groupFightOnline.state;

import com.playerdata.groupFightOnline.bm.GFightGroupBidBM;
import com.playerdata.groupFightOnline.enums.GFResourceState;

public class GFightBidding extends IGFightState{

	public GFightBidding(int resourceID, GFResourceState resState) {
		super(resourceID, resState);
	}

	@Override
	public void Enter() {
		GFightGroupBidBM.getInstance().bidStart(resourceID);
	}

	@Override
	public IGFightState getNext() {
		return new GFightPrepare(resourceID, GFResourceState.PREPARE);
	}
}
