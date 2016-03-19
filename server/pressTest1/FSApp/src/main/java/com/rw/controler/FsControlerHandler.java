package com.rw.controler;



import com.rwbase.dao.user.UserGameData;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;
import com.rwproto.ResponseProtos.ResponseHeader;


public class FsControlerHandler {
	
	
	public ResponseHeader getResponseHeader(Request req, Command command, UserGameData user ) {
		String token = req.getHeader().getToken();
		ResponseHeader.Builder responseHeaderBuilder = ResponseHeader.newBuilder()
				.setToken(token)
				.setCommand(command).setStatusCode(200);
		
	
		return responseHeaderBuilder.build();
	}

	
	
}
