package com.rw.service.praise;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.PraiseServiceProto.PraiseCommonReqMsg;
import com.rwproto.PraiseServiceProto.PraiseReqType;
import com.rwproto.RequestProtos.Request;

/**
 * @Author HC
 * @date 2016年10月14日 上午11:23:28
 * @desc 点赞功能的协议处理
 **/

public class PraiseService implements FsService<PraiseCommonReqMsg, PraiseReqType> {

	@Override
	public ByteString doTask(PraiseCommonReqMsg request, Player player) {
		ByteString result = null;
		PraiseHandler handler = PraiseHandler.getHandler();
		PraiseReqType reqType = request.getReqType();
		switch (reqType) {
		case GET_PRAISE_TYPE:
			return handler.getPraiseDataHandler(player, request.getGetPraiseReqMsg());
		case PRAISE_SOMEONE_TYPE:
			return handler.praiseToSomeoneHandler(player, request.getPraiseSomeoneReqMsg());
		}
		return result;
	}

	@Override
	public PraiseCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		return PraiseCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
	}

	@Override
	public PraiseReqType getMsgType(PraiseCommonReqMsg request) {
		return request.getReqType();
	}
}