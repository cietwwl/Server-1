package com.rw.db.tablesAfterMerge.arena;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.db.dao.annotation.NonSave;
import com.rw.db.dao.annotation.ReadNotWrite;
import com.rw.db.dao.annotation.SaveAsJson;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "ranking_swap")
public class ArenaExt {
	@Id
	private long id;
	private String primary_key;
	private int type;
	private int ranking;
	@ReadNotWrite
	private long lastLoginTime;
	@SaveAsJson
	private ArenaExtAttribute extension;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPrimary_key() {
		return primary_key;
	}
	public void setPrimary_key(String primary_key) {
		this.primary_key = primary_key;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getRanking() {
		return ranking;
	}
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}
	public ArenaExtAttribute getExtension() {
		return extension;
	}
	public void setExtension(ArenaExtAttribute extension) {
		this.extension = extension;
	}
	public long getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
}
