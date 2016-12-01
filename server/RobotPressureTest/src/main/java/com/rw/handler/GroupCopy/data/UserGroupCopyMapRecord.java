package com.rw.handler.GroupCopy.data;

import com.rw.dataSyn.SynItem;

public class UserGroupCopyMapRecord implements SynItem{

	private String id;
	
	private String chaterID;
	
	private int leftFightCount;
	
	
	@Override
	public String getId() {
		
		return id;
	}


	public String getChaterID() {
		return chaterID;
	}


	public int getLeftFightCount() {
		return leftFightCount;
	}


	public void setId(String id) {
		this.id = id;
	}


	public void setChaterID(String chaterID) {
		this.chaterID = chaterID;
	}


	public void setLeftFightCount(int leftFightCount) {
		this.leftFightCount = leftFightCount;
	}
	
	

}
