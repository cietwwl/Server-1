package com.playerdata.battleVerify;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.BattleVerifyProto.BattleVerifyComReqMsg;
import com.rwproto.BattleVerifyProto.RequestType;
import com.rwproto.RequestProtos.Request;



public class BattleVerifyService implements FsService<BattleVerifyComReqMsg, RequestType> {

	@Override
	public ByteString doTask(BattleVerifyComReqMsg request, Player player) {
		// TODO Auto-generated method stub
		ByteString byteString = null;
		try {
			
			RequestType reqType = request.getReqType();
			switch (reqType) {	
			case Copy:
				byteString = BattleVerifyHandler.getInstance().verify(player, request);
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

	@Override
	public BattleVerifyComReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		BattleVerifyComReqMsg commonReq = BattleVerifyComReqMsg.parseFrom(request.getBody().getSerializedContent());
		return commonReq;
	}

	@Override
	public RequestType getMsgType(BattleVerifyComReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}