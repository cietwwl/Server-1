package com.rwbase.dao.guildSecretArea;

import java.util.ArrayList;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.guildSecretArea.projo.SecretUserChange;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;



public class SecretAreaBattleInfoHolder {//战斗数据
	
	private SecretAreaBattleInfoDAO secretAreaBattleInfoDAO = SecretAreaBattleInfoDAO.getInstance();
	private SecretAreaBattleInfo secretAreaBattleInfo;
	private boolean modified = false;
	final private eSynType synType = eSynType.SECRETAREA_BATTLE_INFO;
	
	public SecretAreaBattleInfoHolder(Player player) {
		secretAreaBattleInfo =  secretAreaBattleInfoDAO.get(player.getUserId());
		if(secretAreaBattleInfo == null){
			secretAreaBattleInfo = new SecretAreaBattleInfo();
			reset(player);
		}
	}

	public void synData(Player player){
		boolean isSend =reset(player);
		if(!isSend){
			ClientDataSynMgr.synData(player, secretAreaBattleInfo, synType, eSynOpType.UPDATE_SINGLE);
		}
	}

	
	public SecretAreaBattleInfo get(){
		return secretAreaBattleInfo;
	}
	public boolean reset(Player player){//重致
		boolean success =false;
		if(secretAreaBattleInfo.getEnemyChangeList()==null||secretAreaBattleInfo.getEnemyChangeList().size()>0){//上次血量变化没重致
			secretAreaBattleInfo.setUserId(player.getUserId());
			secretAreaBattleInfo.setEnemyChangeList(new ArrayList<SecretUserChange>());
			secretAreaBattleInfo.setPlayerChange(new SecretUserChange());
			success = secretAreaBattleInfoDAO.update(secretAreaBattleInfo);
			if(success){
				ClientDataSynMgr.updateData(player, secretAreaBattleInfo, synType, eSynOpType.UPDATE_SINGLE);
			}
		}
		return success;
	}
	//更新血量
	public void udpate(Player player){
		modified = true;;
		ClientDataSynMgr.updateData(player, secretAreaBattleInfo, synType, eSynOpType.UPDATE_SINGLE);
	}
	public void flush(){
		if(modified){
			secretAreaBattleInfoDAO.update(secretAreaBattleInfo);
			modified = false;
		}
	}

}