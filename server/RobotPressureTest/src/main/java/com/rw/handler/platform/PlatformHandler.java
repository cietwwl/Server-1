package com.rw.handler.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.config.PlatformConfig;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.ClientInfo;
//import com.rw.ClientPool;
import com.rw.account.ServerInfo;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.dataSyn.JsonUtil;
import com.rwproto.AccountLoginProtos.AccountInfo;
import com.rwproto.AccountLoginProtos.AccountLoginRequest;
import com.rwproto.AccountLoginProtos.AccountLoginResponse;
import com.rwproto.AccountLoginProtos.UserInfo;
import com.rwproto.AccountLoginProtos.ZoneInfo;
import com.rwproto.AccountLoginProtos.eAccountLoginType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.AccountLoginProtos;

public class PlatformHandler {
	
	private static PlatformHandler instance = new PlatformHandler();
	public static PlatformHandler instance(){
		return instance;
	}

	/**
	 * 注册用户
	 */
	public Client reg(String accountId) {
		Client client = newClient(accountId);
	
		System.err.printf("[%s]用户的注册开始....\n", accountId);		
		AccountLoginRequest.Builder loginReq = AccountLoginRequest.newBuilder();
		loginReq.setLoginType(eAccountLoginType.ACCOUNT_LOGIN);
		AccountInfo.Builder accountInfo = AccountInfo.newBuilder();
		accountInfo.setAccountId(client.getAccountId());
		accountInfo.setPassword(client.getPassword());
		ClientInfo clientInfo = new ClientInfo();
		clientInfo.setAccountId(accountId);
		accountInfo.setClientInfoJson(JsonUtil.writeValue(clientInfo));
		loginReq.setAccount(accountInfo);
		
		
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_LOGIN_PLATFORM, loginReq.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_LOGIN_PLATFORM;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					AccountLoginResponse rsp = AccountLoginResponse.parseFrom(serializedContent);
					AccountLoginProtos.eLoginResultType resultType = rsp.getResultType();
					if(resultType == AccountLoginProtos.eLoginResultType.SUCCESS){
						accountLoginSuccess(client, rsp);
						return true;
					}else{
						RobotLog.fail("PlatformHandler[registerUser]  accountId:"+client.getAccountId()+" server return not success, resultType:"+resultType+";error:"+rsp.getError());
						
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("PlatformHandler[registerUser] accountId:"+client.getAccountId()+" exception", e);
				}
				return false;
			}
			
			
		});
		if(success){
			accountId = client.getAccountId();
			RobotLog.info("user Created accountId:"+client.getAccountId()+" password:"+client.getPassword());
		}else{
//			ClientPool.remove(client.getAccountId());
			client = null;
		}
		
		return client;
	}
	
	private Client newClient(String accountId){
		Client client = new Client(accountId);
		// 注册
		
		if (!client.doConnect(PlatformConfig.getPlatformHost(), PlatformConfig.getPlatformPort())) {
			RobotLog.fail("连接登陆服连接失败！退出注册用户步骤 host:"+PlatformConfig.getPlatformHost()+" port:"+PlatformConfig.getPlatformPort());
			return null;
		}
		return client;
	}

	/**
	 * 登录验证
	 * 
	 * @param c
	 */
	public Client login(String accountId ) {
		
		Client client = newClient(accountId);
		if(client == null){
			return null;
		}
		
		System.err.printf("[%s]用户的验证开始....\n", accountId );
		AccountLoginRequest.Builder req = AccountLoginRequest.newBuilder();
		req.setLoginType(eAccountLoginType.ACCOUNT_LOGIN);
		AccountInfo.Builder accountInfo = AccountInfo.newBuilder();
		accountInfo.setAccountId(accountId);
		accountInfo.setPassword(client.getPassword());
		req.setAccount(accountInfo);
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_LOGIN_PLATFORM, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_LOGIN_PLATFORM;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					AccountLoginResponse rsp = AccountLoginResponse.parseFrom(serializedContent);
					AccountLoginProtos.eLoginResultType resultType = rsp.getResultType();
					if(resultType == AccountLoginProtos.eLoginResultType.SUCCESS){
						accountLoginSuccess(client, rsp);
						return true;
					}else{
						RobotLog.fail("PlatformHandler[loginVerify]  accountId:"+client.getAccountId()+" server return not success, resultType:"+resultType);
					}
					
				} catch (Exception e) {
					RobotLog.fail("PlatformHandler[loginVerify] accountId:"+client.getAccountId()+" exception", e);
				}
				return false;
			}

		});
		if(success){
			return client;
		}
		return null;

	}
	private Client accountLoginSuccess(Client client, AccountLoginResponse rsp) {
		AccountInfo account = rsp.getAccount();
		String accountId = account.getAccountId();
		String password = account.getPassword();
		
		ZoneInfo lastZone = rsp.getLastZone();
		String port = lastZone.getPort();
		String serverIp = lastZone.getServerIp();
		
		// 设置角色信息
		client.setAccountId(accountId);
		client.setLastServerId(lastZone.getZoneId());
		ServerInfo serverInfo = new ServerInfo();
		serverInfo.setZoneId(lastZone.getZoneId());
		serverInfo.setServerIP(serverIp);
		serverInfo.setServerPort(port);
		client.addServerInfo(serverInfo);
//		ClientPool.put(client);
		RobotLog.info("PlatformHandler[execute] 验证或者创建成功, accoutId:"+accountId+" password:"+password+" lastZondId:"+lastZone.getZoneId() +" address:"+serverIp+":"+port);
		return client;
	}
	

	/**
	 * 获取所有的区服，以及账户下在那些服务器有帐号
	 * 
	 * @param c
	 */
	public boolean loadZoneAndRoleList(Client client) {
		
		
		String accountId = client.getAccountId();
		System.err.printf("[%s]要获取所有的服务器列表开始\n", accountId );
		AccountLoginRequest.Builder req = AccountLoginRequest.newBuilder();
		req.setLoginType(eAccountLoginType.ZONE_LIST);
		AccountInfo.Builder accountInfo = AccountInfo.newBuilder();
		accountInfo.setAccountId(accountId);
		req.setAccount(accountInfo);

		boolean success = client.getMsgHandler().sendMsg( Command.MSG_LOGIN_PLATFORM, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_LOGIN_PLATFORM;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					AccountLoginResponse rsp = AccountLoginResponse.parseFrom(serializedContent);
					com.rwproto.AccountLoginProtos.eLoginResultType resultType = rsp.getResultType();
					if(resultType == com.rwproto.AccountLoginProtos.eLoginResultType.SUCCESS){
						if(handleZoneResp(client, rsp)){
							
							RobotLog.info("PlatformHandler[loadZoneAndRoleList] 获取服务器的所有列表成功！！");
							return true;
						}else{
							RobotLog.fail("PlatformHandler[loadZoneAndRoleList] 失败，服务器处理消息失败!accountId:"+client.getAccountId());
						}
					}else{
						RobotLog.fail("PlatformHandler[loadZoneAndRoleList] 失败，服务器处理消息失败!accountId:"+client.getAccountId());
					}
					
				} catch (Exception e) {
					RobotLog.fail("PlatformHandler[loadZoneAndRoleList] accountId:"+client.getAccountId()+" password:"+client.getPassword(), e);
				}
				return false;
			}
			
			public boolean handleZoneResp(Client client, AccountLoginResponse rsp) {

				// 区服列表
				List<ZoneInfo> zoneList = rsp.getZoneListList();
				int size = zoneList.size();

				Map<Integer, ServerInfo> serverMap = new HashMap<Integer, ServerInfo>(size);

				for (int i = 0; i < size; i++) {
					ZoneInfo zi = zoneList.get(i);

					ServerInfo si = new ServerInfo();
					si.setZoneId(zi.getZoneId());
					si.setServerIP(zi.getServerIp());
					si.setServerPort(zi.getPort());

					serverMap.put(zi.getZoneId(), si);
					 System.err.printf("可用的服务器连接[%s][%s][%s][%s]\n", zi.getZoneName(), zi.getZoneId(), zi.getServerIp(), zi.getPort());
				}

				// 用户数据
				List<UserInfo> userList = rsp.getUserListList();
				for (UserInfo userInfo : userList) {
					ZoneInfo zoneInfo = userInfo.getZoneInfo();
					int serverId = zoneInfo.getZoneId();
					ServerInfo serverInfo = serverMap.get(serverId);
					if (serverInfo != null) {
						serverInfo.setHasRole(true);
					}
				}

				client.setServerList(new ArrayList<ServerInfo>(serverMap.values()));
				return !client.getServerList().isEmpty();
			}

		});
		
		return success;
		
	}
}
