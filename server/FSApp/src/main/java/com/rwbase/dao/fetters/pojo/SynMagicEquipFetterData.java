package com.rwbase.dao.fetters.pojo;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;


@SynClass
public class SynMagicEquipFetterData {

	//可以是法宝或神器id
	private int itemID;
	
	//已经开启羁绊id
	private List<Integer> fetterIDs;

	
	
	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public List<Integer> getFetterIDs() {
		return fetterIDs;
	}

	public void setFetterIDs(List<Integer> fetterIDs) {
		this.fetterIDs = fetterIDs;
	}
	
	

	

	
}
