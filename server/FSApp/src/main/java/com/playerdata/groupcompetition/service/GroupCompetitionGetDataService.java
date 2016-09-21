package com.playerdata.groupcompetition.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GroupCompetitionProto.CommonGetDataReqMsg;
import com.rwproto.GroupCompetitionProto.GCRequestType;
import com.rwproto.RequestProtos.Request;

public class GroupCompetitionGetDataService implements FsService<CommonGetDataReqMsg, GCRequestType> {

	@Override
	public ByteString doTask(CommonGetDataReqMsg request, Player player) {
		switch (request.getReqType()) {
		case GetSelectionData:
			return GroupCompetitionHandler.getInstance().getSelectionData(player);
		default:
			return null;
		}
	}

	@Override
	public CommonGetDataReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		return CommonGetDataReqMsg.parseFrom(request.getBody().getSerializedContent());
	}

	@Override
	public GCRequestType getMsgType(CommonGetDataReqMsg request) {
		return request.getReqType();
	}

}
