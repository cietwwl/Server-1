package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.List;

import com.playerdata.army.ArmyMagic;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GroupCopyTeamInfo {

	//佣兵列表不包括主角
	private List<TeamHero> heroList;
	//主角
	private TeamHero player;
	private ArmyMagic armyMagic;
	private String playerName;
	private String playerHeadImage;
	private String guildName;
	
	public void setArmyMagic(ArmyMagic armyMagic) {
		this.armyMagic = armyMagic;
	}

	public void setGuildName(String name) {
		this.guildName = name;
		
	}

	public List<TeamHero> getHeroList() {
		return heroList;
	}

	public void setHeroList(List<TeamHero> heroList) {
		this.heroList = heroList;
	}

	public TeamHero getPlayer() {
		return player;
	}

	public void setPlayer(TeamHero player) {
		this.player = player;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerHeadImage() {
		return playerHeadImage;
	}

	public void setPlayerHeadImage(String playerHeadImage) {
		this.playerHeadImage = playerHeadImage;
	}

	public ArmyMagic getArmyMagic() {
		return armyMagic;
	}

	public String getGuildName() {
		return guildName;
	}

}
