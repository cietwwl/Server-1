package com.rw.service.PeakArena.datamodel;

public class PeakRecordInfo {

	private int win;//0是输，1是赢
	private int placeUp;
	private String name;
	private String headImage;
	private int level;
	private long time;
	private String userId;
	private int challenge;//1是我打别人，01是别人打我
	
	public int getWin() {
		return win;
	}
	public void setWin(int win) {
		this.win = win;
	}
	public int getPlaceUp() {
		return placeUp;
	}
	public void setPlaceUp(int placeUp) {
		this.placeUp = placeUp;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getChallenge() {
		return challenge;
	}
	public void setChallenge(int challenge) {
		this.challenge = challenge;
	}
	
}
