package com.rwbase.dao.item.pojo.itembase;

import com.rwbase.dao.item.pojo.ItemData;

/*
 * @author HC
 * @date 2015年8月6日 下午3:40:30
 * @Description 
 */
public interface INewItem {

	public int getCfgId();

	public int getCount();

	public IItemCallback<ItemData> getCallback();
}