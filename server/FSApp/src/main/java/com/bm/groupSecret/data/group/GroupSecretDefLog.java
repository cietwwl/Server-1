package com.bm.groupSecret.data.group;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.army.ArmyInfo;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupSecretDefLog {

	private String attackerName;
	
	private String attackUserId;
	
	private List<ArmyInfo> attackArmy;
		
	//防守奖励
	private int rewardCount;
	
	//已经领取了奖励的 userId列表
	private List<String> rewardTakenUserIdList;

	public String getAttackerName() {
		return attackerName;
	}

	public void setAttackerName(String attackerName) {
		this.attackerName = attackerName;
	}

	public String getAttackUserId() {
		return attackUserId;
	}

	public void setAttackUserId(String attackUserId) {
		this.attackUserId = attackUserId;
	}

	public List<ArmyInfo> getAttackArmy() {
		return attackArmy;
	}

	public void setAttackArmy(List<ArmyInfo> attackArmy) {
		this.attackArmy = attackArmy;
	}

	public int getRewardCount() {
		return rewardCount;
	}

	public void setRewardCount(int rewardCount) {
		this.rewardCount = rewardCount;
	}

	public List<String> getRewardTakenUserIdList() {
		return rewardTakenUserIdList;
	}

	public void setRewardTakenUserIdList(List<String> rewardTakenUserIdList) {
		this.rewardTakenUserIdList = rewardTakenUserIdList;
	}
	
	
	
	
	
}
