package com.rwbase.dao.globalData;



import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;



@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "globaldata")
public class TableGlobalData {
	@Id
	private int serverId;


	private int gulidIndex;


	public int getServerId() {
		return serverId;
	}


	public void setServerId(int serverId) {
		this.serverId = serverId;
	}


	public int getGulidIndex() {
		return gulidIndex;
	}


	public void setGulidIndex(int gulidIndex) {
		this.gulidIndex = gulidIndex;
	}
	
	
	
	

}