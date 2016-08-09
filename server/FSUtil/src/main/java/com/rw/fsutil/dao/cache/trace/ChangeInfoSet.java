package com.rw.fsutil.dao.cache.trace;

import java.util.List;

public interface ChangeInfoSet {

	/**
	 * 获取新增信息
	 * @return
	 */
	public List<JsonInfo> getAddInfos();
	
	/**
	 * 获取删除信息
	 * @return
	 */
	public List<JsonInfo> getDeleteInfos();
	
	/**
	 * 获取更新信息
	 * @return
	 */
	public List<JsonChangeInfo> getUpdateInfos();
	
	/**
	 * 获取主键
	 * @return
	 */
	public Object getKey();
	
}
