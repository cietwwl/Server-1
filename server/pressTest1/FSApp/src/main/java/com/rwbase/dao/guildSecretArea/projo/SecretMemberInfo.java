package com.rwbase.dao.guildSecretArea.projo;

import java.util.List;
import java.util.Map;

import com.rwbase.dao.tower.pojo.TowerHeroChange;
import com.rwbase.dao.guildSecretArea.projo.ESecretType;
public class SecretMemberInfo {//转为客户端所需内容数据格式
	private String palyerId;//玩家id
	private int secretKey;//玩家拥有密钥数量(player)
	private int buKeyTimes;//购买次数  空

	private List<String> areaIdList;
	private List<String> HeroIdList;
	public String getPalyerId() {
		return palyerId;
	}
	public void setPalyerId(String palyerId) {
		this.palyerId = palyerId;
	}
	public int getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(int secretKey) {
		this.secretKey = secretKey;
	}
	public int getBuKeyTimes() {
		return buKeyTimes;
	}
	public void setBuKeyTimes(int buKeyTimes) {
		this.buKeyTimes = buKeyTimes;
	}
	public List<String> getAreaIdList() {
		return areaIdList;
	}
	public void setAreaIdList(List<String> areaIdList) {
		this.areaIdList = areaIdList;
	}
	public List<String> getHeroIdList() {
		return HeroIdList;
	}
	public void setHeroIdList(List<String> heroIdList) {
		HeroIdList = heroIdList;
	}
	 
}
