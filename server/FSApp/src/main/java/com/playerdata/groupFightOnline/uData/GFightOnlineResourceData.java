package com.playerdata.groupFightOnline.uData;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GFightOnlineResourceData {
	
	@Id
	private int resourceID;

	private int ownerGroupID;

	public int getResourceID() {
		return resourceID;
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}

	public int getOwnerGroupID() {
		return ownerGroupID;
	}

	public void setOwnerGroupID(int ownerGroupID) {
		this.ownerGroupID = ownerGroupID;
	}

}
