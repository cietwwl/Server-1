package com.rw.handler.gameLogin;

import java.util.List;
import java.util.Random;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.ClientInfo;
import com.rw.account.ServerInfo;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.dataSyn.JsonUtil;
import com.rwproto.GameLoginProtos.GameLoginRequest;
import com.rwproto.GameLoginProtos.GameLoginResponse;
import com.rwproto.GameLoginProtos.eGameLoginType;
import com.rwproto.GameLoginProtos.eLoginResultType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GameLoginHandler {
	
	private static GameLoginHandler instance = new GameLoginHandler();
	public static GameLoginHandler instance(){
		return instance;
	}

	/** 所有运行起来的客户端 */
	private static final Random random = new Random();

	/**
	 * 创建角色
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean createRole(final Client client, int serverId) {
		
		String accountId = client.getAccountId();
		System.err.printf("[%s]用户选择服务器创建角色开始....\n", accountId );
		List<ServerInfo> serverList = client.getServerList();
		if (serverList == null || serverList.isEmpty()) {
			RobotLog.info("区服列表是空的，不能进行选服，帐号退出创建角色步骤！！！！");
			return false;
		}
		
		ServerInfo sInfo = client.getServerById(serverId);
		
		if (!client.doConnect(sInfo.getServerIP(), sInfo.getServerPort())) {
			RobotLog.fail("连接服务器[%s][%s]失败！退出登录游戏服务器步骤, host:"+sInfo.getServerIP()+" port:"+sInfo.getServerPort());
			return false;
		}
		
		GameLoginRequest.Builder req = GameLoginRequest.newBuilder();
		req.setLoginType(eGameLoginType.CREATE_ROLE);
		req.setAccountId(accountId);
		req.setPassword(client.getPassword());
		req.setZoneId(sInfo.getZoneId());
		req.setNick("独孤求败" + accountId);// 随机角色名字
		req.setSex(random.nextInt(2));// 随机性别
//		ClientInfo clientInfo = new ClientInfo();
//		clientInfo.setAccountId(accountId);
//		req.setClientInfoJson(JsonUtil.writeValue(clientInfo));
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_LOGIN_GAME, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_LOGIN_GAME;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					GameLoginResponse rsp = GameLoginResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("GameLoginHandler[createRole] 角色的业务逻辑出现了错误，转换响应消息为null");
						return false;
					}

					eLoginResultType result = rsp.getResultType();
					if (result == eLoginResultType.FAIL || result == eLoginResultType.NO_ROLE) {
						RobotLog.fail("GameLoginHandler[createRole] 角色的业务逻辑出现了错误，服务器处理消息失败  原因:"+rsp.getError());
						return false;
					}
					
					client.setUserId(rsp.getUserId());
					RobotLog.info("GameLoginHandler[createRole] 角色创建通过！  accountId:"+client.getAccountId()+" password:"+client.getPassword()+","+rsp.getUserId());
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("GameLoginHandler[createRole] accountId:"+client.getAccountId()+" password:"+client.getPassword(), e);
					return false;
				}
				return true;
			}

		});
		return success;
		
		
	}

	/**
	 * 角色登录
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean loginGame(Client client, final int serverId) {

		
		String accountId = client.getAccountId();
		System.err.printf("[%s]用户选择角色登录服务器开始....\n", accountId );
		
		ServerInfo sInfo = client.getServerById(serverId);
		
		if (!client.doConnect(sInfo.getServerIP(), sInfo.getServerPort())) {
			RobotLog.fail("连接服务器失败！退出登录游戏服务器步骤, host:"+sInfo.getServerIP()+" port:"+sInfo.getServerPort());
			return false;
		}
		
		GameLoginRequest.Builder req = GameLoginRequest.newBuilder();
		req.setLoginType(eGameLoginType.GAME_LOGIN);
		req.setAccountId(accountId);
		req.setZoneId(sInfo.getZoneId());

		boolean success = client.getMsgHandler().sendMsg( Command.MSG_LOGIN_GAME, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_LOGIN_GAME;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					GameLoginResponse rsp = GameLoginResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("GameLoginHandler[loginGame] 角色的登录出错，转换响应消息为null");
						return false;
					}

					eLoginResultType result = rsp.getResultType();
					if (result == eLoginResultType.FAIL || result == eLoginResultType.NO_ROLE) {
						RobotLog.fail("GameLoginHandler[loginGame] 角色的登录失败，服务器处理消息失败"+rsp.getError()+" result:"+result);
						return false;
					}

					client.setUserId(rsp.getUserId());
					RobotLog.info("GameLoginHandler[loginGame] 角色登录通过！  accountId:"+client.getAccountId()+" password:"+client.getPassword()+","+rsp.getUserId()+","+client);
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("GameLoginHandler[loginGame] accountId:"+client.getAccountId()+" password:"+client.getPassword(), e);
					return false;
				}
				return true;
			}

		});
		return success;
		
		
	}

}
