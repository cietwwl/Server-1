package com.rwbase.dao.peakArena.pojo;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "peak_arena_record")
public class TablePeakArenaRecord {

	@Id
	private String userId; // 用户ID
	private List<PeakRecordInfo> recordList;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<PeakRecordInfo> getRecordList() {
		return recordList;
	}
	public void setRecordList(List<PeakRecordInfo> recordList) {
		this.recordList = recordList;
	}
	
}
