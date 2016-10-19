package com.rw.handler.groupCompetition.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.GroupCompetitionProto.CommonRsp;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GCompPersonalMatchingMsgReceiver implements MsgReciver {

	@Override
	public Command getCmd() {
		return Command.MSG_GROUP_COMPETITION_TEAM_REQ;
	}
	
	@Override
	public boolean execute(Client client, Response response) {
		ByteString seriallizedContent = response.getSerializedContent();
		try {
			CommonRsp rsp = CommonRsp.parseFrom(seriallizedContent);
			if (rsp == null) {
				RobotLog.fail("GroupCompetitionHandler[send] requestPersonalMatching转换响应消息为null");
				return false;
			}
			if (!rsp.getResultType().equals(GCResultType.SUCCESS)) {
				RobotLog.fail("GroupCompetitionHandler[send] requestPersonalMatching服务器返回不成功，提示信息：" + rsp.getTips());
				return true;
			} else {
				RobotLog.info("个人匹配响应成功，userId：" + client.getUserId());
				return true;
			}
		} catch(InvalidProtocolBufferException e) {
			RobotLog.fail("GroupCompetitionHandler[send] 失败", e);
			return false;
		}
	}

}
