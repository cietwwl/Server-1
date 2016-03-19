package com.rwbase.dao.guildSecretArea;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;


/**
 * 秘境里和跟用户的信息，一个用户一个
 * @author allen
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "secretarea_userinfo")
@SynClass
public class SecretAreaUserInfo {
	@Id
	private String userId;//玩家id
	private int secretKey;//玩家拥有密钥数量(player)
	private int buyKeyCount;//购买次数
	private int attrackCount;//进攻次数
	private long KeyUseTime;//密钥使用时间（回复计时）
	private List<String> ownAreaIdList;//重复利用的秘境id
	private List<String> currentAreaList;//当前开启的秘境

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(int secretKey) {
		this.secretKey = secretKey;
	}
	public int getBuyKeyCount() {
		return buyKeyCount;
	}
	public void setBuyKeyCount(int buyKeyCount) {
		this.buyKeyCount = buyKeyCount;
	}
	public int getAttrackCount() {
		return attrackCount;
	}
	public void setAttrackCount(int attrackCount) {
		this.attrackCount = attrackCount;
	}

	public List<String> getOwnAreaIdList() {
		return ownAreaIdList;
	}
	public void setOwnAreaIdList(List<String> ownAreaIdList) {
		this.ownAreaIdList = ownAreaIdList;
	}
	public List<String> getCurrentAreaList() {
		return currentAreaList;
	}
	public void setCurrentAreaList(List<String> currentAreaList) {
		this.currentAreaList = currentAreaList;
	}
	public long getKeyUseTime() {
		return KeyUseTime;
	}
	public void setKeyUseTime(long keyUseTime) {
		KeyUseTime = keyUseTime;
	}
}
