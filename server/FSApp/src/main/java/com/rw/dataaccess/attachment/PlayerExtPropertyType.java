package com.rw.dataaccess.attachment;

import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.rw.dataaccess.attachment.creator.ActivityCountTypeCreator;
import com.rw.dataaccess.attachment.property.ActivityCountTypeProperty;
import com.rw.fsutil.cacheDao.attachment.PlayerExtProperty;
import com.rw.fsutil.dao.cache.CacheKey;

public enum PlayerExtPropertyType {

//	TASK(1,TestProperty.class,TestPropertyCreator.class)
	ACTIVITY_COUNTTYPE(1,ActivityCountTypeItem.class,ActivityCountTypeCreator.class)//
	;

	private final Class<? extends PlayerExtProperty> propertyClass;
	private final Class<? extends PlayerExtPropertyCreator<?>> creatorClass;
	private final String propertyName;
	private final CacheKey cacheKey;
	private final short type;
	private final int capacity;

	<T extends PlayerExtProperty> PlayerExtPropertyType(int type, Class<T> attachmentClass, String name, Class<? extends PlayerExtPropertyCreator<T>> creatorClass, int capacity) {
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

	<T extends PlayerExtProperty> PlayerExtPropertyType(int type, Class<T> attachmentClass, Class<? extends PlayerExtPropertyCreator<T>> creatorClass, int capacity) {
		this(type, attachmentClass, attachmentClass.getSimpleName(), creatorClass, capacity);
	}

	<T extends PlayerExtProperty> PlayerExtPropertyType(int type, Class<T> attachmentClass, Class<? extends PlayerExtPropertyCreator<T>> creatorClass) {
		this(type, attachmentClass, attachmentClass.getSimpleName(), creatorClass, 0);
	}

	public Class<? extends PlayerExtProperty> getPropertyClass() {
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
