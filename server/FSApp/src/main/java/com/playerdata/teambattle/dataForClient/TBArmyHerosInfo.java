package com.playerdata.teambattle.dataForClient;

import java.util.List;

public class TBArmyHerosInfo {
	
	private String magicID;
	
	private List<String> heroIDs;

	public String getMagicID() {
		return magicID;
	}

	public void setMagicID(String magicID) {
		this.magicID = magicID;
	}

	public List<String> getHeroIDs() {
		return heroIDs;
	}

	public void setHeroIDs(List<String> heroIDs) {
		this.heroIDs = heroIDs;
	}
}
