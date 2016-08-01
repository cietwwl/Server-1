package com.rwbase.dao.user.platformwhitelist;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.cacheDao.mapItem.IMapItem;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_platform_whitelist")
public class TablePlatformWhiteList implements IMapItem{
	@Id
	private String accountId;
	private boolean isClose=true;
	
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public boolean isClose() {
		return isClose;
	}

	public void setClose(boolean isClose) {
		this.isClose = isClose;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.accountId;
	}

}
