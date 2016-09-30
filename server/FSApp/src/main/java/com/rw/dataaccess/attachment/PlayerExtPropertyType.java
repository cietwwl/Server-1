package com.rw.dataaccess.attachment;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
import com.rw.dataaccess.attachment.creator.ActivityCountTypeCreator;
import com.rw.dataaccess.attachment.creator.ActivityFortuneCatCreator;
import com.rw.dataaccess.attachment.creator.ActivityLimitHeroCreator;
import com.rw.fsutil.cacheDao.attachment.PlayerExtProperty;
import com.rw.fsutil.dao.cache.CacheKey;

public enum PlayerExtPropertyType implements RoleExtPropertyType{

	ACTIVITY_COUNTTYPE(1,ActivityCountTypeItem.class,ActivityCountTypeCreator.class),
	ACTIVITY_LIMITHERO(13,ActivityLimitHeroTypeItem.class,ActivityLimitHeroCreator.class),
	ACTIVITY_FORTUNECAT(12,ActivityFortuneCatTypeItem.class,ActivityFortuneCatCreator.class)
	;

	private final Class<? extends RoleExtProperty> propertyClass;
	private final Class<? extends PlayerExtPropertyCreator<?>> creatorClass;
	private final String propertyName;
	private final CacheKey cacheKey;
	private final short type;
	private final int capacity;

	<T extends RoleExtProperty> PlayerExtPropertyType(int type, Class<T> attachmentClass, String name, Class<? extends PlayerExtPropertyCreator<T>> creatorClass, int capacity) {
		if (type > Short.MAX_VALUE) {
			throw new ExceptionInInitializerError("out of range:" + type + ",max=" + Short.MAX_VALUE);
		}
		this.type = (short) type;
		this.propertyClass = attachmentClass;
		this.propertyName = name;
		this.creatorClass = creatorClass;
		this.capacity = capacity;
		this.cacheKey = new CacheKey(attachmentClass, name);
	}

	<T extends RoleExtProperty> PlayerExtPropertyType(int type, Class<T> attachmentClass, Class<? extends PlayerExtPropertyCreator<T>> creatorClass, int capacity) {
		this(type, attachmentClass, attachmentClass.getSimpleName(), creatorClass, capacity);
	}

	<T extends RoleExtProperty> PlayerExtPropertyType(int type, Class<T> attachmentClass, Class<? extends PlayerExtPropertyCreator<T>> creatorClass) {
		this(type, attachmentClass, attachmentClass.getSimpleName(), creatorClass, 0);
	}

	public Class<? extends RoleExtProperty> getPropertyClass() {
		return propertyClass;
	}

	public CacheKey getCacheKey() {
		return cacheKey;
	}

	public Class<? extends PlayerExtPropertyCreator<?>> getCreatorClass() {
		return creatorClass;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public short getType() {
		return type;
	}

	public int getCapacity() {
		return capacity;
	}
	
}
