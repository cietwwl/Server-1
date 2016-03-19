package com.rw.dataSyn;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.MsgDataSynList;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class DataSynMgr {
	
	private Map<eSynType, SynAction> synActionMap = new HashMap<eSynType, SynAction>();

	public void dataSyn(Response resp){
		if(Command.MSG_DATA_SYN == resp.getHeader().getCommand()){
			try {
				MsgDataSynList datasynList = MsgDataSynList.parseFrom(resp.getSerializedContent());
				for (MsgDataSyn dataSyn : datasynList.getMsgDataSynList()) {
					eSynType synType = dataSyn.getSynType();
					if(synActionMap.containsKey(synType)){
						synActionMap.get(synType).doAction(dataSyn);
					}
					
				}
				
				
			} catch (InvalidProtocolBufferException e) {
				throw(new RuntimeException("ClientMsgHandler[dataSyn] parse error", e));
			}
		}
	}
	
	
}
