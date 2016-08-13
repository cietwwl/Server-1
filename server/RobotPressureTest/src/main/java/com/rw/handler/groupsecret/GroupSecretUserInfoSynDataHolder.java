package com.rw.handler.groupsecret;

import java.util.List;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class GroupSecretUserInfoSynDataHolder {
	private SynDataListHolder<SecretUserInfoSynData> listHolder = new SynDataListHolder<SecretUserInfoSynData>(SecretUserInfoSynData.class);
	
	private static GroupSecretUserInfoSynDataHolder instance = new GroupSecretUserInfoSynDataHolder();
	
	List<SecretUserInfoSynData> defanceList;
	
	public static GroupSecretUserInfoSynDataHolder getInstance(){
		return instance;		
	}
	
	
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		defanceList = listHolder.getItemList();
		
	}
	
	public SecretUserInfoSynData getUserInfoSynData(){
		if(defanceList == null||defanceList.isEmpty()){
			return null;
		}
		return defanceList.get(0);
	}
	
	
} 
