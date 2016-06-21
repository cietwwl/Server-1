package com.playerdata.groupFightOnline.data;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GFightOnlineResourceData {
	
	@Id
	private int resourceID;

	@CombineSave
	private String ownerGroupID;

	public int getResourceID() {
		return resourceID;
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

}
