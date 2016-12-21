package com.playerdata.activity.shakeEnvelope.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityChargeRankProto.ActivityCommonReqMsg;
import com.rwproto.ActivityChargeRankProto.ActivityCommonRspMsg;
import com.rwproto.ActivityChargeRankProto.RequestType;
import com.rwproto.RequestProtos.Request;


public class ActivityService implements FsService<ActivityCommonReqMsg, RequestType> {

	@Override
	public ByteString doTask(ActivityCommonReqMsg request, Player player) {
		ActivityHandler handler = ActivityHandler.getInstance();

		ByteString byteString = null;
		try {
			RequestType reqType = request.getReqType();
			switch (reqType) {
			case ChargeRank:// 充值排行榜
				byteString = handler.getChargeRank(player, request);
				break;
			case ConsumeRank:// 消费排行榜
				byteString = handler.getConsumeRank(player, request);
				break;
			default:
				GameLog.error(LogModule.ComActChargeRank, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
				response.setReqType(request.getReqType());
				response.setIsSuccess(false);
				response.setTipMsg("接收到一个未知的请求");
				break;
			}
		} catch (Exception e) {
			GameLog.error(LogModule.ComActChargeRank, player.getUserId(), "出现了Exception异常", e);
			ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
			response.setReqType(request.getReqType());
			response.setIsSuccess(false);
			response.setTipMsg("获取排行榜失败");
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