package com.rw.handler.groupFight.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class UserGFightOnlineHolder {
	private static UserGFightOnlineHolder instance = new UserGFightOnlineHolder();
	
	public static UserGFightOnlineHolder getInstance() {
		return instance;
	}
	
	private  Map<String, UserGFightOnlineData> list = new HashMap<String, UserGFightOnlineData>();
	
	private SynDataListHolder<UserGFightOnlineData> listHolder = new SynDataListHolder<UserGFightOnlineData>(UserGFightOnlineData.class);
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<UserGFightOnlineData> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			UserGFightOnlineData ugfData = itemList.get(i);
			list.put(ugfData.getId(), ugfData);
		}
	}
	
	public UserGFightOnlineData getUserGFData(String userID){
		return list.get(userID);
	}
}
