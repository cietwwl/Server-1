package com.playerdata.fixEquip;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.fixEquip.exp.FixExpEquipHandler;
import com.playerdata.fixEquip.norm.FixNormEquipHandler;
import com.rw.service.FsService;
import com.rwproto.FixEquipProto.CommonReqMsg;
import com.rwproto.FixEquipProto.RequestType;
import com.rwproto.RequestProtos.Request;


public class FixEquipService implements FsService<CommonReqMsg, RequestType> {

	@Override
	public ByteString doTask(CommonReqMsg request, Player player) {
		// TODO Auto-generated method stub
		FixExpEquipHandler expEquipHandler = FixExpEquipHandler.getInstance();
		FixNormEquipHandler normEquipHandler = FixNormEquipHandler.getInstance();
		
		ByteString byteString = null;
		try {
			RequestType reqType = request.getReqType();
			switch (reqType) {	
			case Norm_level_up:
				byteString = normEquipHandler.levelUp(player, request);
				break;
			case Norm_level_up_one_key:
				byteString = normEquipHandler.levelUpOneKey(player, request);
				break;
			case Norm_quality_up:
				byteString = normEquipHandler.qualityUp(player, request);
				break;
			case Norm_star_up:
				byteString = normEquipHandler.starUp(player, request);
				break;
			case Norm_star_down:
				byteString = normEquipHandler.starDown(player, request);
				break;
			case Exp_level_up:
				byteString = expEquipHandler.levelUp(player, request);
				break;
			case Exp_quality_up:
				byteString = expEquipHandler.qualityUp(player, request);
				break;
			case Exp_star_up:
				byteString = expEquipHandler.starUp(player, request);
				break;
			case Exp_star_down:
				byteString = expEquipHandler.starDown(player, request);
				break;
			default:
				GameLog.error(LogModule.FixEquip, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.FixEquip, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
	}

	@Override
	public CommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		CommonReqMsg commonReq = CommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return commonReq;
	}

	@Override
	public RequestType getMsgType(CommonReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}