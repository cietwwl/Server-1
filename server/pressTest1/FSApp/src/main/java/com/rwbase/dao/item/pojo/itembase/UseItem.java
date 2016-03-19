package com.rwbase.dao.item.pojo.itembase;

/*
 * @author HC
 * @date 2015年8月7日 下午2:05:56
 * @Description 
 */
public class UseItem implements IUseItem {
	private final String slotId;// 物品的Id
	private final int useCount;// 物品要使用的个数

	public UseItem(String slotId, int useCount) {
		this.slotId = slotId;
		this.useCount = useCount;
	}

	@Override
	public String getSlotId() {
		return this.slotId;
	}

	@Override
	public int getUseCount() {
		return this.useCount;
	}
}