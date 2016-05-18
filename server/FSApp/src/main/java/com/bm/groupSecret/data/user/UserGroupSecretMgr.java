package com.bm.groupSecret.data.user;

import java.util.ArrayList;
import java.util.List;

import com.bm.groupSecret.data.group.GroupSecretDataHolder;
import com.bm.groupSecret.data.user.UserGroupSecretData;
import com.bm.groupSecret.data.user.UserGroupSecretDataHolder;
import com.playerdata.Player;

public class UserGroupSecretMgr {
	
	private static UserGroupSecretMgr instance = new UserGroupSecretMgr();
	
	public static UserGroupSecretMgr getInstance(){
		return instance;
	}

	public List<String> getUserSecretIds(Player player){
		
		UserGroupSecretData userGroupSecretData = UserGroupSecretDataHolder.getInstance().get(player);	
		List<String> joinSecretIdList = userGroupSecretData.getJoinSecretIdList();		
		List<String> owenSecretIdList = userGroupSecretData.getOwenSecretIdList();
		
		List<String> secretIdList = new ArrayList<String>();
		secretIdList.addAll(owenSecretIdList);
		secretIdList.addAll(joinSecretIdList);
		
		return secretIdList;
	
	}
	
	public void synUserSecrets(Player player){
		List<String> secretIdList = getUserSecretIds(player);
		GroupSecretDataHolder.getInstance().synList(player, secretIdList);		
	}
	
	public void synSingleSecret(Player player,String secretId){		
		GroupSecretDataHolder.getInstance().synSingle(player, secretId);	
	}
	
}
