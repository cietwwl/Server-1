package com.rw.handler.groupCompetition.data.baseinfo;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GCompBaseInfoHolder {

	private static final GCompBaseInfoHolder _INSTANCE = new GCompBaseInfoHolder();
	
	public static GCompBaseInfoHolder getInstance() {
		return _INSTANCE;
	}
	
	private SynDataListHolder<GCompBaseInfo> listHolder = new SynDataListHolder<GCompBaseInfo>(GCompBaseInfo.class);
	private GCompBaseInfo baseInfoData;
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		baseInfoData = listHolder.getItemList().get(0);
	}
	
	public GCompBaseInfo getGCompBaseInfo() {
		return baseInfoData;
	}
}
