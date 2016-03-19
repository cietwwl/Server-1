package com.rwbase.dao.item.pojo.readonly;

import java.util.Enumeration;

import com.playerdata.readonly.ItemDataIF;

/*
 * @author HC
 * @date 2015年8月31日 上午10:32:34
 * @Description 抽离背包的数据库映射接口
 */
public interface TableItemIF {
	/**
	 * 获取背包中物品的Keys
	 * 
	 * @return
	 */
	public Enumeration<String> getItemKeys();

	/**
	 * 获取某个物品的数据
	 * 
	 * @param slotId
	 * @return
	 */
	public ItemDataIF getItemData(String slotId);

	/**
	 * 获取背包的道具数量
	 * 
	 * @return
	 */
	public int getItemSize();
}