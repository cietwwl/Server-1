package com.rwbase.dao.user.pojo;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_unique_name")
public class TableUniqueName {
	
	@Id
	private String md5;
	private String nick;
	private List<Integer> zoneList;
	
	public TableUniqueName() {
		
	}
	
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public List<Integer> getZoneList() {
		return zoneList;
	}
	public void setZoneList(List<Integer> zoneList) {
		this.zoneList = zoneList;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
}
