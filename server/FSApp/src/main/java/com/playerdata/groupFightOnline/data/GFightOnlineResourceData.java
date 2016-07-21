package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupFightOnline.dataForClient.GFFightRecord;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.NonSave;


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
	
	@CombineSave
	@IgnoreSynField
	private List<GFFightRecord> recordList = new ArrayList<GFFightRecord>();
	
	@IgnoreSynField
	@NonSave
	private static final int LIST_SIZE = 50;

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
	
	public synchronized void addFightRecord(GFFightRecord record){
		if(recordList.size() >= LIST_SIZE){
			Collections.sort(recordList);
			recordList.set(LIST_SIZE - 1, record);
		} else recordList.add(record);
	}
	
	public List<GFFightRecord> getFightRecord(){
		return recordList;
	}
	
	public void clearCurrentLoopData(){
		ownerGroupID = null;
		recordList.clear();
	}
}
