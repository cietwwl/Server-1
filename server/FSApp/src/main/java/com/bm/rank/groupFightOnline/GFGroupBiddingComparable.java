package com.bm.rank.groupFightOnline;

public class GFGroupBiddingComparable implements Comparable<GFGroupBiddingComparable> {
	private int resourceID; 	//资源点
	private int totalBid; 	//总竞标值
	private long lastBidTime;	//上次竞标时间
	
	public GFGroupBiddingComparable(){
		
	}
	
	public GFGroupBiddingComparable(int resourceID, int totalBid, long lastBidTime){
		this.resourceID = resourceID;
		this.totalBid = totalBid;
		this.lastBidTime = lastBidTime;
	}
	
	public int getResourceID(){
		return this.resourceID;
	}
	
	public int getTotalBid(){
		return this.totalBid;
	}

	@Override
	public int compareTo(GFGroupBiddingComparable o) {
		if(resourceID < o.resourceID){
			return 1;
		}
		if(resourceID > o.resourceID){
			return -1;
		}
		if(totalBid > o.totalBid){
			return -1;
		}
		if(totalBid < o.totalBid){
			return 1;
		}
		if(lastBidTime > o.lastBidTime){
			return 1;
		}
		if(lastBidTime < o.lastBidTime){
			return -1;
		}
		return 0;
	}
}
