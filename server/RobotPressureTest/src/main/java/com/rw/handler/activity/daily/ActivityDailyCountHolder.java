package com.rw.handler.activity.daily;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class ActivityDailyCountHolder{
	
	private static ActivityDailyCountHolder instance = new ActivityDailyCountHolder();
	
	public static ActivityDailyCountHolder getInstance(){
		return instance;
	}
	
	private  Map<String, String> giftlist = new HashMap<String, String>();
	
	private SynDataListHolder<ActivityDailyCountTypeItem> listHolder = new SynDataListHolder<ActivityDailyCountTypeItem>(ActivityDailyCountTypeItem.class);
	
	public Map<String, String> getGiftlist() {
		return giftlist;
	}
	

	public void setGiftlist(Map<String, String> giftlist) {
		this.giftlist = giftlist;
	}

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<ActivityDailyCountTypeItem> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			ActivityDailyCountTypeItem activitycounttypeitem = itemList.get(i);
			String cfgid = activitycounttypeitem.getCfgid();
			List<ActivityDailyCountTypeSubItem> subitemlist = activitycounttypeitem
					.getSubItemList();
			for (ActivityDailyCountTypeSubItem subitem : subitemlist) {
				if (!subitem.isTaken()) {
					giftlist.put(subitem.getCfgId(), cfgid);
				}
			}
		}
		
	}

}
