package com.rw.handler.magicSecret;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.eSynType;

public class MagicChapterInfoHolder{
	
	private static class InstanceHolder{
		private static MagicChapterInfoHolder instance = new MagicChapterInfoHolder();
	}
	
	public static MagicChapterInfoHolder getInstance(){
		return InstanceHolder.instance;
	}
	
	
	
	private  Map<String, MagicChapterInfo> list = new HashMap<String, MagicChapterInfo>();
	
	private SynDataListHolder<MagicChapterInfo> listHolder = new SynDataListHolder<MagicChapterInfo>(MagicChapterInfo.class);
	
	public MagicChapterInfoHolder() { }
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<MagicChapterInfo> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			MagicChapterInfo magicChapterInfo = itemList.get(i);
			list.put(magicChapterInfo.getUserId(), magicChapterInfo);
		}		
	}

	public Map<String, MagicChapterInfo> getList() {
		return list;
	}

	public void setList(Map<String, MagicChapterInfo> list) {
		this.list = list;
	}	
	
	
	
}
