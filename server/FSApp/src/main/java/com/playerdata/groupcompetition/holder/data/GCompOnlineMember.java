package com.playerdata.groupcompetition.holder.data;

import javax.persistence.Id;

import com.playerdata.Player;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GCompOnlineMember {

	@Id
	private String id; // 客户端需要用的id字段
	private String userId; // 玩家的userId
	private String userName; // 玩家的名字
	private long power; // 战斗力
	private int lv; // 等级
	private String headIcon; // 头像
	
	
	public GCompOnlineMember(Player player) {
		this.id = player.getUserId();
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

	@Override
	public String toString() {
		return "GCompOnlineMember [userId=" + userId + ", userName=" + userName + ", lv=" + lv + "]";
	}
	
	
}
