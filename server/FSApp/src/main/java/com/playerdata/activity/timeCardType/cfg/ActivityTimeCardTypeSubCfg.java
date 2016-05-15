package com.playerdata.activity.timeCardType.cfg;

import com.playerdata.charge.cfg.ChargeTypeEnum;
import com.rw.fsutil.common.EnumIndex;


public class ActivityTimeCardTypeSubCfg {

	private String id;
	
	//所属活动配置id
	private String parentCfgId;	
	//持续天数
	private int days;
	//所需费用
	private int moneyCount;
	//位置
	private int slot;
	//每日领取钻石
	private int dayAwardCount;
	private int daysLimit;//超过天数不可再续费
	@EnumIndex
	private ChargeTypeEnum chargeType;//类型
	
	


	public ChargeTypeEnum getChargeType() {
		return chargeType;
	}

	public void setChargeType(ChargeTypeEnum chargeType) {
		this.chargeType = chargeType;
	}

	public int getDaysLimit() {
		return daysLimit;
	}

	public void setDaysLimit(int daysLimit) {
		this.daysLimit = daysLimit;
	}

	//月卡类型
	private int timeCardType;
	public int getTimeCardType() {
		return timeCardType;
	}

	public void setTimeCardType(int timeCardType) {
		this.timeCardType = timeCardType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public int getMoneyCount() {
		return moneyCount;
	}

	public void setMoneyCount(int moneyCount) {
		this.moneyCount = moneyCount;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public String getParentCfgId() {
		return parentCfgId;
	}

	public void setParentCfgId(String parentCfgId) {
		this.parentCfgId = parentCfgId;
	}

	public int getDayAwardCount() {
		return dayAwardCount;
	}

	public void setDayAwardCount(int dayAwardCount) {
		this.dayAwardCount = dayAwardCount;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}


	
	


	
	
	
	
}
