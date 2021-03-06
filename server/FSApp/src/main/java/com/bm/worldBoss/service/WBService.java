package com.bm.worldBoss.service;

import com.bm.worldBoss.WBMgr;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.WorldBossProtos.CommonReqMsg;
import com.rwproto.WorldBossProtos.RequestType;


public class WBService implements FsService<CommonReqMsg, RequestType>  {

	@Override
	public ByteString doTask(CommonReqMsg request, Player player) {

		ByteString byteString = null;
		try {

			player.getUserTmpGameDataFlag().setSynFightingAll(true);

			RequestType reqType = request.getReqType();
			switch (reqType) {
			case Enter:
			case SynData:
				byteString = WBHandler.getInstance().getSuccessRep(player, request);
				break;

			case BuyBuff:
				byteString = WBHandler.getInstance().doBuyBuff(player, request);
				break;

			case BuyCD:
				byteString = WBHandler.getInstance().doBuyCD(player, request);
				break;

			case FightBegin:
				byteString = WBHandler.getInstance().doFightBegin(player, request);
				break;
			case FightUpdate:
				byteString = WBHandler.getInstance().doFightUpdate(player, request);
				break;
			case FightEnd:
				byteString = WBHandler.getInstance().doFightEnd(player, request);
				break;
			case ApplyAutoFight:
				byteString = WBHandler.getInstance().applyAutoFight(player,request);
				break;
			default:
				break;

			}
			

		} catch (Exception e) {

			GameLog.error(LogModule.WorldBoss, player.getUserId(),
					"出现了Exception异常", e);
		}
		return byteString;
	}
	
	
	

	@Override
	public CommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		CommonReqMsg msgRequest = CommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return msgRequest;
	}

	@Override
	public RequestType getMsgType(CommonReqMsg request) {
		
		return request.getReqType();
	}


	
}