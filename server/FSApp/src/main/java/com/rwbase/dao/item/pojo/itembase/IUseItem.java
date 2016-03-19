package com.rwbase.dao.item.pojo.itembase;

/*
 * @author HC
 * @date 2015年8月7日 下午2:05:50
 * @Description 使用道具
 */
public interface IUseItem {
	/**
	 * 获取使用物品的Id
	 * 
	 * @return
	 */
	public String getSlotId();

	/**
	 * 获取物品使用的数量
	 * 
	 * @return
	 */
	public int getUseCount();
}