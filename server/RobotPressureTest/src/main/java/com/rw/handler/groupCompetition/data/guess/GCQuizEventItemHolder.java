package com.rw.handler.groupCompetition.data.guess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GCQuizEventItemHolder {
	private static GCQuizEventItemHolder instance = new GCQuizEventItemHolder();
	
	public static GCQuizEventItemHolder getInstance() {
		return instance;
	}
	
	private  Map<String, GCQuizEventItem> list = new HashMap<String, GCQuizEventItem>();
	
	private SynDataListHolder<GCQuizEventItem> listHolder = new SynDataListHolder<GCQuizEventItem>(GCQuizEventItem.class);
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<GCQuizEventItem> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			GCQuizEventItem ugfData = itemList.get(i);
			list.put(ugfData.getId(), ugfData);
		}
	}
	
	public GCQuizEventItem getUserGFData(String userID){
		return list.get(userID);
	}
}
