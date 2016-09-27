package com.rw.db.tablesAfterMerge.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.db.dao.annotation.SaveAsJson;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TableEmail {
	@Id
	private String userId;
	
	private Map<String, EmailItem> emailList = new HashMap<String, EmailItem>();
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Map<String, EmailItem> getEmailList() {
		return emailList;
	}
	public void setEmailList(Map<String, EmailItem> emailList) {
		this.emailList = emailList;
	}
	
	public void addEmail(EmailItem email){
		this.emailList.put(email.getEmailId(), email);
	}
}
