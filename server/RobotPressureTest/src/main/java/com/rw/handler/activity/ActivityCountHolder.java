package com.rw.handler.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.equip.EquipItem;
import com.rw.handler.group.data.GroupBaseData;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class ActivityCountHolder {
	private  Map<String, String> giftlist = new HashMap<String, String>();
	private SynDataListHolder<ActivityCountTypeItem> listHolder = new SynDataListHolder<ActivityCountTypeItem>(ActivityCountTypeItem.class);
	private static ActivityCountHolder instance = new ActivityCountHolder();
	
	public static ActivityCountHolder getInstance(){
		return instance;
	}
	
	
	public  Map<String, String> getGiftlist() {
		return giftlist;
	}

	public  void setGiftlist(Map<String, String> giftlist) {
		this.giftlist = giftlist;
	}

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<ActivityCountTypeItem> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			ActivityCountTypeItem activitycounttypeitem = itemList.get(i);
			String cfgid = activitycounttypeitem.getCfgId();
			List<ActivityCountTypeSubItem> subitemlist = activitycounttypeitem.getSubItemList();
			for(ActivityCountTypeSubItem subitem:subitemlist){
				if(!subitem.isTaken()&&subitem.getCount()<=activitycounttypeitem.getCount()){
					giftlist.put(subitem.getCfgId(), cfgid);					
				}				
			}			
		}
//		System.out.println("@@@@@@activitycount.giftlength =" + giftlist.size());
	}
	
	
	
}
