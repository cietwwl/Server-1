package com.rw.handler.GroupCopy;

import com.google.protobuf.ByteString;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.GroupCopy.data.GroupCopyDataHolder;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdReqMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdRspMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyReqType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupCopyHandler {

	private static GroupCopyHandler handler = new GroupCopyHandler();
	private final static String functionName = "帮派副本";
	
	
	private GroupCopyHandler(){
	}
	
	private static GroupCopyHandler getInstance(){
		return handler;
	}
	
	private final static Command cmdAdm  = Command.MSG_GROUP_COPY_ADMIN;
	private final static Command cmdBattle = Command.MSG_GROUP_COPY_BATTLE;
	private final static Command cmdCom = Command.MSG_GROUP_COPY_CMD;
	
	
	
	public void openMainView(Client client){
		if(client == null){
			return;
		}

		GroupCopyCmdReqMsg.Builder req = GroupCopyCmdReqMsg.newBuilder();
		req.setReqType(GroupCopyReqType.GET_INFO);
		client.getMsgHandler().sendMsg(cmdCom, req.build().toByteString(), new PrintMsgReciver(cmdCom, functionName,"请求帮派副本主界面") {
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString bs = response.getSerializedContent();
				try {
					GroupCopyCmdRspMsg resp = GroupCopyCmdRspMsg.parseFrom(bs);
					if(resp.getIsSuccess()){
						RobotLog.info(parseFunctionDesc() + "成功");
						return true;
					}else{
						RobotLog.info(parseFunctionDesc() + "失败，当前机器人没有帮派");
						return false;
					}
					
				} catch (Exception e) {
					RobotLog.fail(parseFunctionDesc() + "失败，请求数据时出现异常!", e);
				}
				return false;
			}
			
			private String parseFunctionDesc() {
				return functionName + "[" + protoType + "] ";
			}
		});
	}
	
}
