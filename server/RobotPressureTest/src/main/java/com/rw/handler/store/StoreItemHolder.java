package com.rw.handler.store;

import java.util.List;
import java.util.Random;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class StoreItemHolder {
	

	private Random random = new Random();
	
	private SynDataListHolder<StoreData> listHolder = new SynDataListHolder<StoreData>(StoreData.class);
	
	public void syn(MsgDataSyn msgDataSyn){
		listHolder.Syn(msgDataSyn);
	}
	
	public StoreData getStoreData(eStoreType type){
		StoreData target = null;
		List<StoreData> itemList = listHolder.getItemList();
		for (StoreData storeData : itemList) {
			if(storeData.getType() ==  type){
				target = storeData;
				break;
			}
		}
		return target;
	}
	public CommodityData getRandom(eStoreType type){
		StoreData targetData = getStoreData(type);
		
		List<CommodityData> targetList = targetData.getCommodity();
		int nextInt = random.nextInt(targetList.size());
		
		
		return targetList.get(nextInt);
	}
	
	
	
}
