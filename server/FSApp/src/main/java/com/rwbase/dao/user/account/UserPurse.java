package com.rwbase.dao.user.account;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_user_purse")
public class UserPurse {
	@Id
	private String userId;//用户id
	private int diamond;//钻石余额
	private long totalRecharge;//总充值
	private boolean firstRecharge;//是否首次充值
	private long version;//版本
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getDiamond() {
		return diamond;
	}
	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}
	public long getTotalRecharge() {
		return totalRecharge;
	}
	public void setTotalRecharge(long totalRecharge) {
		this.totalRecharge = totalRecharge;
	}
	
	public boolean isFirstRecharge() {
		return firstRecharge;
	}
	public void setFirstRecharge(boolean firstRecharge) {
		this.firstRecharge = firstRecharge;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	
	

}
