package com.rw.fsutil.dao.cache.record;

import com.rw.fsutil.dao.cache.trace.ChangeInfoSet;
import com.rw.fsutil.dao.cache.trace.CharArrayBuffer;

public interface CacheRecordEvent extends ChangeInfoSet,LoggerWriteEvent{

	public void write(CharArrayBuffer sb);
	
}
