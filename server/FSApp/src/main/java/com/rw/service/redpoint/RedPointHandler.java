package com.rw.service.redpoint;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwproto.RedPointServiceProtos.RedPointServiceRequest;
import com.rwproto.RedPointServiceProtos.RedPointServiceResponse;

public class RedPointHandler {
	private static RedPointHandler instance = new RedPointHandler();
	
	public static RedPointHandler getInstance(){
		return instance;
	}

	public ByteString reFreshRedPoint(Player player,RedPointServiceRequest commnreq) {
		RedPointServiceResponse.Builder response = RedPointServiceResponse.newBuilder();
		response.setRespType(commnreq.getReqType());
		int id = commnreq.getId();
		String extraInfo = commnreq.getExtraInfo();
		boolean issucce = false;
		issucce = RedPointManager.getRedPointManager().reFreshRedPoint(player,id,extraInfo);
		response.setIsSuccess(issucce);
		return response.build().toByteString();
	} 
}
