package com.rw.service.gm;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GMServiceProtos.MsgGMRequest;
import com.rwproto.GMServiceProtos.eGMType;
import com.rwproto.RequestProtos.Request;

public class GMService implements FsService<MsgGMRequest, eGMType>{

	private GMHandler gmHandler = GMHandler.getInstance();

	@Override
	public ByteString doTask(MsgGMRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		if(!GMHandler.getInstance().isActive()){
			return result;
		}
		try {
			eGMType gmType = request.getGMType();
			switch (gmType) {
			case GM_COMMAND:
				result = gmHandler.executeGMCommand(player,request);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public MsgGMRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgGMRequest msgGMRequest = MsgGMRequest.parseFrom(request.getBody().getSerializedContent());
		return msgGMRequest;
	}

	@Override
	public eGMType getMsgType(MsgGMRequest request) {
		// TODO Auto-generated method stub
		return request.getGMType();
	}

}
