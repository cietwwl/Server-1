package com.rw.fsutil.cacheDao;

import java.util.concurrent.atomic.AtomicLong;

public class LocalMemoryIdGenerator implements SequenceGenerator {

	private final AtomicLong generaotr;

	public LocalMemoryIdGenerator(int initValue) {
		this.generaotr = new AtomicLong(initValue);
	}

	@Override
	public long generateId() {
		return generaotr.incrementAndGet();
	}

}
