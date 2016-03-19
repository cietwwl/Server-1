package com.rwbase.dao.guildSecretArea.projo;

import com.rwbase.dao.guildSecretArea.projo.ESecretType;

public class SecretAttrackInfo {
	private String id;//id
	private String secretId;//进攻秘境id
	private String attrackPlayerId;//进攻玩家id
	private long attrackTime;//进攻时间
	private int getGold;//获得钻石
	private int keyNum;//获得密钥
	private ESecretType type;//资源类型
	private int getTypeNum;//获得对应资源数量
	private Boolean isWin;//胜利失败
	private int attrackCount;//进攻次数
	private String guildName;//所属公会
	private String regiondName;//所属区
	private String attrackPlayerName;//进攻玩家名
	public String getAttrackPlayerId() {
		return attrackPlayerId;
	}
	public void setAttrackPlayerId(String attrackPlayerId) {
		this.attrackPlayerId = attrackPlayerId;
	}
	public long getAttrackTime() {
		return attrackTime;
	}
	public void setAttrackTime(long attrackTime) {
		this.attrackTime = attrackTime;
	}
	public int getGetGold() {
		return getGold;
	}
	public void setGetGold(int getGold) {
		this.getGold = getGold;
	}
	public int getGetTypeNum() {
		return getTypeNum;
	}
	public void setGetTypeNum(int getTypeNum) {
		this.getTypeNum = getTypeNum;
	}
	public Boolean getIsWin() {
		return isWin;
	}
	public void setIsWin(Boolean isWin) {
		this.isWin = isWin;
	}
	public int getAttrackCount() {
		return attrackCount;
	}
	public void setAttrackCount(int attrackCount) {
		this.attrackCount = attrackCount;
	}
	public String getSecretId() {
		return secretId;
	}
	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}
	public ESecretType getType() {
		return type;
	}
	public void setType(ESecretType type) {
		this.type = type;
	}
	public String getGuildName() {
		return guildName;
	}
	public void setGuildName(String guildName) {
		this.guildName = guildName;
	}
	public String getRegiondName() {
		return regiondName;
	}
	public void setRegiondName(String regiondName) {
		this.regiondName = regiondName;
	}
	public String getAttrackPlayerName() {
		return attrackPlayerName;
	}
	public void setAttrackPlayerName(String attrackPlayerName) {
		this.attrackPlayerName = attrackPlayerName;
	}
	public int getKeyNum() {
		return keyNum;
	}
	public void setKeyNum(int keyNum) {
		this.keyNum = keyNum;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
