package com.rw.handler.groupFight.data;

import com.rwproto.DataSynProtos.eSynType;

public class GFBiddingItemHolder {
	
	final private eSynType synType = eSynType.GFBiddingData;
	
	private static GFBiddingItemHolder instance = new GFBiddingItemHolder();

	public static GFBiddingItemHolder getInstance() {
		return instance;
	}
}
