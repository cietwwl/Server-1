package com.rw.dataaccess.attachment;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.attachment.InsertRoleExtPropertyData;

public class InsertRoleExtDataWrap<T extends RoleExtProperty> extends InsertRoleExtPropertyData {

	private long id;
	private final T extProperty;

	public InsertRoleExtDataWrap(T extProperty, String ownerId, short type, int subType, String extension) {
		super(ownerId, type, subType, extension);
		this.extProperty = extProperty;
	}

	public T getExtProperty() {
		return extProperty;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
