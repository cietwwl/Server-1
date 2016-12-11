package com.rwbase.dao.serverPage;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_server_page")
public class TableServerPage {
	
	@Id
	private int pageId;           //分页id （推荐页永远放在第一页  推荐页 -2   测试页 -1   最近登陆  0）
	private String pageName;      //分页名字
	private String pageServers;   //分页的zoneId
	public int getPageId() {
		return pageId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public String getPageServers() {
		return pageServers;
	}
	public void setPageServers(String pageServers) {
		this.pageServers = pageServers;
	}
	
	
}
