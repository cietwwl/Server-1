package com.rwbase.dao.gulid.faction;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GuildUserInfoHolder {
	
	private GuildUserInfoDAO guildUserInfoDAO = GuildUserInfoDAO.getInstance();
	private GuildUserInfo guildUserInfo;
	private boolean modified = false;
	final private eSynType synType = eSynType.GUILD_USER_INFO;
	
	public GuildUserInfoHolder(Player player) {
		String roleIdP = player.getUserId();
		guildUserInfo =  guildUserInfoDAO.get(roleIdP );
		if(guildUserInfo == null){
			GuildUserInfo newUserInfo = new GuildUserInfo();
			newUserInfo.setUserId(roleIdP);
			boolean success = guildUserInfoDAO.update(newUserInfo);
			if(success){
				guildUserInfo = newUserInfo;
				ClientDataSynMgr.updateData(player, guildUserInfo, synType, eSynOpType.UPDATE_SINGLE);
			}
		}
	}
	

	public void synData(Player player){
		ClientDataSynMgr.synData(player, guildUserInfo, synType, eSynOpType.UPDATE_SINGLE);
	}

	
	public GuildUserInfo get(){
		return guildUserInfo;
	}
	
	//更换帮派的时候调用
	public void reset(){
		GuildUserInfo newItem = new GuildUserInfo();
		newItem.setUserId(guildUserInfo.getUserId());
		guildUserInfo = newItem;
	}

	public void update(Player player){
		modified = true;
		ClientDataSynMgr.updateData(player, guildUserInfo, synType, eSynOpType.UPDATE_SINGLE);
		if(modified){
			guildUserInfoDAO.update(guildUserInfo);
			modified = false;
		}
	}
	
	public void flush(){
		if(modified){
			guildUserInfoDAO.update(guildUserInfo);
			modified = false;
		}
	}



}