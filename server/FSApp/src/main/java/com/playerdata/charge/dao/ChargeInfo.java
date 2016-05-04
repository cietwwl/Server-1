package com.playerdata.charge.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
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
	private List<ChargeInfoSubRecording> payTimesList = new ArrayList<ChargeInfoSubRecording>();
	


	//充值次数
	private int count;
	//上次充值金额
	private int lastCharge;
	//上次充值时间
	private long lastChargeTime;
	
	private int totalChargeMoney;
	
	private int totalChargeGold;	
	
	private List<ChargeOrder> chargeOrderList = new ArrayList<ChargeOrder>();
	
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
	public ChargeInfo addTotalChargeMoney(int addMoney){
		this.totalChargeMoney += addMoney;
		return this;
	}
	
	public ChargeInfo addCount(int count){
		this.count += count;
		return this;
	}
	public List<ChargeOrder> getChargeOrderList() {
		return chargeOrderList;
	}
	public void setChargeOrderList(List<ChargeOrder> chargeOrderList) {
		this.chargeOrderList = chargeOrderList;
	}
	
	public List<ChargeInfoSubRecording> getPayTimesList() {
		return payTimesList;
	}
	public void setPayTimesList(List<ChargeInfoSubRecording> payTimesList) {
		this.payTimesList = payTimesList;
	
	public void addOrder(ChargeOrder chargeOrder){
		if(chargeOrder == null){
			return;
		}
		
		final int maxSizeKeep = 10;//最多只保留10条订单信息
		if(chargeOrderList.size()>=maxSizeKeep){
			chargeOrderList.remove(0);
			chargeOrderList.add(0,chargeOrder);
			Collections.sort(chargeOrderList);//排序，最新的排在后面
		}
	
	}
	
	public boolean isOrderExist(String cpTradeNo){
		boolean isExist = false;
		for (ChargeOrder chargeOrder : chargeOrderList) {
			if(StringUtils.equals(cpTradeNo, chargeOrder.getCpTradeNo())){
				isExist = true;
				break;
			}
		}
		return isExist;
	}
	
	}
}
