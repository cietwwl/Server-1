package com.rw.service.PeakArena.datamodel;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.log.GameLog;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamData {

	private int teamId;
	private String magicId;
	private List<String> heros;	
	
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	
	public String getMagicId() {
		return magicId;
	}
	public void setMagicId(String magicId) {
		this.magicId = magicId;
	}
	public List<String> getHeros() {
		return heros;
	}
	public void setHeros(List<String> heros) {
		if(heros == null){
			return;
		}
		this.heros = heros;
	}
	
}
