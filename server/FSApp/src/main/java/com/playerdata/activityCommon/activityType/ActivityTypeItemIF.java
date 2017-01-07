package com.playerdata.activityCommon.activityType;

import java.util.List;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;

public interface ActivityTypeItemIF<T> extends RoleExtProperty{
	
	public void setId(int id);

	public void setUserId(String userId);

	public void setCfgId(String cfgId);
	
	public String getCfgId();
	
	public void setVersion(int version);
	
	public int getVersion();
	
	public void setSubItemList(List<T> subItemList);
	
	public List<T> getSubItemList();
	
	public boolean isHasViewed();
	
	public void setHasViewed(boolean hasViewed);
	
	/**
	 * 做重置
	 */
	public void reset();
}
