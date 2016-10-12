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
	
	private  Map<Integer, GCompUserQuizItem> list = new HashMap<Integer, GCompUserQuizItem>();
	
	private SynDataListHolder<GCompUserQuizItem> listHolder = new SynDataListHolder<GCompUserQuizItem>(GCompUserQuizItem.class);
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<GCompUserQuizItem> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			GCompUserQuizItem ugfData = itemList.get(i);
			list.put(ugfData.getMatchId(), ugfData);
		}
	}
	
	public GCompUserQuizItem getUserQuizData(Integer matchId){
		return list.get(matchId);
	}
}
