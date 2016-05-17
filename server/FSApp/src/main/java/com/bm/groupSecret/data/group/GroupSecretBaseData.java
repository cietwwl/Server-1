package com.bm.groupSecret.data.group;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.bm.groupSecret.GroupSecretType;
import com.playerdata.dataSyn.annotation.SynClass;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupSecretBaseData {

	private String name;
	
	private long createdTime;
	
	private GroupSecretType secretType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public GroupSecretType getSecretType() {
		return secretType;
	}

	public void setSecretType(GroupSecretType secretType) {
		this.secretType = secretType;
	}
	
	
	
}
