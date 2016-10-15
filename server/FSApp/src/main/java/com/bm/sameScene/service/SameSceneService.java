package com.bm.sameScene.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.SaloonServiceProto.CommonReqMsg;
import com.rwproto.SaloonServiceProto.CommonRspMsg;
import com.rwproto.SaloonServiceProto.RequestType;
import com.rwproto.SaloonServiceProto.ResultType;

/**
 * 同屏请求的分发
 * @author AkenWang
 *
 */
public class SameSceneService implements FsService<CommonReqMsg, RequestType> {

	private SameSceneHandler mHandler = SameSceneHandler.getInstance();

	@Override
	public ByteString doTask(CommonReqMsg request, Player player) {
		ByteString result = null;
		try {
			RequestType gcType = request.getReqType();
			switch (gcType) {
			case Enter:
				result = mHandler.enterPrepareArea(player, request);
				break;
			case Leave:
				result = mHandler.leavePrepareArea(player, request);
				break;
			case InformPosition:
				result = mHandler.informPreparePosition(player, request);
				break;
			case GetPlayerInfo:
				result = mHandler.getPlayersBaseInfo(player, request);
				break;
			case AlreadyIn:
//				mHandler.inPrepareArea(player);
				break;
			default:
				GameLog.error(LogModule.GroupCompetition, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCompetition, player.getUserId(), "出现了Exception异常", e);
			CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
			gcRsp.setRstType(ResultType.FAIL);
			result = gcRsp.build().toByteString();
		}
		return result;
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