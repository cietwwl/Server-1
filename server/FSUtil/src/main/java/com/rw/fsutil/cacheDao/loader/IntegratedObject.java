package com.rw.fsutil.cacheDao.loader;

public class IntegratedObject<T> {

	private final long id;
	private final long userId;
	private final int type;
	private final T value;

	public IntegratedObject(long id, long userId, int type, T value) {
		this.id = id;
		this.userId = userId;
		this.type = type;
		this.value = value;
	}

	public long getId() {
		return id;
	}

	public long getUserId() {
		return userId;
	}

	public int getType() {
		return type;
	}

	public T getValue() {
		return value;
	}

}
