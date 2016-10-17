package com.rw.handler.groupCompetition.data.onlinemember;

import com.rw.dataSyn.SynItem;

public class GCompOnlineMember implements SynItem {

	private String id; // 客户端需要用的id字段
	private String userId; // 玩家的userId
	private String userName; // 玩家的名字
	private long power; // 战斗力
	private int lv; // 等级
	private String headIcon; // 头像
	private boolean inTeam; // 是否在队伍里面
	
	@Override
	public String getId() {
		return id;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public long getPower() {
		return power;
	}
	
	public int getLv() {
		return lv;
	}
	
	public String getHeadIcon() {
		return headIcon;
	}
	
	public void setInTeam(boolean value) {
		this.inTeam = value;
	}
	
	public boolean isInTeam() {
		return inTeam;
	}

	@Override
	public String toString() {
		return "GCompOnlineMember [userId=" + userId + ", userName=" + userName + ", lv=" + lv +  ", inTeam=" + inTeam + "]";
	}
	
	
}
