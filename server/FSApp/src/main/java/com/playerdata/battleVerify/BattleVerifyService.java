package com.playerdata.battleVerify;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.BattleVerifyProto.BattleVerifyMsg;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;



public class BattleVerifyService implements FsService<GeneratedMessage, Command> {
	
	private static Map<Class<? extends GeneratedMessage>, Command> cmdMap = new HashMap<Class<? extends GeneratedMessage>,Command>();

	static {
		cmdMap.put(BattleVerifyMsg.class, Command.MSG_BATTLE_VERIFY);
	}

	@Override
	public ByteString doTask(GeneratedMessage request, Player player) {
		Command cmd = cmdMap.get(request.getClass());
		switch (cmd) {
		case MSG_BATTLE_VERIFY:
			return BattleVerifyHandler.getInstance().verifyArmyInfo(player, (BattleVerifyMsg)request);
		default:
			GameLog.error("BattleVerifyService", "doTask", "未能处理的消息类型：" + cmd);
		}
		return null;
	}

	@Override
	public GeneratedMessage parseMsg(Request request) throws InvalidProtocolBufferException {
		switch (request.getHeader().getCommand()) {
		case MSG_BATTLE_VERIFY:
			return BattleVerifyMsg.parseFrom(request.getBody().getSerializedContent());
		default:
			return null;
		}
	}

	@Override
	public Command getMsgType(GeneratedMessage request) {
		return cmdMap.get(request.getClass());
	}

//	@Override
//	public ByteString doTask(BattleVerifyComReqMsg request, Player player) {
//		// TODO Auto-generated method stub
//		ByteString byteString = null;
//		try {
//			
//			RequestType reqType = request.getReqType();
//			switch (reqType) {	
//			case Copy:
//				byteString = BattleVerifyHandler.getInstance().verify(player, request);
//				break;
//	
//			default:
//				GameLog.error(LogModule.BattleVerify, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
//				break;
//			}
//			
//		} catch (Exception e) {
//			GameLog.error(LogModule.BattleVerify, player.getUserId(), "出现了Exception异常", e);
//		}
//		return byteString;
//	}
//
//	@Override
//	public BattleVerifyComReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
//		BattleVerifyComReqMsg commonReq = BattleVerifyComReqMsg.parseFrom(request.getBody().getSerializedContent());
//		return commonReq;
//	}
//
//	@Override
//	public RequestType getMsgType(BattleVerifyComReqMsg request) {
//		return request.getReqType();
//	}
}