package com.rwbase.dao.guildSecretArea.projo;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class SecretArmy {//驻守信息
	private String secretId;//秘境id
	private String userId;
	private List<String> heroIdList;//uuid
	private int battleForce;//驻守佣兵总战斗力
	private long sourceChangeTime;//队伍调整时间  或更新时间
	private int sourceNum;//剩余总资源数量
	private int GuildMaterial;//公会材料总资源数量
	private boolean isAllDead;
	public String getSecretId() {
		return secretId;
	}
	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<String> getHeroIdList() {
		return heroIdList;
	}
	public void setHeroIdList(List<String> heroIdList) {
		this.heroIdList = heroIdList;
	}
	public int getBattleForce() {
		return battleForce;
	}
	public void setBattleForce(int battleForce) {
		this.battleForce = battleForce;
	}
	public long getSourceChangeTime() {
		return sourceChangeTime;
	}
	public void setSourceChangeTime(long sourceChangeTime) {
		this.sourceChangeTime = sourceChangeTime;
	}
	public int getSourceNum() {
		return sourceNum;
	}
	public void setSourceNum(int sourceNum) {
		this.sourceNum = sourceNum;
	}
	public boolean isAllDead() {
		return isAllDead;
	}
	public void setAllDead(boolean isAllDead) {
		this.isAllDead = isAllDead;
	}
	public int getGuildMaterial() {
		return GuildMaterial;
	}
	public void setGuildMaterial(int guildMaterial) {
		GuildMaterial = guildMaterial;
	}
}
