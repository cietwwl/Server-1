package com.playerdata.activityCommon.activityType;

import com.playerdata.activityCommon.AbstractActivityMgr;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;

@SuppressWarnings("rawtypes")
public class ActivityType<D extends CfgCsvDao<? extends ActivityCfgIF>, T extends ActivityTypeItemIF> {
	private int typeId;
	private Class<D> activityDao;
	private Class<T> activityItem;
	private Class<? extends CfgCsvDao<? extends ActivitySubCfgIF>> activitySubDao;
	private Class<? extends ActivityTypeSubItemIF> activitySubItem;
	private AbstractActivityMgr<?> activityMgr;
	@Deprecated
	private IndexRankJudgeIF activityMgr2; 	//过渡类型，以后会去掉
	
	private volatile long verStamp = 0;

	public ActivityType(int typeId, Class<D> dao, Class<T> item, AbstractActivityMgr<?> mgr) {
		this.typeId = typeId;
		this.activityDao = dao;
		this.activityItem = item;
		this.activitySubDao = null;
		this.activitySubItem = null;
		this.activityMgr = mgr;
	}
	
	public ActivityType(int typeId, Class<D> dao, Class<T> item, Class<? extends CfgCsvDao<? extends ActivitySubCfgIF>> subDao, Class<? extends ActivityTypeSubItemIF> subItem, AbstractActivityMgr<?> mgr) {
		this.typeId = typeId;
		this.activityDao = dao;
		this.activityItem = item;
		this.activitySubDao = subDao;
		this.activitySubItem = subItem;
		this.activityMgr = mgr;
		this.activityMgr2 = mgr;
	}
	
	public ActivityType(int typeId, Class<D> dao, IndexRankJudgeIF mgr) {
		this.typeId = typeId;
		this.activityDao = dao;
		this.activityItem = null;
		this.activitySubDao = null;
		this.activitySubItem = null;
		this.activityMgr = null;
		this.activityMgr2 = mgr;
	}

	public int getTypeId() {
		return typeId;
	}

	public Class<T> getActivityItem() {
		return activityItem;
	}

	public CfgCsvDao<? extends ActivityCfgIF> getActivityDao() {
		return SpringContextUtil.getBean(activityDao);
	}
	
	public CfgCsvDao<? extends ActivitySubCfgIF> getSubActivityDao() {
		if(null == activitySubDao) return null;
		return SpringContextUtil.getBean(activitySubDao);
	}
	
	public void addVerStamp() {
		verStamp++;
	}

	public long getVerStamp() {
		return verStamp;
	}
	
	public T getNewActivityTypeItem(){
		try {
			return activityItem.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ActivityTypeSubItemIF getNewActivityTypeSubItem(){
		try {
			return activitySubItem.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public AbstractActivityMgr<?> getActivityMgr() {
		return activityMgr;
	}
	
	public IndexRankJudgeIF getActivityJudgeMgr() {
		return activityMgr2;
	}
	
	public boolean equals(ActivityType type){
		return this.typeId == type.typeId;
	}
}
