package com.rw.handler.groupFight.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GFightOnlineGroupHolder {
	
	private static GFightOnlineGroupHolder instance = new GFightOnlineGroupHolder();
	
	public static GFightOnlineGroupHolder getInstance() {
		return instance;
	}

	private Map<String, GFightOnlineGroupData> list = new HashMap<String, GFightOnlineGroupData>();
	private List<String> rankIDList = new ArrayList<String>();
	
	private SynDataListHolder<GFightOnlineGroupData> listHolder = new SynDataListHolder<GFightOnlineGroupData>(GFightOnlineGroupData.class);
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<GFightOnlineGroupData> itemList = listHolder.getItemList();
		if(itemList.size() > 0) rankIDList.clear();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			GFightOnlineGroupData gfGroupData = itemList.get(i);
			list.put(gfGroupData.getGroupID(), gfGroupData);
			if(rankIDList.size() < 4) rankIDList.add(gfGroupData.getGroupID());
		}
	}
	
	public GFightOnlineGroupData getUserGFData(String groupID){
		return list.get(groupID);
	}
	
	public List<String> getRankIDList(){
		return rankIDList;
	}
}
