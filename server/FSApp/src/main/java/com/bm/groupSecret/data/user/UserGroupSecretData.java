package com.bm.groupSecret.data.user;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "user_group_secret_data")
@SynClass
public class UserGroupSecretData {
	
	@Id
	private String id;

	private GroupSecretBattleInfo battleInfo;
	
	private List<String> invitedSecretIdList;
	
	private List<String> joinSecretIdList;
	
	private List<String> owenSecretIdList;

	public GroupSecretBattleInfo getBattleInfo() {
		return battleInfo;
	}

	public void setBattleInfo(GroupSecretBattleInfo battleInfo) {
		this.battleInfo = battleInfo;
	}

	public List<String> getInvitedSecretIdList() {
		return invitedSecretIdList;
	}

	public void setInvitedSecretIdList(List<String> invitedSecretIdList) {
		this.invitedSecretIdList = invitedSecretIdList;
	}

	public List<String> getJoinSecretIdList() {
		return joinSecretIdList;
	}

	public void setJoinSecretIdList(List<String> joinSecretIdList) {
		this.joinSecretIdList = joinSecretIdList;
	}

	public List<String> getOwenSecretIdList() {
		return owenSecretIdList;
	}

	public void setOwenSecretIdList(List<String> owenSecretIdList) {
		this.owenSecretIdList = owenSecretIdList;
	}
	
	
	
	
}
