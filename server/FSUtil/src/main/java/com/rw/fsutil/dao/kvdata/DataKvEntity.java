package com.rw.fsutil.dao.kvdata;

public interface DataKvEntity {


	/**
	 * 获取角色唯一ID
	 * @return
	 */
	public String getUserId();

	/**
	 * 获取dbValue
	 * @return
	 */
	public String getValue();

	/**
	 * 获取保存类型
	 * @return
	 */
	public Integer getType() ;

}
