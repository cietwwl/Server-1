package com.rw.fsutil.cacheDao.attachment;

import com.rw.fsutil.cacheDao.mapItem.RowMapItem;

public class PlayerExtPropertyData<T extends PlayerExtProperty> implements RowMapItem<Integer> {

	private final Long id;
	private final T attachment;

	public PlayerExtPropertyData(Long id, T attachment) {
		super();
		this.id = id;
		this.attachment = attachment;
	}

	@Override
	public Integer getId() {
		return attachment.getId();
	}

	public Long getPrimaryKey() {
		return this.id;
	}

	public T getAttachment() {
		return attachment;
	}
}
