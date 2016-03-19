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

	public static FriendHandler instance() {
		return instance;
	}

	/**
	 * 移除好友
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean remove(Client client, String friendUserId) {

		FriendRequest.Builder req = FriendRequest.newBuilder();
		req.setRequestType(EFriendRequestType.REMOVE_FRIEND);
		req.setOtherUserId(friendUserId);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_FRIEND, req.build().toByteString(), new MsgReciver() {

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
						RobotLog.fail("FriendHandler[remove] 转换响应消息为null");
						return false;
					}

					EFriendResultType result = rsp.getResultType();
					if (result == EFriendResultType.SUCCESS) {
						RobotLog.info("FriendHandler[remove] 成功");
						return true;
					} else {
						RobotLog.fail("FriendHandler[remove] 服务器处理消息失败:" + result);
						return false;

					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("FriendHandler[remove] 失败", e);
					return false;
				}
			}

		});
		return success;
	}

	/**
	 * 增加好友
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean add(Client client, String friendUserId) {

		FriendRequest.Builder req = FriendRequest.newBuilder();
		req.setRequestType(EFriendRequestType.REQUEST_ADD_FRIEND);
		req.setOtherUserId(friendUserId);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_FRIEND, req.build().toByteString(), new MsgReciver() {

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
					} else {
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
	 * 接受所有好友申请
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean acceptAll(Client client) {

		FriendRequest.Builder req = FriendRequest.newBuilder();
		req.setRequestType(EFriendRequestType.CONSENT_ADD_FRIEND_ALL);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_FRIEND, req.build().toByteString(), new MsgReciver() {

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
					} else {
						RobotLog.fail("FriendHandler[acceptAll] 服务器处理消息失败");
						return false;

					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("FriendHandler[acceptAll] 失败", e);
					return false;
				}
			}

		});
		return success;
	}

	/**
	 * 赠送好友体力
	 * 
	 * @param client
	 */
	public boolean givePowerOne(Client client, String friendUserId) {
		FriendRequest.Builder req = FriendRequest.newBuilder();
		req.setRequestType(EFriendRequestType.GIVE_POWER);
		req.setOtherUserId(friendUserId);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_FRIEND, req.build().toByteString(), new MsgReciver() {

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
						RobotLog.fail("FriendHandler[givePowerOne] 转换响应消息为null");
						return false;
					}

					EFriendResultType result = rsp.getResultType();
					if (result == EFriendResultType.SUCCESS) {
						RobotLog.info("FriendHandler[givePowerOne] 成功");
						return true;
					} else {
						RobotLog.fail("FriendHandler[givePowerOne] 服务器处理消息失败");
						return false;

					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("FriendHandler[givePowerOne] 失败", e);
					return false;
				}
			}

		});
		return success;
	}

	/**
	 * 领取所有好友体力
	 * 
	 * @param client
	 */
	public boolean receivePowerOne(Client client, String friendUserId) {
		FriendRequest.Builder req = FriendRequest.newBuilder();
		req.setRequestType(EFriendRequestType.RECEIVE_POWER);
		req.setOtherUserId(friendUserId);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_FRIEND, req.build().toByteString(), new MsgReciver() {

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
						RobotLog.fail("FriendHandler[receivePowerOne] 转换响应消息为null");
						return false;
					}

					EFriendResultType result = rsp.getResultType();
					if (result == EFriendResultType.SUCCESS) {
						RobotLog.info("FriendHandler[receivePowerOne] 成功");
						return true;
					} else {
						RobotLog.fail("FriendHandler[receivePowerOne] 服务器处理消息失败");
						return false;

					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("FriendHandler[receivePowerOne] 失败", e);
					return false;
				}
			}

		});
		return success;
	}

	/**
	 * 赠送所有好友体力
	 * 
	 * @param client
	 */
	public boolean givePowerAll(Client client) {
		FriendRequest.Builder req = FriendRequest.newBuilder();
		req.setRequestType(EFriendRequestType.GIVE_POWER_ALL);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_FRIEND, req.build().toByteString(), new MsgReciver() {

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
						RobotLog.fail("FriendHandler[givePowerAll] 转换响应消息为null");
						return false;
					}

					EFriendResultType result = rsp.getResultType();
					if (result == EFriendResultType.SUCCESS) {
						RobotLog.info("FriendHandler[givePowerAll] 成功");
						return true;
					} else {
						RobotLog.fail("FriendHandler[givePowerAll] 服务器处理消息失败");
						return false;

					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("FriendHandler[givePowerAll] 失败", e);
					return false;
				}
			}

		});
		return success;
	}

	/**
	 * 领取所有好友体力
	 * 
	 * @param client
	 */
	public boolean receivePowerAll(Client client) {
		FriendRequest.Builder req = FriendRequest.newBuilder();
		req.setRequestType(EFriendRequestType.RECEIVE_POWER_ALL);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_FRIEND, req.build().toByteString(), new MsgReciver() {

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
						RobotLog.fail("FriendHandler[receivePowerAll] 转换响应消息为null");
						return false;
					}

					EFriendResultType result = rsp.getResultType();
					if (result == EFriendResultType.SUCCESS_MSG) {
						RobotLog.info("FriendHandler[receivePowerAll] 成功");
						return true;
					} else {
						RobotLog.fail("FriendHandler[receivePowerAll] 服务器处理消息失败");
						return false;

					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("FriendHandler[receivePowerAll] 失败", e);
					return false;
				}
			}

		});
		return success;
	}
}