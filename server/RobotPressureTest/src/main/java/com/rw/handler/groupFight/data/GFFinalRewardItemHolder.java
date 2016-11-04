package com.rw.handler.groupFight.data;

import com.rwproto.DataSynProtos.eSynType;

public class GFFinalRewardItemHolder {
	private static GFFinalRewardItemHolder instance = new GFFinalRewardItemHolder();

	public static GFFinalRewardItemHolder getInstance() {
		return instance;
	}
	
	final private eSynType synType = eSynType.GFightFinalReward;
}
