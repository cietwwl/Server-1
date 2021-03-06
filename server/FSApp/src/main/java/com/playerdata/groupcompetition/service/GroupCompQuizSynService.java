package com.playerdata.groupcompetition.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GroupCompetitionProto.CommonRspMsg;
import com.rwproto.GroupCompetitionProto.GCRequestType;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.GroupCompetitionProto.ReqAllGuessInfo;
import com.rwproto.RequestProtos.Request;

/**
 * 帮派争霸赛竞猜的分发
 * @author AkenWang
 *
 */
public class GroupCompQuizSynService implements FsService<ReqAllGuessInfo, GCRequestType> {

	private GroupCompetitionHandler mHandler = GroupCompetitionHandler.getInstance();

	@Override
	public ByteString doTask(ReqAllGuessInfo request, Player player) {
		ByteString result = null;
		try {
			GCRequestType gcType = request.getReqType();
			switch (gcType) {
			case GetCanGuessMatch:
				result = mHandler.getCanGuessMatch(player, request);
				break;
			default:
				GameLog.error(LogModule.GroupCompetition, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCompetition, player.getUserId(), "出现了Exception异常", e);
			CommonRspMsg.Builder gcRsp = CommonRspMsg.newBuilder();
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setTipMsg("同步竞猜数据有误");
			result = gcRsp.build().toByteString();
		}
		return result;
	}

	@Override
	public ReqAllGuessInfo parseMsg(Request request) throws InvalidProtocolBufferException {
		ReqAllGuessInfo msgRequest = ReqAllGuessInfo.parseFrom(request.getBody().getSerializedContent());
		return msgRequest;
	}

	@Override
	public GCRequestType getMsgType(ReqAllGuessInfo request) {
		return request.getReqType();
	}
}