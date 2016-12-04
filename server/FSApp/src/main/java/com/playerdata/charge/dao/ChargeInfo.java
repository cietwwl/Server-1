package com.playerdata.charge.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "charge_info")
@SynClass
public class ChargeInfo {

	@Id
	private String userId; // 用户ID
	
	//首充奖励是否已经领取
	private boolean isFirstAwardTaken = false;
	//限购商品的购买次数记录列表
	@IgnoreSynField
	private List<ChargeInfoSubRecording> payTimesList = new ArrayList<ChargeInfoSubRecording>();

	//充值次数
	private int count;
	//上次充值金额
	private int lastCharge;
	//上次充值时间
	private long lastChargeTime;
	//总的充值金额（单位：分）
	private int totalChargeMoney;
	// 总的充值所获得的钻石
	private int totalChargeGold;	
	
//	private List<ChargeOrder> chargeOrderList = new ArrayList<ChargeOrder>();
	
	private List<String> alreadyChargeIds = new ArrayList<String>(); // 已经充值过的充值档位id，只有首次充的时候会添加进来
	
	private boolean isChargeOn ;
	
	public boolean isChargeOn() {
		return isChargeOn;
	}
	public void setChargeOn(boolean isChargeOn) {
		this.isChargeOn = isChargeOn;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * 
	 * 获取充值次数
	 * 
	 * @return
	 */
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
	public int getTotalChargeMoney() {
		return totalChargeMoney;
	}
	public void setTotalChargeMoney(int totalChargeMoney) {
		this.totalChargeMoney = totalChargeMoney;
	}
	public int getTotalChargeGold() {
		return totalChargeGold;
	}
	public void setTotalChargeGold(int totalChargeGold) {
		this.totalChargeGold = totalChargeGold;
	}
	
	public ChargeInfo addTotalChargeGold(int addGold){
		this.totalChargeGold+=addGold;
		return this;
	}
	
	/**
	 * 
	 * 增加充值的总金额
	 * 
	 * @param addMoney
	 * @return
	 */
	public ChargeInfo addTotalChargeMoney(int addMoney){
		this.totalChargeMoney += addMoney;
		return this;
	}
	
	/**
	 * 
	 * 增加充值的次数
	 * 
	 * @param count
	 * @return
	 */
	public ChargeInfo addCount(int count){
		this.count += count;
		return this;
	}
	
	public List<ChargeInfoSubRecording> getPayTimesList() {
		return payTimesList;
	}
	public void setPayTimesList(List<ChargeInfoSubRecording> payTimesList) {
		this.payTimesList = payTimesList;
	}

	public List<String> getAlreadyChargeIds() {
		return alreadyChargeIds;
	}

	public void setAlreadyChargeIds(List<String> alreadyChargeIds) {
		this.alreadyChargeIds = new ArrayList<String>(alreadyChargeIds);
	}
	
	@JsonIgnore
	public boolean isContainsId(String cfgId) {
		return alreadyChargeIds.contains(cfgId);
	}
	
	public void addChargeCfgId(String cfgId) {
		this.alreadyChargeIds.add(cfgId);
	}
}
