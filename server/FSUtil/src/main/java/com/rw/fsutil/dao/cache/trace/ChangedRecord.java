package com.rw.fsutil.dao.cache.trace;

import com.alibaba.fastjson.JSONObject;

/**
 * <pre>
 * 当属性是对象或者Map，会以{@link JSONObject}的形式记录变化的信息
 * 否则只存在{@link #newValue}的形式
 * </pre>
 * @author Jamaz
 *
 */
public class ChangedRecord {

	public final Object oldValue;
	public final Object newValue;
	public final JSONObject diff;

	public ChangedRecord(Object oldValue, Object newValue, JSONObject diff) {
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.diff = diff;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	public JSONObject getDiff() {
		return diff;
	}

}
