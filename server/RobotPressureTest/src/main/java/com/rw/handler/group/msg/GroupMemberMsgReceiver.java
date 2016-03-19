package com.rw.handler.group.msg;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.GroupMemberMgrProto.GroupMemberMgrCommonRspMsg;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/*
 * @author HC
 * @date 2016年3月15日 下午6:01:09
 * @Description 
 */
public class GroupMemberMsgReceiver extends PrintMsgReciver {
	public GroupMemberMsgReceiver(Command command, String functionName, String protoType) {
		super(command, functionName, protoType);
	}

	@Override
	public boolean execute(Client client, Response response) {
		ByteString bs = response.getSerializedContent();
		try {
			GroupMemberMgrCommonRspMsg rsp = GroupMemberMgrCommonRspMsg.parseFrom(bs);
			if (rsp == null) {
				RobotLog.fail(parseFunctionDesc() + "转换响应消息为null");
				return false;
			} else {
				RobotLog.info(parseFunctionDesc() + "成功");
				return true;
			}
		} catch (InvalidProtocolBufferException e) {
			RobotLog.fail(parseFunctionDesc() + "失败", e);
		}
		return false;
	}

	private String parseFunctionDesc() {
		return functionName + "[" + protoType + "] ";
	}
}