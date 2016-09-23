package com.rw.handler.groupCompetition.data.prepare;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class SameSceneSynDataHolder {
	
	private static SameSceneSynDataHolder instance = new SameSceneSynDataHolder();
	
	public static SameSceneSynDataHolder getInstance() {
		return instance;
	}
	
	private  Map<String, SameSceneSynData> list = new HashMap<String, SameSceneSynData>();
	
	private SynDataListHolder<SameSceneSynData> listHolder = new SynDataListHolder<SameSceneSynData>(SameSceneSynData.class);
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<SameSceneSynData> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			SameSceneSynData ugfData = itemList.get(i);
			list.put(ugfData.getId(), ugfData);
		}
	}
	
	public SameSceneSynData getUserGFData(String userID){
		return list.get(userID);
	}
}
