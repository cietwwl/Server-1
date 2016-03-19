package com.bm.srank;

import java.util.List;

public interface ISRankDao {

	
	public ISRankDbTask getUpdateTask(ISRankEntry rankEntry);
	
	public List<ISRankEntry> getAllRankList();
	
}
