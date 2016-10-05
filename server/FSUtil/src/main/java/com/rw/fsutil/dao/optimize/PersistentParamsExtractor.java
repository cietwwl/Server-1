package com.rw.fsutil.dao.optimize;

import java.util.List;

public interface PersistentParamsExtractor<K2> {

	/**
	 * 提交和转换同步参数
	 * 
	 * @param key
	 * @param value
	 * @param updateList
	 * @return
	 */
	public boolean extractParams(K2 key, List<Object[]> updateList);

//	/**
//	 * 获取创建时间
//	 * @return
//	 */
//	public long getCreateTime();
}
