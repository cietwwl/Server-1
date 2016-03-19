package com.rwbase.dao.guildSecretArea;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.guildSecretArea.projo.SecretUserChange;



/**
 * 秘境战斗的信息，一个秘境一个
 * @author allen
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "secretarea_battleinfo")
@SynClass
public class SecretAreaBattleInfo {
	@Id
	private String userId;//进攻方用户id
	
	/**秘境战斗信息***/	
	private List<SecretUserChange> enemyChangeList;//	map<玩家id,enemyHeroChangerList<佣兵id>对方佣兵血量变化> //玩家挑战信息 @挑战完为空
	private SecretUserChange playerChange;//自己血量变化@挑战完为空

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<SecretUserChange> getEnemyChangeList() {
		return enemyChangeList;
	}

	public void setEnemyChangeList(List<SecretUserChange> enemyChangeList) {
		this.enemyChangeList = enemyChangeList;
	}

	public SecretUserChange getPlayerChange() {
		return playerChange;
	}

	public void setPlayerChange(SecretUserChange playerChange) {
		this.playerChange = playerChange;
	}

	
}
