package com.bm.groupSecret.data.group;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GroupSecretDataHolder {

	private static GroupSecretDataHolder instance = new GroupSecretDataHolder();
	
	public static GroupSecretDataHolder getInstance(){
		return instance;
	}
	private static eSynType synType = eSynType.GroupSecretData;

	public void synList(Player player, List<String> secretIdList) {
		List<GroupSecretData> dataList = new ArrayList<GroupSecretData>();
		for (String idTmp : secretIdList) {
			GroupSecretData dataTmp = GroupSecretDataDAO.getInstance().get(idTmp);	
			if(dataTmp!=null){
				dataList.add(dataTmp);
			}
		}
		
		if(!dataList.isEmpty()){
			ClientDataSynMgr.synDataList(player, dataList, synType, eSynOpType.UPDATE_LIST);
		}
	}
	
	public void synSingle(Player player, String secretId){
		GroupSecretData groupSecretData = GroupSecretDataDAO.getInstance().get(secretId);		
		ClientDataSynMgr.synData(player, groupSecretData, synType, eSynOpType.UPDATE_SINGLE);
	}

	public GroupSecretData get(String secretId) {
		return GroupSecretDataDAO.getInstance().get(secretId);
	}

	public void update(String secretId) {
		GroupSecretDataDAO.getInstance().update(secretId);		
		
	}
	
	public boolean newData(GroupSecretData groupSecretData){
		return GroupSecretDataDAO.getInstance().update(groupSecretData);	
	}

}
