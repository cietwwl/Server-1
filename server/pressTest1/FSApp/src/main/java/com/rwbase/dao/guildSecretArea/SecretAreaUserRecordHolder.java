package com.rwbase.dao.guildSecretArea;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class SecretAreaUserRecordHolder{//帮派秘境-用户的秘境记录
	
	
	private String userId;
	private MapItemStore<SecretAreaUserRecord> recordStore;
	final private eSynType synType = eSynType.SECRETAREA_USER_RECORD;
	
	public SecretAreaUserRecordHolder(String roleIdP) {
		userId = roleIdP;
		recordStore = new MapItemStore<SecretAreaUserRecord>("userId", userId, SecretAreaUserRecord.class);
	}

	public void synData(Player player){
		List<SecretAreaUserRecord> recordList = getRecordList();
		ClientDataSynMgr.synDataList(player, recordList, synType, eSynOpType.UPDATE_LIST);
	}
	
	/*
	 * 获取当前用户的副本地图记录,以"0,0,0"记录下"是否领取1,是否领取2,是否领取3"，
	 */
	public List<SecretAreaUserRecord> getRecordList()	
	{
		
		List<SecretAreaUserRecord> recordList = new ArrayList<SecretAreaUserRecord>();
		Enumeration<SecretAreaUserRecord> mapEnum = recordStore.getEnum();
		while (mapEnum.hasMoreElements()) {
			SecretAreaUserRecord record = (SecretAreaUserRecord) mapEnum.nextElement();
			recordList.add(record);
		}
		
		return recordList;
	}
	
	public SecretAreaUserRecord getReord(String recordId){
		return recordStore.getItem(recordId);
	}
	
	public boolean removeReord(Player player,String recordId){
		SecretAreaUserRecord userRecord = getReord(recordId);	
		boolean removeSuccess = recordStore.removeItem(recordId);
		if(removeSuccess){
			ClientDataSynMgr.updateData(player, userRecord, synType, eSynOpType.REMOVE_SINGLE);
		}
		return removeSuccess;
	}
	
	public boolean addRecord(Player player, SecretAreaUserRecord record){
	
		boolean addSuccess = recordStore.addItem(record);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, record, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}

	public void flush(){
		recordStore.flush();	
	}

	
}
