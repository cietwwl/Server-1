package com.rwbase.dao.fetters.pojo;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;


@SynClass
public class SynMagicEquipFetterData {

	private String id;
	
	private List<Integer> fetterIDs = new ArrayList<Integer>();

	
	
	
	public SynMagicEquipFetterData(String id, List<Integer> fetterIDs) {
		
		this.id = id;
		this.fetterIDs.addAll(fetterIDs);
	}

	public String getId() {
		return id;
	}

	public List<Integer> getFetterIDs() {
		return fetterIDs;
	}
	
	
	
}
