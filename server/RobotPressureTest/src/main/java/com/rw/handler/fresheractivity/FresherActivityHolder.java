package com.rw.handler.fresheractivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.activity.ActivityCountHolder;
import com.rw.handler.activity.ActivityCountTypeItem;
import com.rw.handler.activity.ActivityCountTypeSubItem;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class FresherActivityHolder {
	private  List<Integer> giftIdList = new ArrayList<Integer>();
	private SynDataListHolder<FresherActivityItem> listHolder = new SynDataListHolder<FresherActivityItem>(FresherActivityItem.class);
	private static FresherActivityHolder instance = new FresherActivityHolder();
	
	public static FresherActivityHolder getInstance(){
		return instance;
	}
	
	


	public List<Integer> getGiftIdList() {
		return giftIdList;
	}




	public void setGiftIdList(List<Integer> giftIdList) {
		this.giftIdList = giftIdList;
	}




	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<FresherActivityItem> itemList = listHolder.getItemList();
		for(FresherActivityItem subItem: itemList){
			if(!subItem.isGiftTaken()&&subItem.isFinish()&&subItem.getStartTime() < System.currentTimeMillis()){
				giftIdList.add(subItem.getCfgId());			
			}			
		}
	}
}
