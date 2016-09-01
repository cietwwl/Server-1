package com.rw.service.Email;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.EmailProtos.EmailRequest;
import com.rwproto.EmailProtos.EmailRequestType;
import com.rwproto.EmailProtos.EmailResultType;
import com.rwproto.RequestProtos.Request;

public class EmailService implements FsService<EmailRequest, EmailRequestType>{

	private EmailHandler handler = EmailHandler.getInstance();

	@Override
	public ByteString doTask(EmailRequest request, Player player) {
		// TODO Auto-generated method stub
		try{
			EmailRequestType requestType = request.getRequestType();
			switch(requestType){
				case Email_List:
					return handler.getEmailList(player, request);
				case Email_Check:
					return handler.checkEmail(player, request);
				case Email_GetAttachment:
					return handler.getAttachment(player, request);
				default:
					return handler.responseEmailMsg(requestType,EmailResultType.FAIL);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public EmailRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		EmailRequest emailRequest = EmailRequest.parseFrom(request.getBody().getSerializedContent());
		return emailRequest;
	}

	@Override
	public EmailRequestType getMsgType(EmailRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}

}
