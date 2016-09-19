package com.playerdata.groupcompetition.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GroupCompetitionProto.CommonReqMsg;
import com.rwproto.GroupCompetitionProto.CommonRspMsg;
import com.rwproto.GroupCompetitionProto.GCRequestType;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.RequestProtos.Request;

/**
 * 帮派争霸赛的分发
 * @author AkenWang
 *
 */
public class GroupCompetitionService implements FsService<CommonReqMsg, GCRequestType> {

	private GroupCompetitionHandler mHandler = GroupCompetitionHandler.getInstance();

	@Override
	public ByteString doTask(CommonReqMsg request, Player player) {
		ByteString result = null;
		try {
			GCRequestType gcType = request.getReqType();
			switch (gcType) {
			case EnterPrepareArea:
				result = mHandler.enterPrepareArea(player, request);
				break;
			case LeavePrepareArea:
				result = mHandler.leavePrepareArea(player, request);
				break;
			case InformPreparePosition:
				result = mHandler.informPreparePosition(player, request);
				break;
			case GetPlayersBaseInfo:
				result = mHandler.getPlayersBaseInfo(player, request);
			default:
				GameLog.error(LogModule.GroupCompetition, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCompetition, player.getUserId(), "出现了Exception异常", e);
			CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			result = gcRsp.build().toByteString();
		}
		return result;
	}

	@Override
	public CommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		CommonReqMsg msgRequest = CommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return msgRequest;
	}

	@Override
	public GCRequestType getMsgType(CommonReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}