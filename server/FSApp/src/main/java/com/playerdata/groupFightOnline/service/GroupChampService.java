package com.playerdata.groupFightOnline.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GroupChampProto.CommonReqMsg;
import com.rwproto.GroupChampProto.RequestType;
import com.rwproto.RequestProtos.Request;


public class GroupChampService implements FsService {


	@Override
	public ByteString doTask(Request request, Player player) {
		
		
		GroupChampHandler handler = GroupChampHandler.getInstance();
		ByteString byteString = null;
		try {
			CommonReqMsg commonReq = CommonReqMsg.parseFrom(request.getBody().getSerializedContent());
			
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {	
			case Enter:
				byteString = handler.enter(player,commonReq);
				break;
			
			default:
				GameLog.error(LogModule.GroupChamp, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.GroupChamp, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
		
	}
}