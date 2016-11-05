package com.playerdata.battleVerify;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.BattleVerifyProto.BattleVerifyComReqMsg;
import com.rwproto.BattleVerifyProto.RequestType;
import com.rwproto.RequestProtos.Request;



public class BattleVerifyService implements FsService {


	@Override
	public ByteString doTask(Request request, Player player) {
		
	
		
		ByteString byteString = null;
		try {
			BattleVerifyComReqMsg commonReq = BattleVerifyComReqMsg.parseFrom(request.getBody().getSerializedContent());
			
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {	
			case Copy:
				byteString = BattleVerifyHandler.getInstance().verify(player, commonReq);
				break;
	
			default:
				GameLog.error(LogModule.BattleVerify, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.BattleVerify, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
		
	}
}