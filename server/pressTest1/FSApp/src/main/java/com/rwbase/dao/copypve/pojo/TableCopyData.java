package com.rwbase.dao.copypve.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_copy_data")
public class TableCopyData {
	
	@Id
	private String userId; // 用户ID
	private List<CopyData> copyList = new ArrayList<CopyData>();//副本列表
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<CopyData> getCopyList() {
		return copyList;
	}
	public void setCopyList(List<CopyData> copyList) {
		this.copyList = copyList;
	}

}
