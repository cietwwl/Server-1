package com.bm.groupSecret.data.user;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.ArmyInfo;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupSecretBattleInfo {

	private ArmyInfo attackArmy;

	private List<ArmyInfo> defArmyList;

	private List<ArmyInfo> loseArmyList;

	public ArmyInfo getAttackArmy() {
		return attackArmy;
	}

	public void setAttackArmy(ArmyInfo attackArmy) {
		this.attackArmy = attackArmy;
	}

	public List<ArmyInfo> getDefArmyList() {
		return defArmyList;
	}

	public void setDefArmyList(List<ArmyInfo> defArmyList) {
		this.defArmyList = defArmyList;
	}

	public List<ArmyInfo> getLoseArmyList() {
		return loseArmyList;
	}

	public void setLoseArmyList(List<ArmyInfo> loseArmyList) {
		this.loseArmyList = loseArmyList;
	}
}