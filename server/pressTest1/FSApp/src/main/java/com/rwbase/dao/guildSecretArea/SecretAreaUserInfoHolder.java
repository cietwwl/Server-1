package com.rwbase.dao.guildSecretArea;

import java.util.ArrayList;

import com.playerdata.Player;
import com.playerdata.SecretAreaMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class SecretAreaUserInfoHolder {//玩家基本信息
	
	private SecretAreaUserInfoDAO secretAreaUserInfoDAO = SecretAreaUserInfoDAO.getInstance();
	private SecretAreaUserInfo secretAreaUserInfo;
	private boolean modified = false;
	final private eSynType synType = eSynType.SECRETAREA_USER_INFO;
	
	public SecretAreaUserInfoHolder(Player player) {
		String roleIdP = player.getUserId();
		secretAreaUserInfo =  secretAreaUserInfoDAO.get(roleIdP );
		if(secretAreaUserInfo == null){
			SecretAreaUserInfo newUserInfo = new SecretAreaUserInfo();
			newUserInfo.setUserId(roleIdP);
			newUserInfo.setSecretKey(SecretAreaMgr.SECRET_KEY_RECOVER_LIMIT);
			newUserInfo.setBuyKeyCount(0);
			newUserInfo.setAttrackCount(0);
			newUserInfo.setKeyUseTime(0);
			newUserInfo.setOwnAreaIdList(new ArrayList<String>());
			newUserInfo.setCurrentAreaList(new ArrayList<String>());
			boolean success = secretAreaUserInfoDAO.update(newUserInfo);
			if(success){
				secretAreaUserInfo = newUserInfo;
				ClientDataSynMgr.updateData(player, secretAreaUserInfo, synType, eSynOpType.UPDATE_SINGLE);
			}
		}
	}
	

	public void synData(Player player){
		ClientDataSynMgr.synData(player, secretAreaUserInfo, synType, eSynOpType.UPDATE_SINGLE);
	}

	
	public SecretAreaUserInfo get(){
		return secretAreaUserInfo;
	}

	public void udpate(Player player){
		modified = true;
		ClientDataSynMgr.updateData(player, secretAreaUserInfo, synType, eSynOpType.UPDATE_SINGLE);
		if(modified){//test  fluash无作用
			secretAreaUserInfoDAO.update(secretAreaUserInfo);
			modified = false;
		}
	}
	
	public void flush(){
		if(modified){
			secretAreaUserInfoDAO.update(secretAreaUserInfo);
			modified = false;
		}
	}



}