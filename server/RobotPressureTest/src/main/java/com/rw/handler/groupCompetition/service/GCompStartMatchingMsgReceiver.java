package com.rw.handler.groupCompetition.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.GroupCompetitionProto.CommonRsp;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GCompStartMatchingMsgReceiver implements MsgReciver {

	@Override
	public Command getCmd() {
		return Command.MSG_GROUP_COMPETITION_TEAM_STATUS_REQ;
	}

	@Override
	public boolean execute(Client client, Response response) {
		try {
			CommonRsp rsp = CommonRsp.parseFrom(response.getSerializedContent());
			if (rsp == null) {
				RobotLog.fail("GroupCompetitionHandler[send] sendStartMatching转换响应消息为null");
				return false;
			}
			if (!rsp.getResultType().equals(GCResultType.SUCCESS)) {
				RobotLog.fail("GroupCompetitionHandler[send] sendStartMatching服务器返回不成功，提示信息：" + rsp.getTips());
				return true;
			} else {
				RobotLog.info("请求队伍匹配响应成功，userId：{}" + client.getUserId());
			}
		} catch (InvalidProtocolBufferException e) {
			RobotLog.fail("GroupCompetitionHandler[send] 失败", e);
			return false;
		}
		return false;
	}

}
