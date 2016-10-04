package com.rw.dataaccess.attachment;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;

public interface RoleExtPropertyType {

	short getType();

	String getPropertyName();

	int getCapacity();

	Class<? extends RoleExtProperty> getPropertyClass();

	Class<? extends RoleExtPropertyCreator<?, ?>> getCreatorClass();

	int ordinal();
}
