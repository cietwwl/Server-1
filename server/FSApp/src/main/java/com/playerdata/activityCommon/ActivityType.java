package com.playerdata.activityCommon;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;

public class ActivityType<D extends CfgCsvDao<? extends ActivityCfgIF>, T extends RoleExtProperty> {
	private int typeId;
	private Class<D> activityDao;
	private Class<T> activityItem;
	private volatile long verStamp = 0; 

	public ActivityType(int typeId, Class<D> dao, Class<T> item) {
		this.typeId = typeId;
		this.activityDao = dao;
		this.activityItem = item;
	}

	public int getTypeId() {
		return typeId;
	}

	public Class<T> getActivityItem() {
		return activityItem;
	}

	public Class<D> getActivityDao() {
		return activityDao;
	}
	
	public void addVerStamp() {
		verStamp++;
	}

	public long getVerStamp() {
		return verStamp;
	}
}
