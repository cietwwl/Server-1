package com.rwbase.dao.guildSecretArea;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class SecretAreaDefHolder{//掠夺记录信息
	
	
	private String userId;
	private MapItemStore<SecretAreaDefRecord> recordStore;
	
	final private eSynType synType = eSynType.SECRETAREA_DEF_RECORD;
	
	public SecretAreaDefHolder(String roleIdP) {
		userId = roleIdP;
		recordStore = new MapItemStore<SecretAreaDefRecord>("userId", userId, SecretAreaDefRecord.class);
	}

	public void synData(Player player){
		ClientDataSynMgr.synDataList(player, getRecordList(), synType, eSynOpType.UPDATE_LIST);
	}
	

	public List<SecretAreaDefRecord> getRecordList()	
	{
		
		List<SecretAreaDefRecord> recordList = new ArrayList<SecretAreaDefRecord>();
		Enumeration<SecretAreaDefRecord> mapEnum = recordStore.getEnum();
		while (mapEnum.hasMoreElements()) {
			SecretAreaDefRecord record = (SecretAreaDefRecord) mapEnum.nextElement();
			recordList.add(record);
		}
		
		return recordList;
	}
	public void removeAllReord(Player player,List<String> recordIdList)
	{
		if(recordIdList==null||recordIdList.size()==0){
			return;
		}
		boolean isSuccess=false;
		for(String recordId:recordIdList){
			isSuccess=recordStore.removeItem(recordId);
		}
		synData(player);
	}
	
	public SecretAreaDefRecord getReord(String recordId){
		return recordStore.getItem(recordId);
	}
	
	public boolean removeReord(Player player,String recordId){
		SecretAreaDefRecord record = getReord(recordId);
		boolean isSuccess = recordStore.removeItem(recordId);
		if(isSuccess){
			ClientDataSynMgr.updateData(player, record, synType, eSynOpType.REMOVE_SINGLE);
		}
		return isSuccess;
	}

	public boolean addRecord(Player player, SecretAreaDefRecord record){
	
		boolean addSuccess = recordStore.addItem(record);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, record, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	public void updateRecord(Player player, SecretAreaDefRecord record){
		recordStore.updateItem(record);
		ClientDataSynMgr.updateData(player, record, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public SecretAreaDefRecord get(String areaInfoId){
		SecretAreaDefRecord target = null;
		Enumeration<SecretAreaDefRecord> mapEnum = recordStore.getEnum();
		while (mapEnum.hasMoreElements()) {
			SecretAreaDefRecord record = (SecretAreaDefRecord) mapEnum.nextElement();
			if(StringUtils.equals(areaInfoId, record.getSecretId())){
				target = record;
				break;
			}
		}
		
		return target;
	}

	public void flush(){
		recordStore.flush();
	}

}
