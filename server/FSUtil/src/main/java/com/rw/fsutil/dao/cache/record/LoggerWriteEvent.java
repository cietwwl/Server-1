package com.rw.fsutil.dao.cache.record;

import com.rw.fsutil.dao.cache.trace.CharArrayBuffer;

public interface LoggerWriteEvent {
	
	public void write(CharArrayBuffer sb);
}
