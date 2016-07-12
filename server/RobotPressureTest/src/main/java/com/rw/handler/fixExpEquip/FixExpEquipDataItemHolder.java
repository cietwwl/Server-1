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
	

	private  Map<Integer, String> equipMap = new HashMap<Integer, String>();
	
	private static FixExpEquipDataItemHolder instance = new FixExpEquipDataItemHolder();
	private static int num ;
	public static FixExpEquipDataItemHolder getInstance(){
		return instance;
	}	

	public Map<Integer, String> getEquipMap() {
		return equipMap;
	}

	public void setEquipMap(Map<Integer, String> equipMap) {
		this.equipMap = equipMap;
	}

	public void syn(MsgDataSyn msgDataSyn){
		listHolder.Syn(msgDataSyn);
		List<FixExpEquipDataItem> itemList = listHolder.getItemList();
		
		for(FixExpEquipDataItem item : itemList){
			String tmp = item.getOwnerId()+"_"+item.getCfgId();
			equipMap.put(num, tmp);
			
			if(num%2==1){
				check();
			}			
			num++;
		}
	}

	private void check() {
		String tmp0 = equipMap.get(num-1);
		String tmp1 = equipMap.get(num);
		int itemId0 = Integer.parseInt(tmp0.split("_")[1]);
		int itemId1 = Integer.parseInt(tmp1.split("_")[1]);
		if(itemId0 > itemId1){
			//倒着发过来
			equipMap.remove(num);
			equipMap.remove(num-1);
			equipMap.put(num-1, tmp1);
			equipMap.put(num, tmp0);
		}		
	}
	
}
