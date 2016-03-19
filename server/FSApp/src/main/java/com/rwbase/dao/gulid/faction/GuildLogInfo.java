package com.rwbase.dao.gulid.faction;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

/**
 * 
 * 帮会日志
 * @author allen
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "guild_loginfo")
@SynClass
public class GuildLogInfo implements IMapItem{
	
	@Id
	private String id;
	private String guildId;
	
	@CombineSave
	private String myName;
	@CombineSave
	private String otherName;
	@CombineSave
	private String des = "";
	@CombineSave
	private long createTime;
	@CombineSave
	private int type;

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

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMyName() {
		return myName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

	public String getOtherName() {
		if (otherName == null) {
			return "";
		}
		return otherName;
	}

	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}

	public String getDes() {
		if (des == null) {
			return "";
		}
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}



}
