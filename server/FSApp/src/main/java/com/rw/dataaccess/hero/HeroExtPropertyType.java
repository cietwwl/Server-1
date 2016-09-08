package com.rw.dataaccess.hero;

import com.rw.dataaccess.attachment.RoleExtPropertyType;
import com.rw.fsutil.cacheDao.attachment.PlayerExtProperty;
import com.rw.fsutil.dao.cache.CacheKey;

public enum HeroExtPropertyType implements RoleExtPropertyType{

	;
	private final Class<? extends PlayerExtProperty> propertyClass;
	private final Class<? extends HeroExtPropertyCreator<?>> creatorClass;
	private final String propertyName;
	private final CacheKey cacheKey;
	private final short type;
	private final int capacity;

	<T extends PlayerExtProperty> HeroExtPropertyType(int type, Class<T> attachmentClass, Class<? extends HeroExtPropertyCreator<T>> creatorClass) {
		this(type, attachmentClass, attachmentClass.getSimpleName(), creatorClass, 0);
	}
	
	<T extends PlayerExtProperty> HeroExtPropertyType(int type, Class<T> attachmentClass, String name, Class<? extends HeroExtPropertyCreator<T>> creatorClass, int capacity) {
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

	public Class<? extends PlayerExtProperty> getPropertyClass() {
		return propertyClass;
	}

	public Class<? extends HeroExtPropertyCreator<?>> getCreatorClass() {
		return creatorClass;
	}

	public int getCapacity() {
		return capacity;
	}

	public short getType() {
		return type;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public CacheKey getCacheKey() {
		return cacheKey;
	}
}
