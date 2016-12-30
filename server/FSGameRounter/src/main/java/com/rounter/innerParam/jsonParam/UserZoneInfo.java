package com.rounter.innerParam.jsonParam;

public class UserZoneInfo {

	private int zoneId;
	private String userName;      //角色名字
	private String userId;        //角色id
	private int level;            //角色当前等级
	private int vipLevel;         //vip等级
	private String headImage;     //头像
	private int career;           //职业
	private long lastLoginMillis;  //上次登录时间
	
	public int getZoneId() {
		return zoneId;
	}
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getVipLevel() {
		return vipLevel;
	}
	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}
	public long getLastLoginMillis() {
		return lastLoginMillis;
	}
	public void setLastLoginMillis(long lastLoginMillis) {
		this.lastLoginMillis = lastLoginMillis;
	}
	public String getHeadImage() {
		return headImage;
	}
	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}
	public int getCareer() {
		return career;
	}
	public void setCareer(int career) {
		this.career = career;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
