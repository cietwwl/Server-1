package com.playerdata.userSimple;

import java.util.List;

import com.playerdata.dataSyn.ClassInfo4Client;
import com.playerdata.dataSyn.DataSynClassInfoMgr;
import com.playerdata.dataSyn.annotation.SynClass;
@SynClass
public class UserSimpleInfo {

	//佣兵列表不包括主角
	private List<HeroSimple> heroList;
	//主角
	private HeroSimple player;
	private String playerName;
	private String playerHeadImage;
	private String guildName;
	
	public List<HeroSimple> getHeroList() {
		return heroList;
	}
	public void setHeroList(List<HeroSimple> heroList) {
		this.heroList = heroList;
	}
	public HeroSimple getPlayer() {
		return player;
	}
	public void setPlayer(HeroSimple player) {
		this.player = player;
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
	
	public String toJson() throws Exception{
		ClassInfo4Client serverClassInfo = DataSynClassInfoMgr.getByClass(UserSimpleInfo.class);
		String jsonData = serverClassInfo.toJson(this);
		return jsonData;
	}
	
}
