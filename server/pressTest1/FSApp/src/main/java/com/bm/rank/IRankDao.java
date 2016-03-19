package com.bm.rank;

import java.util.List;

public interface IRankDao {

	
	public IRankDbTask getInsertTask(IRankEntry rankEntry);
	
	public IRankDbTask getDeleteTask(IRankEntry rankEntry);	
	
	public List<IRankEntry> getAllRankList();
	
}
