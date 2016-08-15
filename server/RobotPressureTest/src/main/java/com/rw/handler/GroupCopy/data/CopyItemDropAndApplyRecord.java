package com.rw.handler.GroupCopy.data;

import com.rw.dataSyn.SynItem;

public class CopyItemDropAndApplyRecord implements SynItem{

	private String id; //groupID_chaterID 

	private String groupId; // 帮派ID

	private String chaterID;//对应章节id
	
	
	@Override
	public String getId() {
		return id;
	}


	public String getGroupId() {
		return groupId;
	}


	public String getChaterID() {
		return chaterID;
	}
	
	

}
