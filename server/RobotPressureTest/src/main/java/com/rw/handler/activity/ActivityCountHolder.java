package com.rw.handler.activity;

import java.util.HashMap;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.group.data.GroupBaseData;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class ActivityCountHolder {
	private static Map<String, String> giftlist = new HashMap<String, String>();
	private SynDataListHolder<ActivityCountTypeItem> listHolder = new SynDataListHolder<ActivityCountTypeItem>(ActivityCountTypeItem.class);
	
	
	
	public static Map<String, String> getGiftlist() {
		return giftlist;
	}

	public static void setGiftlist(Map<String, String> giftlist) {
		ActivityCountHolder.giftlist = giftlist;
	}

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		
	}
	
	
	
}
