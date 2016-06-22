package com.rw.handler.fixExpEquip;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;



import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.fixEquip.FixNormEquipDataItem;
import com.rw.handler.fixEquip.FixNormEquipDataItemHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class FixExpEquipDataItemHolder{
	
private SynDataListHolder<FixExpEquipDataItem> listHolder = new SynDataListHolder<FixExpEquipDataItem>(FixExpEquipDataItem.class);
	
	private  List<String> equiplist = new ArrayList<String>();
	
	private static FixExpEquipDataItemHolder instance = new FixExpEquipDataItemHolder();
	private static int num ;
	public static FixExpEquipDataItemHolder getInstance(){
		return instance;
	}
	





	public List<String> getEquiplist() {
		return equiplist;
	}






	public void setEquiplist(List<String> equiplist) {
		this.equiplist = equiplist;
	}






	public void syn(MsgDataSyn msgDataSyn){
		listHolder.Syn(msgDataSyn);
		List<FixExpEquipDataItem> itemList = listHolder.getItemList();
		
		for(FixExpEquipDataItem item : itemList){
			String tmp = item.getOwnerId()+"_"+item.getCfgId();
			equiplist.add(tmp);
			num++;
		}
		num++;
	}
	
}
