package com.bm.srank;

public interface ISRankEntry {

	public String getOwnerId();
	
	public int getRank();
	
	public void setRank(int rank);
	
	public ISRankEntry clone();
	
	public boolean canSwap();
	
}
