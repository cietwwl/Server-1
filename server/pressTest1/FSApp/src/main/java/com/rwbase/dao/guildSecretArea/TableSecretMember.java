package com.rwbase.dao.guildSecretArea;

import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.dao.guildSecretArea.projo.SecretAttrackInfo;
import com.rwbase.dao.tower.pojo.TowerHeroChange;
import com.rwbase.dao.guildSecretArea.projo.ESecretType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "secretmember")
public class TableSecretMember {
	@Id
	private String palyerId;//玩家id
	private int secretKey;//玩家拥有密钥数量(player)
	private int expNum;//玩家拥有经验数量(player)
	private int strenNUm;//玩家拥有强化石数量(player)
	private int buyKeyCount;//购买次数
	private int attrackCount;//进攻次数
	private long KeyUseTime;//使用时间
	private Boolean isFight;//使用时间
	private Map<String,List<String>> applyHeroMap;//map<秘境id，佣兵idList> 所开秘境及派驻佣兵list  
	private Map<String,List<TowerHeroChange>> enemyChangeList;//	map<玩家id,enemyHeroChangerList<佣兵id>对方佣兵血量变化> //玩家挑战信息 @挑战完为空
	private List<TowerHeroChange> playerChangeList;//自己血量变化@挑战完为空
	private Map<String,List<SecretAttrackInfo>> attrackInfoMap;//<进攻玩家id,<进攻记录>>
	//private Map<String,Map<String,SecretAttrackInfo>> attrackInfoMap;//map<playerId 被进公的玩家id,Map<attrackPlayerId,secretAttackInfo>> //进攻记录@ 
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
	public int getBuyKeyCount() {
		return buyKeyCount;
	}
	public void setBuyKeyCount(int buKeyCount) {
		this.buyKeyCount = buKeyCount;
	}

	public List<TowerHeroChange> getPlayerChangeList() {
		return playerChangeList;
	}
	public void setPlayerChangeList(List<TowerHeroChange> playerChangeList) {
		this.playerChangeList = playerChangeList;
	}
	public Map<String,List<String>> getApplyHeroMap() {
		return applyHeroMap;
	}
	public void setApplyHeroMap(Map<String,List<String>> applyHeroMap) {
		this.applyHeroMap = applyHeroMap;
	}
	public Map<String,List<SecretAttrackInfo>> getAttrackInfoMap() {
		return attrackInfoMap;
	}
	public void setAttrackInfoMap(Map<String,List<SecretAttrackInfo>> attrackInfoMap) {
		this.attrackInfoMap = attrackInfoMap;
	}
	public Map<String,List<TowerHeroChange>> getEnemyChangeList() {
		return enemyChangeList;
	}
	public void setEnemyChangeList(Map<String,List<TowerHeroChange>> enemyChangeList) {
		this.enemyChangeList = enemyChangeList;
	}
	public long getKeyUseTime() {
		return KeyUseTime;
	}
	public void setKeyUseTime(long keyUseTime) {
		KeyUseTime = keyUseTime;
	}
	public Boolean getIsFight() {
		return isFight;
	}
	public void setIsFight(Boolean isFight) {
		this.isFight = isFight;
	}
	public int getExpNum() {
		return expNum;
	}
	public void setExpNum(int expNum) {
		this.expNum = expNum;
	}
	public int getStrenNUm() {
		return strenNUm;
	}
	public void setStrenNUm(int strenNUm) {
		this.strenNUm = strenNUm;
	}
	public int getAttrackCount() {
		return attrackCount;
	}
	public void setAttrackCount(int attrackCount) {
		this.attrackCount = attrackCount;
	}
	
}
