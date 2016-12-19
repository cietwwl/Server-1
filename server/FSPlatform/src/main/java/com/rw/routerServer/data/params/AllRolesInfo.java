package com.rw.routerServer.data.params;

import java.util.List;

import com.rwbase.dao.user.accountInfo.UserMappingInfo;
import com.rwbase.dao.user.accountInfo.UserZoneInfo;

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
