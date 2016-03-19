package com.rwbase.dao.setting;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeadTypeList {
	private int type;
	private List<String> dataList;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<String> getDataList() {
		return dataList;
	}
	public void setDataList(List<String> dataList) {
		this.dataList = dataList;
	}
}
