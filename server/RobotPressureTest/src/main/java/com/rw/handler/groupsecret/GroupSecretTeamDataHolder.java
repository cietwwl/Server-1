package com.rw.handler.groupsecret;

import java.util.List;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GroupSecretTeamDataHolder {
	private SynDataListHolder<GroupSecretTeamData> listHolder = new SynDataListHolder<GroupSecretTeamData>(GroupSecretTeamData.class);
	
	private static GroupSecretTeamDataHolder instance = new GroupSecretTeamDataHolder();
	
	private GroupSecretTeamData data;
	
	public static GroupSecretTeamDataHolder getInstance(){
		return instance;
	}
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<GroupSecretTeamData> itemList = listHolder.getItemList();
		data = itemList.get(0);
	}

	public GroupSecretTeamData getData() {
		return data;
	}
}
