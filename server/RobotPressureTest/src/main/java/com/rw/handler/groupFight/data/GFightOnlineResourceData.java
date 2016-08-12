package com.rw.handler.groupFight.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GFightOnlineResourceData  implements SynItem{
	
	private int resourceID;

	private String ownerGroupID;
	
	private int state = 0;

	public int getResourceID() {
		return Integer.valueOf(resourceID);
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}

	public String getOwnerGroupID() {
		return ownerGroupID;
	}

	public void setOwnerGroupID(String ownerGroupID) {
		this.ownerGroupID = ownerGroupID;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public String getId() {
		return String.valueOf(resourceID);
	}
}
