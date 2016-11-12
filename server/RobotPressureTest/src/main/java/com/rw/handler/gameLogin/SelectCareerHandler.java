package com.rw.handler.gameLogin;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.player.UserGameData;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.RoleServiceProtos.RoleRequest;
import com.rwproto.RoleServiceProtos.RoleRequestType;
import com.rwproto.RoleServiceProtos.RoleResponse;
import com.rwproto.RoleServiceProtos.RoleResultType;

public class SelectCareerHandler {
	
	private static SelectCareerHandler instance = new SelectCareerHandler();
	public static SelectCareerHandler instance(){
		return instance;
	}


	/**
	 * 创建角色
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean select(Client client ) {
		UserGameData userGameData = client.getUserGameDataHolder().getUserGameData();
		if(userGameData.getCarrerChangeTime() > 0){
			return true;
		}
		
		RoleRequest.Builder req = RoleRequest.newBuilder();
		req.setRequestType( RoleRequestType.SELECT_CAREER);
		req.setCareerType(2);
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_ROLE, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_ROLE;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					RoleResponse rsp = RoleResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("SelectCareerHandler[select] 转换响应消息为null");
						
						return false;
					}

					RoleResultType result = rsp.getResult();
					if (result == RoleResultType.FAIL) {
						RobotLog.fail("SelectCareerHandler[select] 服务器处理消息失败:"+result);
						return false;
					}

					RobotLog.info("SelectCareerHandler[select] success  accountId:"+client.getAccountId()+" password:"+client.getPassword());
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("SelectCareerHandler[select] fail accountId:"+client.getAccountId()+" password:"+client.getPassword(), e);
					return false;
				}
				return true;
			}

		});
		return success;
		
		
	}


}
