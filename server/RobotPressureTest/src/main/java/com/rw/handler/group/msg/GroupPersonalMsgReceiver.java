package com.rw.handler.group.msg;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.GroupPersonalProto.GroupPersonalCommonRspMsg;
import com.rwproto.GroupPersonalProto.GroupRecommentRspMsg;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/*
 * @author HC
 * @date 2016年3月15日 下午6:02:19
 * @Description 
 */
public class GroupPersonalMsgReceiver extends PrintMsgReciver {
	public GroupPersonalMsgReceiver(Command command, String functionName, String protoType) {
		super(command, functionName, protoType);
	}

	@Override
	public boolean execute(Client client, Response response) {
		ByteString bs = response.getSerializedContent();
		try {
			GroupPersonalCommonRspMsg rsp = GroupPersonalCommonRspMsg.parseFrom(bs);
			if (rsp == null) {
				RobotLog.fail(parseFunctionDesc() + "转换响应消息为null");
				return false;
			} else if (!rsp.getIsSuccess()) {
				RobotLog.info(parseFunctionDesc() + "失败" + (rsp.getTipMsg() != null ? ("。原因是：" + rsp.getTipMsg()) : ""));
				return false;
			} else {
				GroupRecommentRspMsg groupRecommentRsp = rsp.getGroupRecommentRsp();
				if (groupRecommentRsp != null) {
					client.getGroupCacheData().setSimpleInfoList(groupRecommentRsp.getGroupSimpleInfoList());
				}
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