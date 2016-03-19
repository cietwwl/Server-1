package com.rw.handler.friend;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.FriendServiceProtos.EFriendRequestType;
import com.rwproto.FriendServiceProtos.EFriendResultType;
import com.rwproto.FriendServiceProtos.FriendRequest;
import com.rwproto.FriendServiceProtos.FriendResponse;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class FriendHandler {

	
	private static FriendHandler instance = new FriendHandler();
	public static FriendHandler instance(){
		return instance;
	}

	/**
	 * 创建角色
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean remove(Client client,String friendUserId) {
		
		FriendRequest.Builder req = FriendRequest.newBuilder();
		req.setRequestType(EFriendRequestType.REMOVE_FRIEND);
		req.setOtherUserId(friendUserId);
		
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_FRIEND, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_FRIEND;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					
					FriendResponse rsp = FriendResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("FriendHandler[add] 转换响应消息为null");
						return false;
					}
					
					EFriendResultType result = rsp.getResultType();
					if (result == EFriendResultType.SUCCESS) {
						RobotLog.info("FriendHandler[add] 成功");
						return true;
					}else{
						RobotLog.fail("FriendHandler[add] 服务器处理消息失败:"+result);
						return false;
						
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("FriendHandler[add] 失败", e);
					return false;
				}
			}
			
		});
		return success;
	}
	/**
	 * 创建角色
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean add(Client client,String friendUserId) {

		FriendRequest.Builder req = FriendRequest.newBuilder();
		req.setRequestType(EFriendRequestType.REQUEST_ADD_FRIEND);
		req.setOtherUserId(friendUserId);
		
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_FRIEND, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_FRIEND;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					
					FriendResponse rsp = FriendResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("FriendHandler[add] 转换响应消息为null");
						return false;
					}

					EFriendResultType result = rsp.getResultType();
					if (result == EFriendResultType.SUCCESS) {
						RobotLog.info("FriendHandler[add] 成功");
						return true;
					}else{
						RobotLog.fail("FriendHandler[add] 服务器处理消息失败");
						return false;
						
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("FriendHandler[add] 失败", e);
					return false;
				}
			}

		});
		return success;
	}
	
	/**
	 * 创建角色
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean acceptAll(Client client) {

		FriendRequest.Builder req = FriendRequest.newBuilder();
		req.setRequestType(EFriendRequestType.CONSENT_ADD_FRIEND_ALL);
		
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_FRIEND, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_FRIEND;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					
					FriendResponse rsp = FriendResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("FriendHandler[acceptAll] 转换响应消息为null");
						return false;
					}

					EFriendResultType result = rsp.getResultType();
					if (result == EFriendResultType.SUCCESS_MSG) {
						RobotLog.info("FriendHandler[acceptAll] 成功");
						return true;
					}else{
						RobotLog.fail("FriendHandler[acceptAll] 服务器处理消息失败");
						return false;
						
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("FriendHandler[add] 失败", e);
					return false;
				}
			}

		});
		return success;
	}

}
