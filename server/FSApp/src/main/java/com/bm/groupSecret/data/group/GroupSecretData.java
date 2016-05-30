package com.bm.groupSecret.data.group;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "group_secret_data")
@SynClass
public class GroupSecretData {
	
	@Id
	private String id;
	
	//秘境的主人
	private String ownerId;

	private GroupSecretBaseData groupSecretBaseData = new GroupSecretBaseData();
	
	private GroupSecretReward groupSecretReward = new GroupSecretReward();
	
	private GroupSecretDefData groupSecretDefData = new GroupSecretDefData();
	
	//邀请的用户id列表
	@IgnoreSynField
	private List<String> invitedUserIdList;
	
	//参与驻守的用户Id列表
	private List<String> joinedUserIdList;	
	
	//秘境开启时间
	private long startTime;
	
	@IgnoreSynField
	private int defLogNo = 1;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public GroupSecretBaseData getGroupSecretBaseData() {
		return groupSecretBaseData;
	}

	public void setGroupSecretBaseData(GroupSecretBaseData groupSecretBaseData) {
		this.groupSecretBaseData = groupSecretBaseData;
	}

	public GroupSecretReward getGroupSecretReward() {
		return groupSecretReward;
	}

	public void setGroupSecretReward(GroupSecretReward groupSecretReward) {
		this.groupSecretReward = groupSecretReward;
	}

	public GroupSecretDefData getGroupSecretDefData() {
		return groupSecretDefData;
	}

	public void setGroupSecretDefData(GroupSecretDefData groupSecretDefData) {
		this.groupSecretDefData = groupSecretDefData;
	}

	public List<String> getInvitedUserIdList() {
		return invitedUserIdList;
	}

	public void setInvitedUserIdList(List<String> invitedUserIdList) {
		this.invitedUserIdList = invitedUserIdList;
	}

	public List<String> getJoinedUserIdList() {
		return joinedUserIdList;
	}

	public void setJoinedUserIdList(List<String> joinedUserIdList) {
		this.joinedUserIdList = joinedUserIdList;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	
	
}
