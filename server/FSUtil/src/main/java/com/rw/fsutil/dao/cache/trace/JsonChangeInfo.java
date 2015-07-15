package com.rw.fsutil.dao.cache.trace;

import java.util.Map;

/**
 * <pre>
 * 以Json格式记录变化的信息
 * </pre>
 * @author Jamaz
 *
 */
public interface JsonChangeInfo {

	/**
	 * 返回记录的唯一标识
	 * @return
	 */
	public String getKey();

	/**
	 * 返回记录变化的内容
	 * @return
	 */
	public Map<String, ChangedRecord> getChangedMap();

}
