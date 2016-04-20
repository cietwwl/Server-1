package com.playerdata.charge.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rwproto.ChargeServiceProto.ChargeServiceCommonReqMsg;
import com.rwproto.ChargeServiceProto.RequestType;
import com.rwproto.RequestProtos.Request;

public class ChargeService {

	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			ChargeServiceCommonReqMsg chargetReq = ChargeServiceCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
			RequestType reqType = chargetReq.getReqType();
			switch (reqType){
				case Charge:
				result = ChargeHandler.getInstance().charge(player, chargetReq);
				break;
		
				default:
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return result;
	}

	
}
