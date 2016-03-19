package com.rw.handler.task;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.StoreProtos.StoreResponse;
import com.rwproto.StoreProtos.eStoreResultType;
import com.rwproto.TaskProtos.TaskRequest;
import com.rwproto.TaskProtos.TaskResponse;
import com.rwproto.TaskProtos.eTaskRequestType;
import com.rwproto.TaskProtos.eTaskResultType;

public class TaskHandler {

	
	private static TaskHandler instance = new TaskHandler();
	public static TaskHandler instance(){
		return instance;
	}

	/**
	 * 创建角色
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean getReward(Client client) {
		

		TaskItem finishItem = client.getTaskItemHolder().getFinishItem();
		if(finishItem == null){
			return false;
		}
		TaskRequest.Builder req = TaskRequest.newBuilder()
										.setRequestType( eTaskRequestType.GetReward )
										.setId(finishItem.getTaskId());
		
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_TASK, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_TASK;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					
					TaskResponse rsp = TaskResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("TaskHandler[getReward] 转换响应消息为null");
						return false;
					}

					eTaskResultType result = rsp.getReslutType();
					if (result == eTaskResultType.SUCCESS) {
						RobotLog.info("TaskHandler[getReward] 购买成功");
						return true;
					}else{
						RobotLog.fail("TaskHandler[getReward] 服务器处理消息失败"+result);
						return false;
						
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("TaskHandler[getReward] 失败", e);
					return false;
				}
			}

		});
		return success;
	}

}
