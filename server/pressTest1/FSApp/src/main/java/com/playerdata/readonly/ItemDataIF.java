package com.playerdata.readonly;

import java.util.Enumeration;

import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rwproto.ItemBagProtos.EItemTypeDef;

/*
 * @author HC
 * @date 2015年8月31日 上午9:56:42
 * @Description 物品Data抽离接口
 */
public interface ItemDataIF extends IMapItem {
	/**
	 * 获取物品的资源Id
	 * 
	 * @return
	 */
	public int getModelId();

	/**
	 * 获取物品的数量
	 * 
	 * @return
	 */
	public int getCount();

	/**
	 * 获取物品的类型
	 * 
	 * @return
	 */
	public EItemTypeDef getType();

	/**
	 * 获取物品的格子Id
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * 获取某个类型属性的数值
	 * 
	 * @param itemAttrId
	 * @return
	 */
	public String getExtendAttr(int itemAttrId);

	/**
	 * 获取物品类型Map中的Keys
	 * 
	 * @return
	 */
	public Enumeration<Integer> getEnumerationKeys();

	/**
	 * 获取法宝等级
	 * 
	 * @return
	 */
	public int getMagicLevel();
}