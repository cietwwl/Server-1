package com.bm.groupSecret;

import java.util.List;

import com.bm.groupSecret.data.group.GroupSecretData;
import com.bm.groupSecret.data.group.GroupSecretDataHolder;
import com.bm.groupSecret.data.user.UserGroupSecretMgr;
import com.playerdata.Player;



public final class GroupSecretBM {	
	
	public static GroupSecretBM getInstance(){
		return null;
	}
	
	public GroupSecretMgr openSecret(Player player, GroupSecretType groupSecretType){

			boolean success  = false;
			String newSecretId = GroupSecretHelper.newSecretId(player, groupSecretType);
			//一个类型只能有一个秘境，不论是自己创建的还是受邀参加的
			List<GroupSecretType> userSecretTypeList = UserGroupSecretMgr.getInstance().getUserSecretTypes(player);
			
			if(!userSecretTypeList.contains(groupSecretType)){
//				long now = System.currentTimeMillis();
				// 帮派基础信息
				GroupSecretData secretData = new GroupSecretData();
				secretData.setId(newSecretId);
				
				success = GroupSecretDataHolder.getInstance().newData(secretData);
				if(success){
					 UserGroupSecretMgr.getInstance().addOwnenSecretId(player, newSecretId);					
				}
			}
			
		return success? new GroupSecretMgr(newSecretId):null;
	}
	
	public GroupSecretMgr getSecret(String secretId){
		GroupSecretData groupSecretData = GroupSecretDataHolder.getInstance().get(secretId);
		return groupSecretData != null?new GroupSecretMgr(secretId):null ;
	}
	
	//搜索
	public GroupSecretMgr findMatch(){
		
		return null;
	}
	


}