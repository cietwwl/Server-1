package com.bm.groupSecret;

import com.bm.groupSecret.data.group.GroupSecretData;
import com.bm.groupSecret.data.group.GroupSecretDataDAO;
import com.playerdata.Player;

public class GroupSecretMgr {
	
	private String secretId;
	
	public GroupSecretMgr(String secretIdP){
		this.secretId = secretIdP;
	}
	
	public GroupSecretComResult joinDef(Player player){
		return null;
	}
	
	public GroupSecretComResult getSecretReward(Player player){
		return null;
	}
	
	public GroupSecretComResult getDefReward(Player player, String secretId, String defLogId){
		return null;
	}
	
	public GroupSecretData getSecretData(){
		return GroupSecretDataDAO.getInstance().get(secretId);
	}
	
	
}
