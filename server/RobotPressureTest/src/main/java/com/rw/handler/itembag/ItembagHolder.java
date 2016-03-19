package com.rw.handler.itembag;

import java.util.List;
import java.util.Random;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class ItembagHolder {
	

	private Random random = new Random();
	
	private SynDataListHolder<ItemData> listHolder = new SynDataListHolder<ItemData>(ItemData.class);
	
	public void syn(MsgDataSyn msgDataSyn){
		listHolder.Syn(msgDataSyn);
	}
	
	public ItemData getRandom(){
		List<ItemData> itemList = listHolder.getItemList();
		ItemData target = null;
		if(itemList.size()>0){
			target = itemList.get(random.nextInt(itemList.size()));
		}
		return target;
	}
	public ItemData getByModelId(int modelId){
		List<ItemData> itemList = listHolder.getItemList();
		ItemData target = null;
		for (ItemData itemData : itemList) {
			if(itemData.getModelId() == modelId){
				target = itemData;
			}
		}
		return target;
	}
	
	
}
