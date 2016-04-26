package com.rw.service.gm;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GMServiceProtos.MsgGMRequest;
import com.rwproto.GMServiceProtos.eGMType;
import com.rwproto.RequestProtos.Request;

public class GMService implements FsService{

	private GMHandler gmHandler = GMHandler.getInstance();
	
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		if(!GMHandler.getInstance().isActive()){
			return result;
		}
		// TODO Auto-generated method stub
		try {
			MsgGMRequest msgGMRequest = MsgGMRequest.parseFrom(request.getBody().getSerializedContent());
			eGMType gmType = msgGMRequest.getGMType();
			switch (gmType) {
			case GM_COMMAND:
				result = gmHandler.executeGMCommand(player,msgGMRequest);
				break;

			default:
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
