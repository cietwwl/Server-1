package com.rw.dataaccess.attachment;

import com.rw.fsutil.cacheDao.attachment.PlayerExtProperty;
import com.rw.fsutil.dao.attachment.NewAttachmentEntry;

public class NewAttachmentInsertData<T extends PlayerExtProperty> extends NewAttachmentEntry {

	private long id;
	private final T extProperty;

	public NewAttachmentInsertData(T extProperty, String ownerId, short type, int subType, String extension) {
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
