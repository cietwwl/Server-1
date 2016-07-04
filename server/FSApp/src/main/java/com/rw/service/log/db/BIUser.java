package com.rw.service.log.db;

import javax.persistence.Id;

import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rwbase.dao.majorDatas.pojo.MajorData;
import com.rwbase.dao.user.UserGameData;

public class BIUser{
	@Id
	private String userId;
	private int zoneId;
	private int vip;
	private int level;
	@SaveAsJson
	private  ZoneRegInfo zoneRegInfo;
	@SaveAsJson
	private UserGameData dbvalue;
	
	private long coin;// 铜钱
	
	private int gold;// 金钱
	
	private int giftGold;
	
	private int chargeGold;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getZoneId() {
		return zoneId;
	}
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}
	public int getVip() {
		return vip;
	}
	public void setVip(int vip) {
		this.vip = vip;
	}
	public ZoneRegInfo getZoneRegInfo() {
		return zoneRegInfo;
	}
	public void setZoneRegInfo(ZoneRegInfo zoneRegInfo) {
		this.zoneRegInfo = zoneRegInfo;
	}
	public long getCoin() {
		return coin;
	}
	public void setCoin(long coin) {
		this.coin = coin;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getGiftGold() {
		return giftGold;
	}
	public void setGiftGold(int giftGold) {
		this.giftGold = giftGold;
	}
	public int getChargeGold() {
		return chargeGold;
	}
	public void setChargeGold(int chargeGold) {
		this.chargeGold = chargeGold;
	}
}