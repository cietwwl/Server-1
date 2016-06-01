package com.playerdata.army.simple;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.ArmyMagic;
import com.playerdata.dataSyn.ClassInfo4Client;
import com.playerdata.dataSyn.DataSynClassInfoMgr;
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
		ClassInfo4Client serverClassInfo = DataSynClassInfoMgr.getByClass(ArmyInfoSimple.class);
		String jsonData = serverClassInfo.toJson(this);
		return jsonData;
	}
	
	public static void main(String[] args) throws Exception {
		ArmyInfoSimple amryInfo = new ArmyInfoSimple();
		ArmyHeroSimple armyHero = new ArmyHeroSimple();
		amryInfo.setPlayer(armyHero);
		System.out.println(amryInfo.toJson());
		
		
	}
	
}
