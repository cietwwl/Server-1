package com.rw.handler.groupCompetition.data.guess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GCompUserQuizItemHolder {
	private static GCompUserQuizItemHolder instance = new GCompUserQuizItemHolder();
	
	public static GCompUserQuizItemHolder getInstance() {
		return instance;
	}
	
	private  Map<String, GCompUserQuizItem> list = new HashMap<String, GCompUserQuizItem>();
	
	private SynDataListHolder<GCompUserQuizItem> listHolder = new SynDataListHolder<GCompUserQuizItem>(GCompUserQuizItem.class);
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<GCompUserQuizItem> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			GCompUserQuizItem ugfData = itemList.get(i);
			list.put(ugfData.getId(), ugfData);
		}
	}
	
	public GCompUserQuizItem getUserGFData(String userID){
		return list.get(userID);
	}
}
