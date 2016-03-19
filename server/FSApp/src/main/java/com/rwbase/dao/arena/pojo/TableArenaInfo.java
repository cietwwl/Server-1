package com.rwbase.dao.arena.pojo;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.Hero;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "arena_info")
public class TableArenaInfo {

	@Id
	private String userId; // 用户ID
	private int career;
	private int place;
	private int fighting;
	private int state;//0不在战斗，1是战斗中
	private long lastStateTime;
	private String name;
	private String headImage;
	private int level;	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getCareer() {
		return career;
	}
	public void setCareer(int career) {
		this.career = career;
	}
	public int getPlace() {
		return place;
	}
	public void setPlace(int place) {
		this.place = place;
	}
	public int getFighting() {
		return fighting;
	}
	public void setFighting(int fighting) {
		this.fighting = fighting;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public long getLastStateTime() {
		return lastStateTime;
	}
	public void setLastStateTime(long lastStateTime) {
		this.lastStateTime = lastStateTime;
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
}
