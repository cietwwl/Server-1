package com.rw.handler.fresheractivity;

import java.util.ArrayList;
import java.util.List;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FresherActivityBigItem implements SynItem{
	
	private String id;
	private String ownerId;
	private List<FresherActivityItem> itemList = new ArrayList<FresherActivityItem>();
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}


	public List<FresherActivityItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<FresherActivityItem> itemList) {
		this.itemList = itemList;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
}
