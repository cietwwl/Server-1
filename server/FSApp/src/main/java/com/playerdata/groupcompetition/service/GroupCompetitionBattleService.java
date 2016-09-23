package com.playerdata.groupcompetition.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GroupCompetitionBattleProto.GCBattleCommonReqMsg;
import com.rwproto.GroupCompetitionBattleProto.GCBattleReqType;
import com.rwproto.RequestProtos.Request;

/**
 * @Author HC
 * @date 2016年9月22日 下午4:27:55
 * @desc
 **/

public class GroupCompetitionBattleService implements FsService<GCBattleCommonReqMsg, GCBattleReqType> {

	@Override
	public ByteString doTask(GCBattleCommonReqMsg request, Player player) {
		GroupCompetitionBattleHandler handler = GroupCompetitionBattleHandler.getHandler();
		GCBattleReqType reqType = request.getReqType();
		switch (reqType) {
		case BATTLE_START:
			return handler.battleStartHandler(player);
		case UPLOAD_HP_INFO:
			return handler.uploadHpInfoHandler(player, request.getUploadHpInfoReq());
		case BATTLE_END:
			return handler.battleEndHandler(player, request.getBattleEndReq());
		default:
			return null;
		}
	}

	@Override
	public GCBattleCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		return GCBattleCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
	}

	@Override
	public GCBattleReqType getMsgType(GCBattleCommonReqMsg request) {
		return request.getReqType();
	}
}