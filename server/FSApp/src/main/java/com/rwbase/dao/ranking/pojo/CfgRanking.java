package com.rwbase.dao.ranking.pojo;

public class CfgRanking {
	private String rankId;
	private String parentId;
	private String title;
	private String accountTime;// 5_11 表示5点11分排序
	private int limitLevel;
	private int rankNum;

	private int hour = 0;
	private int minute = 0;
	private int realTime;// 表示是否实时

	/** 树ID */
	public String getRankId() {
		return rankId;
	}

	public void setRankId(String rankId) {
		this.rankId = rankId;
	}

	/** 父类ID */
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/** 列表名字 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/** 每日刷新时间 */
	public String getAccountTime() {
		return accountTime;
	}

	public void setAccountTime(String accountTime) {
		this.accountTime = accountTime;
		String[] times = this.accountTime.split("_");
		hour = Integer.parseInt(times[0]);
		if (times.length >= 2) {
			minute = Integer.parseInt(this.accountTime.split("_")[1]);
		}
	}

	/** 开放等级限制 */
	public int getLimitLevel() {
		return limitLevel;
	}

	public void setLimitLevel(int limitLevel) {
		this.limitLevel = limitLevel;
	}

	/** 最多显示排行前几 */
	public int getRankNum() {
		return rankNum;
	}

	public void setRankNum(int rankNum) {
		this.rankNum = rankNum;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getRealTime() {
		return realTime;
	}

	public void setRealTime(int realTime) {
		this.realTime = realTime;
	}

}
