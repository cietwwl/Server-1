package com.rw.handler.itembag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class ItembagHolder {

	private Random random = new Random();

	private Map<String, ItemData> itemDataMap = new HashMap<String, ItemData>();

	private SynDataListHolder<ItemData> listHolder = new SynDataListHolder<ItemData>(ItemData.class);

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);

		List<ItemData> itemList = listHolder.getItemList();
		if (itemList == null || itemList.isEmpty()) {
			return;
		}

		for (ItemData itemData : itemList) {
			String id = itemData.getId();
			ItemData item = itemDataMap.get(id);
			if (item == null) {
				itemDataMap.put(id, itemData);
			} else if (itemData.getCount() <= 0) {
				itemDataMap.remove(id);
			} else {
				itemDataMap.put(id, itemData);
			}
		}
	}

	public ItemData getRandom() {
		List<ItemData> itemList = new ArrayList<ItemData>(itemDataMap.values());
		ItemData target = null;
		if (itemList.size() > 0) {
			target = itemList.get(random.nextInt(itemList.size()));
		}
		return target;
	}

	public ItemData getByModelId(int modelId) {
		List<ItemData> itemList = new ArrayList<ItemData>(itemDataMap.values());
		ItemData target = null;
		for (ItemData itemData : itemList) {
			if (itemData.getModelId() == modelId) {
				target = itemData;
				break;
			}
		}
		return target;
	}

	public int getItemCountByModelId(int modelId) {
		int count = 0;
		List<ItemData> itemList = new ArrayList<ItemData>(itemDataMap.values());
		for (ItemData itemData : itemList) {
			if (itemData.getModelId() == modelId) {
				count += itemData.getCount();
			}
		}
		return count;
	}

	public List<ItemData> getItemDataByModelId(int modelId) {
		List<ItemData> data = new ArrayList<ItemData>();
		List<ItemData> itemList = new ArrayList<ItemData>(itemDataMap.values());
		for (ItemData itemData : itemList) {
			if (itemData.getModelId() == modelId) {
				data.add(itemData);
			}
		}
		return data;
	}
}