package com.playerdata.dataSyn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rwproto.DataSynProtos.MsgDataSynList;
import com.rwproto.DataSynProtos.MsgDataSynList.Builder;
import com.rwproto.MsgDef.Command;

public class SynDataInReqMgr {

	private Map<Object, SynDataInfo> synDataMap = new ConcurrentHashMap<Object, SynDataInfo>();
	private List<Object> orderList = new ArrayList<Object>();

	private boolean isInReq = false;

	public boolean isInReq() {
		return isInReq;
	}

	public void setInReq(boolean isInReq) {
		this.isInReq = isInReq;
	}

	public void addSynData(Object serverData, SynDataInfo synData) {
		synDataMap.put(serverData, synData);
		if(!orderList.contains(serverData)){
			orderList.add(serverData);
		}
	}

	public void doSyn(Player player) {
		if (player == null) {
			return;
		}
		
		try {
			Collection<SynDataInfo> values = synDataMap.values();
			if(!values.isEmpty()){
				Builder msgDataSynList = MsgDataSynList.newBuilder();
				
				for (Object keyObject : orderList) {
					SynDataInfo synData = synDataMap.get(keyObject);
					if(synData!=null){
						msgDataSynList.addMsgDataSyn(synData.getContent());
					}
				}
				
				player.SendMsg(Command.MSG_DATA_SYN, msgDataSynList.build().toByteString());
			}
		} catch (Exception e) {
			GameLog.error(LogModule.COMMON.getName(), player.getUserId(), "SynDataInReqMgr[doSyn] error synType:", e);
		} finally{			
			orderList.clear();
			synDataMap.clear();
			isInReq = false;		
		}
		
	}

}
