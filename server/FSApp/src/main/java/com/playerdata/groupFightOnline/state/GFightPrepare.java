package com.playerdata.groupFightOnline.state;

import com.playerdata.groupFightOnline.bm.GFightPrepareBM;
import com.playerdata.groupFightOnline.enums.GFResourceState;

public class GFightPrepare extends IGFightState{

	public GFightPrepare(int resourceID, GFResourceState resState) {
		super(resourceID, resState);
	}

	@Override
	public void Enter() {
		GFightPrepareBM.getInstance().prepareStart(resourceID);
	}

	@Override
	public IGFightState getNext() {
		return new GFightFight(resourceID, GFResourceState.FIGHT);
	}
}
