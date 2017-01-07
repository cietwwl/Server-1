package com.playerdata.activity.shakeEnvelope.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityCommonTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityCommonTypeProto.ActivityCommonRspMsg;
import com.rwproto.ActivityCommonTypeProto.RequestType;
import com.rwproto.ActivityCommonTypeProto.ResultType;
import com.rwproto.RequestProtos.Request;


public class ActivityService implements FsService<ActivityCommonReqMsg, RequestType> {

	@Override
	public ByteString doTask(ActivityCommonReqMsg request, Player player) {
		
		ByteString byteString = null;
		try {
			RequestType reqType = request.getReqType();
			switch (reqType) {
			case ShakeEnvelope:// 摇一摇红包
				byteString = ActivityShakeEnvelopeHandler.getInstance().getEnvelopeReward(player, request);
				break;
			default:
				GameLog.error(LogModule.ComActType, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
				response.setReqType(request.getReqType());
				response.setResult(ResultType.FAIL);
				response.setTipMsg("接收到一个未知的请求");
				break;
			}
		} catch (Exception e) {
			GameLog.error(LogModule.ComActType, player.getUserId(), "出现了Exception异常", e);
			ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
			response.setReqType(request.getReqType());
			response.setResult(ResultType.EXCEPTION);
			response.setTipMsg("服务器内部错误");
		}
		return byteString;
	}

	@Override
	public ActivityCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		ActivityCommonReqMsg commonReq = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return commonReq;
	}

	@Override
	public RequestType getMsgType(ActivityCommonReqMsg request) {
		return request.getReqType();
	}
}