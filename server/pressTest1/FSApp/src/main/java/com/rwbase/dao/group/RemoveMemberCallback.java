package com.rwbase.dao.group;

/*
 * @author HC
 * @date 2016年1月25日 下午3:52:27
 * @Description 
 */
public interface RemoveMemberCallback<T> {

	/**
	 * 移除成员的回调
	 * 
	 * @param t
	 */
	public void call(T t);
}