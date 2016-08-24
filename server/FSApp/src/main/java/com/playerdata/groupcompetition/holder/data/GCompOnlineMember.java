package com.playerdata.groupcompetition.holder.data;

import com.playerdata.Player;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GCompOnlineMember {

	private String userId; // 玩家的userId
	private String userName; // 玩家的名字
	private long power; // 战斗力
	private int lv; // 等级
	private String headIcon; // 头像
	
	public GCompOnlineMember(Player player) {
		this.userId = player.getUserId();
		this.userName = player.getUserName();
		this.power = player.getUserGameDataMgr().getFightingAll();
		this.lv = player.getLevel();
		this.headIcon = player.getHeadImage();
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
}
