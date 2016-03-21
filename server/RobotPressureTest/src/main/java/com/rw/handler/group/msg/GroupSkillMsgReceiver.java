package com.rw.handler.group.msg;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.GroupSkillServiceProto.GroupSkillCommonRspMsg;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/*
 * @author HC
 * @date 2016年3月20日 上午12:45:50
 * @Description 帮派技能接受
 */
public class GroupSkillMsgReceiver extends PrintMsgReciver {
	public GroupSkillMsgReceiver(Command command, String functionName, String protoType) {
		super(command, functionName, protoType);
	}

	@Override
	public boolean execute(Client client, Response response) {
		ByteString bs = response.getSerializedContent();
		try {
			GroupSkillCommonRspMsg rsp = GroupSkillCommonRspMsg.parseFrom(bs);
			if (rsp == null) {
				RobotLog.fail(parseFunctionDesc() + "转换响应消息为null");
				return false;
			} else if (!rsp.getIsSuccess()) {
				RobotLog.info(parseFunctionDesc() + "失败" + (rsp.getTipMsg() != null ? ("。原因是：" + rsp.getTipMsg()) : ""));
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