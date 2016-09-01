package com.playerdata.dataSyn.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.DataSynProtos.DataSynReqMsg;
import com.rwproto.DataSynProtos.RequestType;
import com.rwproto.RequestProtos.Request;

public class SynDataService implements FsService<DataSynReqMsg, RequestType> {

	@Override
	public ByteString doTask(DataSynReqMsg request, Player player) {
		// TODO Auto-generated method stub
		ByteString byteString = null;
		try {

			RequestType reqType = request.getReqType();
			switch (reqType) {
			case SynByType:
				byteString = SynDataHandler.getInstance().synByType(player, request);
				break;

			default:
				GameLog.error(LogModule.DataSynService, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			GameLog.error(LogModule.DataSynService, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
	}

	@Override
	public DataSynReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		DataSynReqMsg commonReq = DataSynReqMsg.parseFrom(request.getBody().getSerializedContent());
		return commonReq;
	}

	@Override
	public RequestType getMsgType(DataSynReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}