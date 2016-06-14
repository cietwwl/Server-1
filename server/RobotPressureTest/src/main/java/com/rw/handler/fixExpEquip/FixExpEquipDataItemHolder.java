package com.rw.handler.fixExpEquip;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;



import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.fixEquip.FixNormEquipDataItem;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class FixExpEquipDataItemHolder{
	
	final private eSynType synType = eSynType.FIX_EXP_EQUIP;
	private SynDataListHolder<FixExpEquipDataItem> listHolder = new SynDataListHolder<FixExpEquipDataItem>(FixExpEquipDataItem.class);
	
	public List<FixExpEquipDataItem> getItemList(String ownerId)	
	{
		
		List<FixExpEquipDataItem> itemList = new ArrayList<FixExpEquipDataItem>();
//		Enumeration<FixExpEquipDataItem> mapEnum = getItemStore(ownerId).getEnum();
//		while (mapEnum.hasMoreElements()) {
//			FixExpEquipDataItem item = (FixExpEquipDataItem) mapEnum.nextElement();
//			itemList.add(item);
//		}
		
		return itemList;
	}
	

	
	
	public void syn(MsgDataSyn msgDataSyn){
		listHolder.Syn(msgDataSyn);
		List<FixExpEquipDataItem> itemList = listHolder.getItemList();
	}
	

//	private List<Action> callbackList = new ArrayList<Action>();


	
}
