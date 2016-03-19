package com.playerdata.readonly;

import java.util.List;

import com.rwproto.ItemBagProtos.EItemTypeDef;

/*
 * @author HC
 * @date 2015年8月31日 上午9:49:58
 * @Description 背包模块抽理接口
 */
public interface ItemBagMgrIF {
	/**
	 * 通过物品的唯一格子Id获取物品数据
	 * 
	 * @param nSlotId 物品的格子Id
	 * @return 返回物品数据
	 */
	public ItemDataIF findBySlotId(String nSlotId);

	/**
	 * 根据物品的资源Id获取在背包中的物品数量
	 * 
	 * @param nItemId 物品的资源Id
	 * @return 通过资源Id获取某个物品的总数量
	 */
	public int getItemCountByModelId(int nItemId);

	/**
	 * 通过物品的类型获取背包中的数据
	 * 
	 * @param itemType
	 * @return
	 */
	public List<? extends ItemDataIF> getItemListByType(EItemTypeDef itemType);
}