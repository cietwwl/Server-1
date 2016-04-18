package com.rwbase.dao.user.accountInfo;

/**
 * 记录角色登陆信息
 * @author lida
 *
 */
public class AccountLoginRecord {
	private String accountId;
	private String userId;
	private int zoneId;
	private long loginTime;
	
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public int getZoneId() {
		return zoneId;
	}
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public long getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}
}
