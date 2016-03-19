package com.rwbase.dao.battletower.pojo.cfg;

import org.codehaus.jackson.annotate.JsonIgnore;

/*
 * @author HC
 * @date 2015年9月2日 下午4:55:36
 * @Description 
 */
public class BattleTowerConfigCfg {
	private int strategyCacheRecordSize;// 策略缓存记录长度
	private int perPageFriendSize;// 每页显示好友的数量
	private int perDayBossSize;// 每天产生Boss的数量
	private int bossShowTime;// Boss显示的时间(分)
	private int bossNumInTheSameTime;// 可同时出现的最大的Boss数量
	private int theSweepTime4PerFloor;// 每一层扫荡需要的时间（秒）

	// // ///////////////////////////////////////////////金银铜三钥匙掉落方案
	private String copperKeyDropIds;// 铜钥匙掉落方案
	private String silverKeyDropIds;// 银钥匙掉落方案
	private String goldKeyDropIds;// 金钥匙掉落方案
	//
	// // ///////////////////////////////////////////////毋须转换成Json的区域
	@JsonIgnore
	private String[] copperKeyDropIdArr;// 铜钥匙掉落
	@JsonIgnore
	private String[] silverKeyDropIdArr;// 银钥匙掉落
	@JsonIgnore
	private String[] goldKeyDropIdArr;// 金钥匙掉落

	// ///////////////////////////////////////////////SET区
	public void setStrategyCacheRecordSize(int strategyCacheRecordSize) {
		this.strategyCacheRecordSize = strategyCacheRecordSize;
	}

	public void setPerPageFriendSize(int perPageFriendSize) {
		this.perPageFriendSize = perPageFriendSize;
	}

	public void setPerDayBossSize(int perDayBossSize) {
		this.perDayBossSize = perDayBossSize;
	}

	public void setTheSweepTime4PerFloor(int theSweepTime4PerFloor) {
		this.theSweepTime4PerFloor = theSweepTime4PerFloor;
	}

	public void setCopperKeyDropIds(String copperKeyDropIds) {
		this.copperKeyDropIds = copperKeyDropIds;
	}

	public void setSilverKeyDropIds(String silverKeyDropIds) {
		this.silverKeyDropIds = silverKeyDropIds;
	}

	public void setGoldKeyDropIds(String goldKeyDropIds) {
		this.goldKeyDropIds = goldKeyDropIds;
	}

	public void setBossShowTime(int bossShowTime) {
		this.bossShowTime = bossShowTime;
	}

	public void setBossNumInTheSameTime(int bossNumInTheSameTime) {
		this.bossNumInTheSameTime = bossNumInTheSameTime;
	}

	// ///////////////////////////////////////////////GET区
	public int getStrategyCacheRecordSize() {
		return strategyCacheRecordSize;
	}

	public int getPerPageFriendSize() {
		return perPageFriendSize;
	}

	public int getPerDayBossSize() {
		return perDayBossSize;
	}

	public int getTheSweepTime4PerFloor() {
		return theSweepTime4PerFloor;
	}

	public String getCopperKeyDropIds() {
		return copperKeyDropIds;
	}

	public String getSilverKeyDropIds() {
		return silverKeyDropIds;
	}

	public String getGoldKeyDropIds() {
		return goldKeyDropIds;
	}

	public String[] getCopperKeyDropIdArr() {
		if (this.copperKeyDropIds != null) {
			this.copperKeyDropIdArr = this.copperKeyDropIds.split(",");
		}
		return copperKeyDropIdArr;
	}

	public String[] getSilverKeyDropIdArr() {
		if (this.silverKeyDropIds != null) {
			this.silverKeyDropIdArr = this.silverKeyDropIds.split(",");
		}
		return silverKeyDropIdArr;
	}

	public String[] getGoldKeyDropIdArr() {
		if (this.goldKeyDropIds != null) {
			this.goldKeyDropIdArr = this.goldKeyDropIds.split(",");
		}
		return goldKeyDropIdArr;
	}

	public int getBossShowTime() {
		return bossShowTime;
	}

	public int getBossNumInTheSameTime() {
		return bossNumInTheSameTime;
	}
}