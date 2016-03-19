package com.rwbase.dao.gulid.faction;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

/**
 * 
 * 帮会成员信息
 * @author allen
 *
 */


@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "guild_member")
@SynClass
public class GuildMember implements IMapItem {
		
	@Id
	private String userId;
	private String guildId;
	
	@CombineSave
	private String playerName;
	@CombineSave
	private GuildPositionType position;
	@CombineSave
	private int contribute;
	@CombineSave
	private int totalContribute;
	@CombineSave
	private int level;
	@CombineSave
	private long joinTime;
	@CombineSave
	private long loginTime;	
	@CombineSave
	private boolean isDonate;//每天只能捐献一次	
	@CombineSave
	private String iconId;
	



	@Override
	public String getId() {
		return userId;
	}




	public String getUserId() {
		return userId;
	}




	public void setUserId(String userId) {
		this.userId = userId;
	}




	public String getGuildId() {
		return guildId;
	}




	public void setGuildId(String guildId) {
		this.guildId = guildId;
	}




	public String getPlayerName() {
		return playerName;
	}




	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}





	public GuildPositionType getPosition() {
		return position;
	}




	public void setPosition(GuildPositionType position) {
		this.position = position;
	}




	public int getContribute() {
		return contribute;
	}




	public void setContribute(int contribute) {
		this.contribute = contribute;
	}




	public int getTotalContribute() {
		return totalContribute;
	}




	public void setTotalContribute(int totalContribute) {
		this.totalContribute = totalContribute;
	}




	public int getLevel() {
		return level;
	}




	public void setLevel(int level) {
		this.level = level;
	}




	public long getJoinTime() {
		return joinTime;
	}




	public void setJoinTime(long joinTime) {
		this.joinTime = joinTime;
	}




	public long getLoginTime() {
		return loginTime;
	}




	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}




	public boolean isDonate() {
		return isDonate;
	}




	public void setDonate(boolean isDonate) {
		this.isDonate = isDonate;
	}




	public String getIconId() {
		return iconId;
	}




	public void setIconId(String iconId) {
		this.iconId = iconId;
	}
	



}
