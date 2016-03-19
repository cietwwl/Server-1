package com.rw.handler.battle.army;

import java.util.List;

import com.rw.dataSyn.DataSynHelper;
public class ArmyInfo {

	//佣兵列表不包括主角
	private List<ArmyHero> heroList;
	//主角
	private ArmyHero player;
	private ArmyMagic armyMagic;
	private String playerName;
	private String playerHeadImage;
	private String guildName;
	
	public List<ArmyHero> getHeroList() {
		return heroList;
	}
	public void setHeroList(List<ArmyHero> heroList) {
		this.heroList = heroList;
	}
	public ArmyHero getPlayer() {
		return player;
	}
	public void setPlayer(ArmyHero player) {
		this.player = player;
	}
	public ArmyMagic getArmyMagic() {
		return armyMagic;
	}
	public void setArmyMagic(ArmyMagic armyMagic) {
		this.armyMagic = armyMagic;
	}
	public void setPlayerName(String name) {
		this.playerName = name;
	}
	public String getPlayerName() {
		return this.playerName;
	}
	
	public void setPlayerHeadImage(String headImage) {
		this.playerHeadImage = headImage;
	}
	public String getPlayerHeadImage() {
		return playerHeadImage;
	}
	
	public void setGuildName(String guildName) {
		this.guildName = guildName;
	}
	public String getGuildName() {
		return this.guildName;
	}
	
	public static ArmyInfo fromJson(String jsonData){
		return DataSynHelper.ToObject(ArmyInfo.class, jsonData);
	}
	
}
