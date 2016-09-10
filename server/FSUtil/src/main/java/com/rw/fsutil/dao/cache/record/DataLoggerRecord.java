package com.rw.fsutil.dao.cache.record;

import com.rw.fsutil.dao.cache.trace.CharArrayBuffer;

public interface DataLoggerRecord {

	/**
	 * 写入字符缓冲区
	 * @param sb
	 */
	public void write(CharArrayBuffer sb);

	/**
	 * 获取记录主键
	 * @return
	 */
	public Object getKey();
}
