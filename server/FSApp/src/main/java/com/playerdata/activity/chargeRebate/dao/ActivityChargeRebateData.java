package com.playerdata.activity.chargeRebate.dao;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="charge_rebate")
public class ActivityChargeRebateData {

	@Column(name = "chargeMoney")
	private int chargeMoney;
	@Column(name = "vipExp")
	private int vipExp;
	@Column(name = "monthCard")
	private int monthCard;
	@Column(name = "vipMonthCard")
	private int vipMonthCard;
	@Column(name = "isArenaKing")
	private boolean isArenaKing;
	@Column(name = "isSendReward")
	private boolean isSendReward;
	@Column(name = "zoneId")
	private int zoneId;	
	@Column(name = "openAccount")
	@Id
	private String openAccount;
	
	
	public String getOpenAccount() {
		return openAccount;
	}
	public void setOpenAccount(String openAccount) {
		this.openAccount = openAccount;
	}
	public int getChargeMoney() {
		return chargeMoney;
	}
	public void setChargeMoney(int chargeMoney) {
		this.chargeMoney = chargeMoney;
	}
	public boolean isSendReward() {
		return isSendReward;
	}
	public void setSendReward(boolean isSendReward) {
		this.isSendReward = isSendReward;
	}
	public int getZoneId() {
		return zoneId;
	}
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}
	public int getVipExp() {
		return vipExp;
	}
	public void setVipExp(int vipExp) {
		this.vipExp = vipExp;
	}
	public int getMonthCard() {
		return monthCard;
	}
	public void setMonthCard(int monthCard) {
		this.monthCard = monthCard;
	}
	public int getVipMonthCard() {
		return vipMonthCard;
	}
	public void setVipMonthCard(int vipMonthCard) {
		this.vipMonthCard = vipMonthCard;
	}
	public boolean isArenaKing() {
		return isArenaKing;
	}
	public void setArenaKing(boolean isArenaKing) {
		this.isArenaKing = isArenaKing;
	}
}
