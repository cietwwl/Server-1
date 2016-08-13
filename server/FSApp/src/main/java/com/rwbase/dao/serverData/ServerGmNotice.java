package com.rwbase.dao.serverData;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.dao.annotation.SaveAsJson;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "server_gm_notice")
public class ServerGmNotice {
	@Id
	private long id;
	@SaveAsJson
	private GmNoticeInfo noticeInfo;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public GmNoticeInfo getNoticeInfo() {
		return noticeInfo;
	}
	public void setNoticeInfo(GmNoticeInfo noticeInfo) {
		this.noticeInfo = noticeInfo;
	}
}
