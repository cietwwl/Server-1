package com.rw.handler.groupCompetition.data.prepare;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

public class SameSceneSynDataHolder {
	
	private  Map<String, PositionInfo> list = new HashMap<String, PositionInfo>();
	
	private SynDataListHolder<SameSceneSynData> listHolder = new SynDataListHolder<SameSceneSynData>(SameSceneSynData.class);
	private long lastMoveTime;
	
	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		// 更新数据
		List<SameSceneSynData> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			SameSceneSynData ugfData = itemList.get(i);
			if(null != ugfData.getSynData()){
				for(Entry<String, PositionInfo> entry : ugfData.getSynData().entrySet()){
					list.put(entry.getKey(), entry.getValue());
				}
			}
			List<String> removeList = ugfData.getRemoveMembers();
			if(null != removeList){
				for(String removeId : ugfData.getRemoveMembers()){
					list.remove(removeId);
				}
			}
		}
	}
	
	public PositionInfo getUserGFData(String userID){
		return list.get(userID);
	}

	public long getLastMoveTime() {
		return lastMoveTime;
	}

	public void setLastMoveTime(long lastMoveTime) {
		this.lastMoveTime = lastMoveTime;
	}
}
