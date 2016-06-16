package com.bm.groupChamp.data;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GroupChampData {
	
	private String id;
	
	private int version;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public void incrVersion(){
		this.version ++;
	}
	
}
