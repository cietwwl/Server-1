package com.playerdata.charge.dao;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargeInfoSubRecording {
	
	private String id;
	

	
	//是否已经领取
	private int count ;



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public int getCount() {
		return count;
	}



	public void setCount(int count) {
		this.count = count;
	}





	
}
