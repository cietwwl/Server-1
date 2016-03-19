package com.rwbase.dao.gulid.faction;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GuildHolder {
	

	private Guild guild;
	private boolean modified = false;
	final private eSynType synType = eSynType.Guild;
	
	public GuildHolder(Guild guildP) {
		guild = guildP;
	}

	public void synData(Player player){
		ClientDataSynMgr.synData(player, guild, synType, eSynOpType.UPDATE_SINGLE);
	}

	
	public Guild get(){
		return guild;
	}

	public void update(){
		modified = true;
	}
	
	public void addActivity(int add){
		guild.setActiveValue(guild.getActiveValue()+add);
		update();
	}
	public void setLevel(int level) {
		guild.setLevel(level);
		update();
		
	}
	public void addContribute(int contribute) {
		guild.setContribute(guild.getContribute()+contribute);
		guild.setTotalContribute(guild.getTotalContribute()+contribute);
		update();
		
	}
	
	public void flush(){
		if(modified){
			GuildDAO.getInstance().update(guild);
			modified = false;
		}
	}



}