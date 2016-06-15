package com.rw.handler.groupsecret;

import java.util.List;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GroupSecretBaseInfoSynDataHolder {

	private SynDataListHolder<SecretBaseInfoSynData> listHolder = new SynDataListHolder<SecretBaseInfoSynData>(SecretBaseInfoSynData.class);
	private static GroupSecretBaseInfoSynDataHolder instance = new GroupSecretBaseInfoSynDataHolder();

	List<SecretBaseInfoSynData> defanceList;
	
	public static GroupSecretBaseInfoSynDataHolder getInstance() {
		return instance;
	}

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		defanceList = listHolder.getItemList();
		
	}

	public String getDefendSecretId() {
		if(defanceList !=null && defanceList.size()> 0){
			SecretBaseInfoSynData secretBaseInfoSynData = defanceList.get(0);
			return  secretBaseInfoSynData.getId();
		}else{
			return null;
		}
	}
}
