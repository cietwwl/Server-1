package com.rw.handler.group.msg;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.GroupPrayProto.GroupPrayCommonRspMsg;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/**
 * @Author HC
 * @date 2016年12月26日 下午3:39:50
 * @desc
 **/

public class GroupPrayCommonMsgReceiver extends PrintMsgReciver {

	public GroupPrayCommonMsgReceiver(Command command, String functionName, String protoType) {
		super(command, functionName, protoType);
	}

	@Override
	public boolean execute(Client client, Response response) {
		try {
			GroupPrayCommonRspMsg rsp = GroupPrayCommonRspMsg.parseFrom(response.getSerializedContent());
			if (!rsp.getIsSuccess()) {// 失败了
				RobotLog.info(parseFunctionDesc() + "失败" + (rsp.getTipMsg() != null ? ("。原因是：" + rsp.getTipMsg()) : "") + " client.账号=" + client.getAccountId());
				return true;
			}

			return true;
		} catch (InvalidProtocolBufferException e) {
			RobotLog.fail("解析帮派祈福的响应消息出现了异常", e);
		}
		return true;
	}

	private String parseFunctionDesc() {
		return functionName + "[" + protoType + "] ";
	}
}