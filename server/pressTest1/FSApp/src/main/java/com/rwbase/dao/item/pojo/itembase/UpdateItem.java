package com.rwbase.dao.item.pojo.itembase;

import com.rwbase.dao.item.pojo.ItemData;

/*
 * @author HC
 * @date 2015年8月6日 下午3:10:08
 * @Description 更新物品
 */
public class UpdateItem implements IUpdateItem {
	private final String slotId;// 物品的格子Id
	private final int count;// 更新的物品数量
	private final IItemCallback<ItemData> callback;// 回调

	public UpdateItem(String slotId, int count, IItemCallback<ItemData> callback) {
		this.slotId = slotId;
		this.count = count;
		this.callback = callback;
	}

	public String getSlotId() {
		return slotId;
	}

	public int getCount() {
		return count;
	}

	public IItemCallback<ItemData> getCallback() {
		return callback;
	}
}