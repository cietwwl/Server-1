package com.rwbase.dao.guildSecretArea.projo;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.tower.pojo.TowerHeroChange;
@SynClass
public class SecretUserChange {//玩家血量改变
	public SecretUserChange(){
		
	}
	private String userId;//玩家id

	private List<TowerHeroChange> changeList;//	玩家血量变化
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<TowerHeroChange> getChangeList() {
		return changeList;
	}
	public void setChangeList(List<TowerHeroChange> changeList) {
		this.changeList = changeList;
	}
}
