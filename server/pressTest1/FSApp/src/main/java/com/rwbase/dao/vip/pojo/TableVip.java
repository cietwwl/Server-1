package com.rwbase.dao.vip.pojo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.common.enu.EPrivilegeDef;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_table_vip")
@SynClass
public class TableVip {
	@Id
	private String userId;
	private Map<EPrivilegeDef, Integer> privilege = new ConcurrentHashMap<EPrivilegeDef, Integer>();
	private long lastRefreshTime;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getLastRefreshTime() {
		return lastRefreshTime;
	}

	public void setLastRefreshTime(long lastRefreshTime) {
		this.lastRefreshTime = lastRefreshTime;
	}

	public Map<EPrivilegeDef, Integer> getPrivilege() {
		return privilege;
	}

	public void setPrivilege(ConcurrentHashMap<EPrivilegeDef, Integer> privilege) {
		this.privilege = privilege;
	}
	
	public void addPrivilege(EPrivilegeDef key,int value){
		privilege.put(key, value);
	}
	
	public int getPrivilege(EPrivilegeDef key){
		return privilege.get(key);
	}
	
	public boolean containPrivilege(EPrivilegeDef key){
		return privilege.containsKey(key);
	}
}
