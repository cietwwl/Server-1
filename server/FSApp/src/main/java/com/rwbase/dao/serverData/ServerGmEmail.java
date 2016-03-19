package com.rwbase.dao.serverData;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.common.playerFilter.PlayerFilterCondition;
import com.rwbase.dao.email.EmailData;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerGmEmail {
	private EmailData sendToAllEmailData;
	private List<PlayerFilterCondition> conditionList = new ArrayList<PlayerFilterCondition>();
	private int status;
	
	public EmailData getSendToAllEmailData() {
		return sendToAllEmailData;
	}
	public void setSendToAllEmailData(EmailData sendToAllEmailData) {
		this.sendToAllEmailData = sendToAllEmailData;
	}
	public List<PlayerFilterCondition> getConditionList() {
		return conditionList;
	}
	public void setConditionList(List<PlayerFilterCondition> conditionList) {
		this.conditionList = conditionList;
	}
	public long getEmailTaskId(){
		return sendToAllEmailData.getTaskId();
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
