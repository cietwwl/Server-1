package com.rwbase.dao.arena.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "arena_record")
public class TableArenaRecord {

	@Id
	private String userId; // 用户ID
	private List<RecordInfo> recordList;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<RecordInfo> getRecordList() {
		//TODO 不是线程安全
		if(recordList == null){
			recordList = new ArrayList<RecordInfo>();
		}
		return recordList;
	}

	public void setRecordList(List<RecordInfo> recordList) {
		if (recordList != null) {
			this.recordList = recordList;
		}
	}

}
