package com.rwbase.dao.battletower.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/*
 * @author HC
 * @date 2015年9月1日 下午2:19:59
 * @Description 产生Boss的信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BossInfo {
	private int bossUniqueId;// 产生Boss的唯一Id
	private int bossId;// 产生Boss的Id
	private long bossStartTime;// 产生Boss的时间
	private int bossInGroupId;// boss产生的里程碑
	private int bossInFloor;// 产生Boss的层数
	private boolean hasFight;// 是否打过Boss

	public void setBossUniqueId(int bossUniqueId) {
		this.bossUniqueId = bossUniqueId;
	}

	public void setBossId(int bossId) {
		this.bossId = bossId;
	}

	public void setBossStartTime(long bossStartTime) {
		this.bossStartTime = bossStartTime;
	}

	public void setBossInGroupId(int bossInGroupId) {
		this.bossInGroupId = bossInGroupId;
	}

	public void setBossInFloor(int bossInFloor) {
		this.bossInFloor = bossInFloor;
	}

	public int getBossId() {
		return bossId;
	}

	public long getBossStartTime() {
		return bossStartTime;
	}

	public int getBossInGroupId() {
		return bossInGroupId;
	}

	public int getBossInFloor() {
		return bossInFloor;
	}

	public int getBossUniqueId() {
		return bossUniqueId;
	}

	public boolean isHasFight() {
		return hasFight;
	}

	public void setHasFight(boolean hasFight) {
		this.hasFight = hasFight;
	}
}