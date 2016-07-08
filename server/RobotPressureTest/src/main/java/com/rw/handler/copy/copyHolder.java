package com.rw.handler.copy;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.eSynType;

public class copyHolder{
	
	private static class InstanceHolder{
		private static copyHolder instance = new copyHolder();
	}
	
	public static copyHolder getInstance(){
		return InstanceHolder.instance;
	}
	

	
//	private SynDataListHolder<MagicChapterInfo> listHolder = new SynDataListHolder<MagicChapterInfo>(MagicChapterInfo.class);
	
	public copyHolder() { }
	
	public void syn(MsgDataSyn msgDataSyn) {
		int i = 0;
		i++;
		System.out.println(i);
//		listHolder.Syn(msgDataSyn);
//		// 更新数据
//		List<MagicChapterInfo> itemList = listHolder.getItemList();
//		for (int i = 0, size = itemList.size(); i < size; i++) {
//			MagicChapterInfo magicChapterInfo = itemList.get(i);
//			list.put(magicChapterInfo.getChapterId(), magicChapterInfo);
//		}		
	}


	
	
	
}
