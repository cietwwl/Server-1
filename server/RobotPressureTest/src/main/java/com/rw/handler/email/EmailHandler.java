package com.rw.handler.email;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.EmailProtos.EmailInfo;
import com.rwproto.EmailProtos.EmailRequest;
import com.rwproto.EmailProtos.EmailRequestType;
import com.rwproto.EmailProtos.EmailResponse;
import com.rwproto.EmailProtos.EmailResultType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;


public class EmailHandler {
	
	private static EmailHandler instance = new EmailHandler();
	public static EmailHandler instance(){
		return instance;
	}

	/**
	 * 创建角色
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean openEmailList(Client client) {
		
		EmailRequest.Builder req = EmailRequest.newBuilder();
		req.setRequestType(EmailRequestType.Email_List);
		
		final List<EmailInfo> emailList = new ArrayList<EmailInfo>();
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_EMAIL, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_EMAIL;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					
					EmailResponse rsp = EmailResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("EmailHandler[getEmailList] 转换响应消息为null");
						return false;
					}

					EmailResultType result = rsp.getResultType();
					if (result == EmailResultType.SUCCESS) {
						List<EmailInfo> emailListTmp = rsp.getEmailListList();
						emailList.addAll(emailListTmp);
						
						
					}else{
						RobotLog.fail("EmailHandler[getEmailList] 服务器处理消息失败"+result);
						return false;
						
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("EmailHandler[getEmailList] 失败", e);
					return false;
				}
				return true;
			}

		});
		if(success){
			openAllEmail(client, emailList);
		}
		return success;
	}

	private void openAllEmail(Client client, List<EmailInfo> emailList) {
		for (EmailInfo emailInfo : emailList) {
			openEmail(client, emailInfo.getEmailId());
		}
		
	}

	
	public boolean openEmail(Client client, String emailId ) {
		
		EmailRequest.Builder req = EmailRequest.newBuilder();
		req.setRequestType(EmailRequestType.Email_GetAttachment);
		req.setEmailId(emailId);
		
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_EMAIL, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_EMAIL;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					
					EmailResponse rsp = EmailResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("EmailHandler[openEmail] 转换响应消息为null");
						return false;
					}

					EmailResultType result = rsp.getResultType();
					if (result != EmailResultType.SUCCESS) {						
						RobotLog.fail("EmailHandler[openEmail] 服务器处理消息失败");
						return false;
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("EmailHandler[getEmailList] 失败", e);
					return false;
				}
				return true;
			}

		});
		return success;
	}

}
