package com.playerdata.army.simple;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.ArmyMagic;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.dataSyn.annotation.SynClass;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmyInfoSimple {

	//佣兵列表不包括主角
	private List<ArmyHeroSimple> heroList;
	//主角
	private ArmyHeroSimple player;
	private ArmyMagic armyMagic;
	private String playerName;
	private String playerHeadImage;
	private String groupName;
	//team fighting
	private int teamFighting;
	
	public List<ArmyHeroSimple> getHeroList() {
		return heroList;
	}
	
	public void setHeroList(List<ArmyHeroSimple> heroList) {
		this.heroList = heroList;
	}
	
	public ArmyHeroSimple getArmyHeroByID(String id){
		if(StringUtils.equals(player.getId(),id)) return player;
		for(ArmyHeroSimple hero : heroList) {
			if(hero == null) continue;
			if(StringUtils.equals(hero.getId(),id)) return hero;
		}
		return null;
	}
	
	public ArmyHeroSimple getPlayer() {
		return player;
	}
	public void setPlayer(ArmyHeroSimple player) {
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
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public int getTeamFighting() {
		return teamFighting;
	}
	public void setTeamFighting(int teamFighting) {
		this.teamFighting = teamFighting;
	}
	public String toJson() throws Exception{
		String jsonData = ClientDataSynMgr.toClientData(this);
		return jsonData;
	}
	
	public List<String> getHeroIdList(){
		List<String> heroIdList = new ArrayList<String>();
		for (ArmyHeroSimple hero : heroList) {
			heroIdList.add(hero.getId());
		}
		return heroIdList;
	}
}
