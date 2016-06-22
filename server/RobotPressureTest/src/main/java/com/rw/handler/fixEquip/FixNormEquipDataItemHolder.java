package com.rw.handler.fixEquip;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;



import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.activity.ActivityCountHolder;
import com.rw.handler.activity.daily.ActivityDailyCountHolder;
import com.rw.handler.activity.daily.ActivityDailyCountTypeItem;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class FixNormEquipDataItemHolder{
	
	
	private SynDataListHolder<FixNormEquipDataItem> listHolder = new SynDataListHolder<FixNormEquipDataItem>(FixNormEquipDataItem.class);
	
	private  Map<Integer, String> equiplist = new HashMap<Integer, String>();
	
	private static FixNormEquipDataItemHolder instance = new FixNormEquipDataItemHolder();
	
	private static int num ;
	public static FixNormEquipDataItemHolder getInstance(){
		return instance;
	}
	
	
	
	

	
	

	
	
	public Map<Integer, String> getEquiplist() {
		return equiplist;
	}





	public void syn(MsgDataSyn msgDataSyn){
		listHolder.Syn(msgDataSyn);
		List<FixNormEquipDataItem> itemList = listHolder.getItemList();
		
		for(FixNormEquipDataItem item : itemList){
			String tmp = item.getOwnerId()+"_"+item.getCfgId();
			equiplist.put(num, tmp);
			num++;
		}
	}
		
}
