package com.rw.fsutil.dao.cache.record;

public interface RecordEvent<T> extends LoggerWriteEvent{

	public CacheRecordEvent parse(T o);

}
