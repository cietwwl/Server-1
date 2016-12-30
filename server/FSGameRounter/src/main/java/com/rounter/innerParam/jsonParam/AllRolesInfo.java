package com.rounter.innerParam.jsonParam;

import java.util.List;

public class AllRolesInfo {
	
	private String accountId;
	
	public List<UserMappingInfo> roles;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public List<UserMappingInfo> getRoles() {
		return roles;
	}

	public void setRoles(List<UserMappingInfo> roles) {
		this.roles = roles;
	}
}
