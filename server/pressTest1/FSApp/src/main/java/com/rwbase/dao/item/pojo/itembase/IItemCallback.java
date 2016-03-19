package com.rwbase.dao.item.pojo.itembase;

/*
 * @author HC
 * @date 2015年8月6日 下午3:03:48
 * @Description 物品处理的回调，以后可能会加入多线程的处理，便于统一管理
 */
public interface IItemCallback<T> {
	/**
	 * 回调方法，可以保证在物品任意操作成功之后，才来后续通知，或者做相应的处理
	 * 
	 * @param t
	 */
	public void call(T t);
}