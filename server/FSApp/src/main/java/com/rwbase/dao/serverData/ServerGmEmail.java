package com.rwbase.dao.serverData;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.common.playerFilter.PlayerFilterCondition;
import com.rw.fsutil.dao.annotation.SaveAsJson;
import com.rwbase.dao.email.EmailData;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "server_gm_email")
public class ServerGmEmail {
	@Id
	private long id;
	@SaveAsJson
	private EmailData sendToAllEmailData;
	@SaveAsJson
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
