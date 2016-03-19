package com.rwbase.dao.item.pojo.itembase;

import com.rwbase.dao.item.pojo.ItemData;

/*
 * @author HC
 * @date 2015年8月6日 下午3:06:20
 * @Description 创建一个新的物品的简单模型
 */
public class NewItem implements INewItem {
	private final int cfgId;// 新创建物品的模版Id
	private final int count;// 要创建物品的数量
	private final IItemCallback<ItemData> callback;// 回调

	public NewItem(int cfgId, int count, IItemCallback<ItemData> callback) {
		this.cfgId = cfgId;
		this.count = count;
		this.callback = callback;
	}

	public int getCfgId() {
		return cfgId;
	}

	public int getCount() {
		return count;
	}

	public IItemCallback<ItemData> getCallback() {
		return callback;
	}
}