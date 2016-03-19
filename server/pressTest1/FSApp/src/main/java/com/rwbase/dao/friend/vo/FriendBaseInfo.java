package com.rwbase.dao.friend.vo;

import java.util.Calendar;

public class FriendBaseInfo {
	private String userId;
	private String userName;
	private String headImage;
	private int level;
	private int career;
	private String unionName;
	private long lastLoginTime;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getHeadImage() {
		return headImage;
	}
	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getCareer() {
		return career;
	}
	public void setCareer(int career) {
		this.career = career;
	}
	public long getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	
	/**时间换算*/
	public String getLastLoginTip(){
		long diffTime = Calendar.getInstance().getTime().getTime() - lastLoginTime;
		int month = (int) (diffTime / 1000 / 60 / 60 / 24 / 30);
		if(month > 0){
			return month + "个月前";
		}
		int day = (int) (diffTime / 1000 / 60 / 60 / 24);
		if(day > 0){
			return day + "天前";
		}
		int hour = (int) (diffTime / 1000 / 60 / 60);
		if(hour > 0){
			return hour + "小时前";
		}
		int minute = (int) (diffTime / 1000 / 60);
		if(minute > 0){
			return minute + "分钟前";
		}
		return "1分钟前";
	}
	public String getUnionName() {
		return unionName == null ? "" : unionName;
	}
	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}
}
