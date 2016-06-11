package com.rw.handler.battletower.msg;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.BattleTowerServiceProtos.BattleTowerCommonRspMsg;
import com.rwproto.BattleTowerServiceProtos.EResponseState;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/*
 * @author HC
 * @date 2016年3月20日 下午2:32:45
 * @Description 试练塔数据
 */
public class BattleTowerMsgReceiver extends PrintMsgReciver {

	public BattleTowerMsgReceiver(Command command, String functionName, String protoType) {
		super(command, functionName, protoType);
	}

	@Override
	public boolean execute(Client client, Response response) {
		ByteString bs = response.getSerializedContent();
		try {
			BattleTowerCommonRspMsg rsp = BattleTowerCommonRspMsg.parseFrom(bs);
			if (rsp == null) {
				RobotLog.fail(parseFunctionDesc() + "转换响应消息为null");
				return false;
			} else if (rsp.getRspState() == EResponseState.RSP_FAIL) {
				RobotLog.info(parseFunctionDesc() + "失败" + (rsp.getTips() != null ? ("。原因是：" + rsp.getTips()) : ""));
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