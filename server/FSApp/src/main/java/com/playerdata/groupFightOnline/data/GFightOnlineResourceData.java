package com.playerdata.groupFightOnline.data;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gf_resource_data")
public class GFightOnlineResourceData {
	
	@Id
	private int resourceID;

	@CombineSave
	private String ownerGroupID;
	
	@CombineSave
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
}
