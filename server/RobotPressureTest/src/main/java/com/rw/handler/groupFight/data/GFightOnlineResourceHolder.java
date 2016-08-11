package com.rw.handler.groupFight.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GFightOnlineResourceHolder {
	
	private static GFightOnlineResourceHolder instance = new GFightOnlineResourceHolder();
	

	public static GFightOnlineResourceHolder getInstance() {
		return instance;
	}
	
	private  Map<Integer, GFightOnlineResourceData> list = new HashMap<Integer, GFightOnlineResourceData>();
	
	private SynDataListHolder<GFightOnlineResourceData> listHolder = new SynDataListHolder<GFightOnlineResourceData>(GFightOnlineResourceData.class);
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<GFightOnlineResourceData> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			GFightOnlineResourceData gfResData = itemList.get(i);
			list.put(gfResData.getResourceID(), gfResData);
		}
	}
	
	public GFightOnlineResourceData getUserGFData(int resID){
		return list.get(resID);
	}
}
