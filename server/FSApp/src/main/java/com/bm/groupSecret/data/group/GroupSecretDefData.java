package com.bm.groupSecret.data.group;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupSecretDefData {

	private List<GroupSecretDeferInfo> deferInfoList = new ArrayList<GroupSecretDeferInfo>();

	public List<GroupSecretDeferInfo> getDeferInfoList() {
		return deferInfoList;
	}

	public void setDeferInfoList(List<GroupSecretDeferInfo> deferInfoList) {
		this.deferInfoList = deferInfoList;
	}

	
	
}
