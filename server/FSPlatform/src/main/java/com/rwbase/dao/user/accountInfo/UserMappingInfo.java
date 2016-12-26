package com.rwbase.dao.user.accountInfo;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;



@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_user_mapping")
public class UserMappingInfo {


	@Id
	private String user_id;
	
	private String account_id;
	
	private String open_account;
	
	private int zone_id;

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public String getOpen_account() {
		return open_account;
	}

	public void setOpen_account(String open_account) {
		this.open_account = open_account;
	}

	public int getZone_id() {
		return zone_id;
	}

	public void setZone_id(int zone_id) {
		this.zone_id = zone_id;
	}
	
	
	
}
