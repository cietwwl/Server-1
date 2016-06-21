package com.playerdata.army;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurArmyAttrData {
	
	private String id;
	
	private List<CurAttrData> attrDataList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<CurAttrData> getAttrDataList() {
		return attrDataList;
	}

	public void setAttrDataList(List<CurAttrData> attrDataList) {
		this.attrDataList = attrDataList;
	}
	
	
	
	
	
}
