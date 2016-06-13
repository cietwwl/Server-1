package com.log.logToDataCenter.errorStore;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.dao.annotation.CombineSave;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "log_store")
public class LogStoreInfo {

	@Id
	private String log_Id; 

	@CombineSave
	private String log_content;

	public LogStoreInfo() {}
	
	public LogStoreInfo(String log_Id, String log_content) {
		this.log_Id = log_Id;
		this.log_content = log_content;
	}
	
	public String getLogId() {
		return log_Id;
	}

	public void setLogId(String userId) {
		this.log_Id = userId;
	}

	public String getLogContent() {
		return log_content;
	}

	public void setLogContent(String log_content) {
		this.log_content = log_content;
	}
}
