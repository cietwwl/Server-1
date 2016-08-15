package com.rw.handler.groupFight.dataForClient;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;

/**
 * 客户端用来传参数给服务端
 * 设置的防守队伍中的英雄id列表
 * 给玩家设置本人的防守队伍
 * @author aken
 */
@SynClass
public class DefendArmyHerosInfo {
	
	private String defendArmyID;
	
	private String magicID;
	
	private List<String> heroIDs;

	public String getDefendArmyID() {
		return defendArmyID;
	}

	public void setDefendArmyID(String defendArmyID) {
		this.defendArmyID = defendArmyID;
	}

	public List<String> getHeroIDs() {
		return heroIDs;
	}

	public void setHeroIDs(List<String> heroIDs) {
		this.heroIDs = heroIDs;
	}

	public String getMagicID() {
		return magicID;
	}

	public void setMagicID(String magicID) {
		this.magicID = magicID;
	}
}
