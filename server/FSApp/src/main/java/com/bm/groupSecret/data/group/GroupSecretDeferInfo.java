package com.bm.groupSecret.data.group;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import com.playerdata.army.ArmyInfo;
import com.playerdata.dataSyn.annotation.SynClass;



@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupSecretDeferInfo {

	private ArmyInfo armyInfo;
	
	private String userId;
	
	private String userName;

	public ArmyInfo getArmyInfo() {
		return armyInfo;
	}

	public void setArmyInfo(ArmyInfo armyInfo) {
		this.armyInfo = armyInfo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	
	
}
