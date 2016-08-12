package com.playerdata.groupFightOnline.state;

import com.playerdata.groupFightOnline.bm.GFightOnFightBM;
import com.playerdata.groupFightOnline.enums.GFResourceState;

public class GFightFight extends IGFightState{

	public GFightFight(int resourceID, GFResourceState resState) {
		super(resourceID, resState);
	}

	@Override
	public void Enter() {
		GFightOnFightBM.getInstance().fightStart(resourceID);
	}

	@Override
	public IGFightState getNext() {
		return new GFightRest(resourceID, GFResourceState.REST);
	}	
}
