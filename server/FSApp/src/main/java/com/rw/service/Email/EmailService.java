package com.rw.service.Email;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.EmailProtos.EmailRequest;
import com.rwproto.EmailProtos.EmailRequestType;
import com.rwproto.EmailProtos.EmailResultType;
import com.rwproto.RequestProtos.Request;

public class EmailService implements FsService{

	private EmailHandler handler = EmailHandler.getInstance();
	
	public ByteString doTask(Request request, Player player) {
		try{
			EmailRequest emailRequest = EmailRequest.parseFrom(request.getBody().getSerializedContent());
			EmailRequestType requestType = emailRequest.getRequestType();
			switch(requestType){
				case Email_List:
					return handler.getEmailList(player, emailRequest);
				case Email_Check:
					return handler.checkEmail(player, emailRequest);
				case Email_GetAttachment:
					return handler.getAttachment(player, emailRequest);
				default:
					return handler.responseEmailMsg(requestType,EmailResultType.FAIL);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

}
