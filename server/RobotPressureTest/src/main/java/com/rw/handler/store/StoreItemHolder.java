package com.rw.handler.store;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class StoreItemHolder {

	private Random random = new Random();

	private SynDataListHolder<StoreData> listHolder = new SynDataListHolder<StoreData>(StoreData.class);

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
	}

	public StoreData getStoreData(eStoreType type) {
		StoreData target = null;
		List<StoreData> itemList = listHolder.getItemList();
		for (StoreData storeData : itemList) {
			if (storeData.getType() == type) {
				target = storeData;
				break;
			}
		}
		return target;
	}

	public CommodityData getRandom(eStoreType type) {
		StoreData targetData = getStoreData(type);
		if (targetData == null) {
			return null;
		}

		List<CommodityData> targetList = targetData.getCommodity();
		if (targetList.isEmpty()) {
			return null;
		}

		Iterator<CommodityData> iterator = targetList.iterator();
		while (iterator.hasNext()) {
			CommodityData next = iterator.next();
			if (next == null || next.getCount() <= 0) {
				iterator.remove();
			}
		}

		if (targetList.isEmpty()) {
			return null;
		}

		int nextInt = random.nextInt(targetList.size());

		return targetList.remove(nextInt);
	}
}