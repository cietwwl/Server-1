package com.rw.handler.groupFight.dataForRank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.DataSynHelper;

public class GFBidRankHolder {
	private static GFBidRankHolder instance = new GFBidRankHolder();
	
	public static GFBidRankHolder getInstance() {
		return instance;
	}
	
	private  Map<Integer, List<GFGroupBiddingItem>> bidRankMap = new HashMap<Integer, List<GFGroupBiddingItem>>();
	
	public void updateBidRank(int resourceID, List<String> bidRank) {
		List<GFGroupBiddingItem> rankList = new ArrayList<GFGroupBiddingItem>();
		for(String rankJason : bidRank){
			GFGroupBiddingItem item = DataSynHelper.ToObject(GFGroupBiddingItem.class, rankJason);
			rankList.add(item);
		}
		bidRankMap.put(resourceID, rankList);
	}
	
	public List<GFGroupBiddingItem> getBidRank(int resourceID){
		return bidRankMap.get(resourceID);
	}
}
