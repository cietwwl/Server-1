package com.playerdata.activityCommon.activityType;

import java.util.List;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;

public interface ActivityTypeItemIF<T> extends RoleExtProperty{
	
	public void setId(int id);

	public void setUserId(String userId);

	public void setCfgId(String cfgId);
	
	public String getCfgId();
	
	public void setVersion(int version);
	
	public void setClosed(boolean isClose);
	
	public void setSubItemList(List<T> subItemList);
	
	public List<T> getSubItemList();
	
	public boolean isHasViewed();
	
	public void setHasViewed(boolean hasViewed);
	
	public void reset();
}
