package com.playerdata.dataSyn.service;

import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwproto.DataSynProtos.DataSynReqMsg;
import com.rwproto.DataSynProtos.DataSynRspMsg;
import com.rwproto.DataSynProtos.DataSynVersion;


public class SynDataHandler {
	
	private static SynDataHandler instance = new SynDataHandler();
	
	public static SynDataHandler getInstance(){
		return instance;
	}

	public ByteString synByType(Player player, DataSynReqMsg synReqMsg) {
		DataSynRspMsg.Builder response = DataSynRspMsg.newBuilder();
		response.setReqType(synReqMsg.getReqType());
		
		
		List<DataSynVersion> versionList = synReqMsg.getVersionList();
		for (DataSynVersion clientVerion : versionList) {
			
			player.getDataSynVersionHolder().synByClientVersion(player, clientVerion.getSynType(), clientVerion.getVersion());		
			
		}

		response.setIsSuccess(true);		
		
		return response.build().toByteString();
	}




}
