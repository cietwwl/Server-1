package com.rw.fsutil.cacheDao.attachment;

import java.util.Enumeration;

public interface RoleExtPropertyStore<T extends RoleExtProperty> extends IRowMapItemContainer<Integer, T> {

	/**
	 * 获取指定配置id的{@link RoleExtProperty}
	 * 
	 * @param cfgId
	 * @return
	 */
	public T get(Integer cfgId);

	/**
	 * 获取所有{@link RoleExtProperty}的枚举迭代器
	 * @return
	 */
	public Enumeration<T> getExtPropertyEnumeration();

	/**
	 * 获取附加对象
	 * @return
	 */
	public Object getAttachment();

	/**
	 * 设置附加对象
	 * @param attachment
	 */
	public void setAttachment(Object attachment);
}
