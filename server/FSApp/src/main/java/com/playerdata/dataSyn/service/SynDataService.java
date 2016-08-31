package com.playerdata.dataSyn.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.DataSynProtos.DataSynReqMsg;
import com.rwproto.DataSynProtos.RequestType;
import com.rwproto.RequestProtos.Request;



public class SynDataService implements FsService {


	@Override
	public ByteString doTask(Request request, Player player) {		
			
		ByteString byteString = null;
		try {
			DataSynReqMsg commonReq = DataSynReqMsg.parseFrom(request.getBody().getSerializedContent());
			
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {
				case SynByType:
				byteString = SynDataHandler.getInstance().synByType(player, commonReq);
				break;
			
		
			default:
				GameLog.error(LogModule.DataSynService, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			GameLog.error(LogModule.DataSynService, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
		
	}
}