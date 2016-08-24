package com.rw.handler.GroupCopy.data;

import com.rw.dataSyn.SynItem;

public class GroupCopyLevelRecord implements SynItem{

	
	private String id;
	
	private String levelID;
	
	
	@Override
	public String getId() {
		return id;
	}


	public String getLevelID() {
		return levelID;
	}
	
	
	

}
