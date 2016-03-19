package com.rwbase.dao.gulid.faction;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

/**
 * 
 * 帮会申请
 * @author allen
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "guild_applyinfo")
@SynClass
public class GuildApplyInfo implements IMapItem {
	
	@Id
	private String id;
	private String guildId;
	
	@CombineSave
	private String playerId;
	@CombineSave
	private String playerName;
	@CombineSave
	private int level;
	@CombineSave
	private long applyTime;
	@CombineSave
	private String iconId;


	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGuildId() {
		return guildId;
	}

	public void setGuildId(String guildId) {
		this.guildId = guildId;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(long applyTime) {
		this.applyTime = applyTime;
	}

	public String getIconId() {
		return iconId;
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

}
