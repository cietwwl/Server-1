package com.rwbase.dao.item.pojo.itembase;

import com.rwbase.dao.item.pojo.ItemData;

/*
 * @author HC
 * @date 2015年8月6日 下午3:40:43
 * @Description 
 */
public interface IUpdateItem {
	/**
	 * 获取更新物品的格子Id
	 * 
	 * @return
	 */
	public String getSlotId();

	/**
	 * 获取物品更新的数量
	 * 
	 * @return
	 */
	public int getCount();

	/**
	 * 获取物品的回调，可以为
	 * 
	 * <pre>
	 * <b>Null</b>
	 * </pre>
	 * 
	 * @return
	 */
	public IItemCallback<ItemData> getCallback();
}