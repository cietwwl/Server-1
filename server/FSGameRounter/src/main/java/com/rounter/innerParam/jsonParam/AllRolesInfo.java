package com.rounter.innerParam.jsonParam;

import java.util.List;

public class AllRolesInfo {
	
	private String accountId;
	
	public List<UserZoneInfo> roles;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public List<UserZoneInfo> getRoles() {
		return roles;
	}

	public void setRoles(List<UserZoneInfo> roles) {
		this.roles = roles;
	}
}
