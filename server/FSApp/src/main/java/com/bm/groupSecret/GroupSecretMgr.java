package com.bm.groupSecret;

import java.util.List;

import com.bm.groupSecret.data.group.GroupSecretData;
import com.bm.groupSecret.data.group.GroupSecretDataDAO;
import com.bm.groupSecret.data.group.GroupSecretDefLog;
import com.bm.groupSecret.data.group.GroupSecretDefLogHolder;
import com.bm.groupSecret.data.user.UserGroupSecretMgr;
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
	
	public List<GroupSecretDefLog> getDefLogList(){
		
		return GroupSecretDefLogHolder.getInstance().getItemList(secretId);
	}
	
	public void synToClient(Player player){
		UserGroupSecretMgr.getInstance().synSingleSecret(player, secretId);
	}
	
	
}
