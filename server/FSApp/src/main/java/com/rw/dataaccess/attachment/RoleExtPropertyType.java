package com.rw.dataaccess.attachment;

import com.rw.fsutil.cacheDao.attachment.PlayerExtProperty;

public interface RoleExtPropertyType {

	short getType();

	String getPropertyName();

	int getCapacity();

	Class<? extends PlayerExtProperty> getPropertyClass();

	Class<? extends RoleExtPropertyCreator<?, ?>> getCreatorClass();

	int ordinal();
}
