package com.rwbase.dao.groupCopy.db;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class DropInfo{

	private int count;
	
	private long time;

	
	
	public DropInfo() {
	}

	public DropInfo(int count, long time) {
		super();
		this.count = count;
		this.time = time;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	
	
	
}
