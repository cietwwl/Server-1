package com.rw.fsutil.dao.cache.trace;

import java.util.HashMap;
import java.util.Map;

public interface DataChangedListener {

	public Map<String, String> FieldMap = new HashMap<String, String>();
	
	/**
	 * 监听数据变动
	 * @param set
	 */
	public void notifyDataChanged(ChangeInfoSet set);
	
	/**
	 * 检查字段合法
	 * @return
	 */
	public boolean checkFieldLegal();
}

