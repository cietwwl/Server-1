package com.playerdata.charge.dao;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "charge_Info")
@SynClass
public class ChargeInfo {

	@Id
	private String userId; // 用户ID

	//首充奖励是否已经领取
	private boolean isFirstAwardTaken = false;
	
	//重置次数
	private int count;
	//上次充值金额
	private int lastCharge;
	//上次充值时间
	private long lastChargeTime;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getLastCharge() {
		return lastCharge;
	}
	public void setLastCharge(int lastCharge) {
		this.lastCharge = lastCharge;
	}
	public long getLastChargeTime() {
		return lastChargeTime;
	}
	public void setLastChargeTime(long lastChargeTime) {
		this.lastChargeTime = lastChargeTime;
	}
	public boolean isFirstAwardTaken() {
		return isFirstAwardTaken;
	}
	public void setFirstAwardTaken(boolean isFirstAwardTaken) {
		this.isFirstAwardTaken = isFirstAwardTaken;
	}
	
	
	

}
