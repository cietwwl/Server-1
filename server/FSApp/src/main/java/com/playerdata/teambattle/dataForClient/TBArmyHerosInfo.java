package com.playerdata.teambattle.dataForClient;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class TBArmyHerosInfo {
	
	private String magicID;
	
	private List<String> heroIDs;
	
	private List<Integer> position;

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

	public List<Integer> getPosition() {
		return position;
	}

	public void setPosition(List<Integer> position) {
		this.position = position;
	}
}
