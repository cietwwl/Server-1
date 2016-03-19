package com.playerdata.guild;

import com.rwbase.dao.gulid.faction.Guild;
import com.rwbase.dao.gulid.faction.GuildApplyInfoHolder;
import com.rwbase.dao.gulid.faction.GuildHolder;
import com.rwbase.dao.gulid.faction.GuildLogInfoHolder;
import com.rwbase.dao.gulid.faction.GuildMemberHolder;


public class GuildDataMgr {

	
	private GuildHolder guildHolder;
	private GuildMemberHolder guildMemberHolder;
	private GuildLogInfoHolder guildLogInfoHolder;
	private GuildApplyInfoHolder guildApplyInfoHolder;
	
	//管理类，没有自己的数据结构，不需要flush
	private GuildSettingMgr guildSettingMgr;
	private GuildMemberTSMgr guildMemberTSMgr;
	private GuildPropTSMgr guildPropTSMgr;
	
	public GuildDataMgr(Guild guildP){
		if(guildP!=null){
			String guildId = guildP.getId();
			guildHolder = new GuildHolder(guildP);
			guildMemberHolder = new GuildMemberHolder(guildId);
			guildLogInfoHolder = new GuildLogInfoHolder(guildId);
			guildApplyInfoHolder = new GuildApplyInfoHolder(guildId);
			
			guildSettingMgr = new GuildSettingMgr(guildId);
			guildMemberTSMgr = new GuildMemberTSMgr(guildId);
			guildPropTSMgr = new GuildPropTSMgr(guildId);
			
		}
	}
	

	public GuildHolder getGuildHolder() {
		return guildHolder;
	}

	public GuildMemberHolder getGuildMemberHolder() {
		return guildMemberHolder;
	}

	public GuildLogInfoHolder getGuildLogInfoHolder() {
		return guildLogInfoHolder;
	}

	public GuildApplyInfoHolder getGuildApplyInfoHolder() {
		return guildApplyInfoHolder;
	}
	
	
	public GuildSettingMgr getGuildSettingMgr() {
		return guildSettingMgr;
	}
	
	

	public GuildMemberTSMgr getGuildMemberTSMgr() {
		return guildMemberTSMgr;
	}


	public GuildPropTSMgr getGuildPropTSMgr() {
		return guildPropTSMgr;
	}


	public void flush(){
		guildHolder.flush();
		guildMemberHolder.flush();
		guildLogInfoHolder.flush();
		guildApplyInfoHolder.flush();
	}
	
	
	
}
