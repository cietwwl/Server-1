package com.rw.handler.groupCompetition.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.GroupCompetitionProto.CommonRsp;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GCompLeaveTeamMsgReceiver implements MsgReciver {

	@Override
	public Command getCmd() {
		return Command.MSG_GROUP_COMPETITION_TEAM_STATUS_REQ;
	}

	@Override
	public boolean execute(Client client, Response response) {
		if(response.getHeader().getSeqID() > 0) {
			try {
				CommonRsp rsp = CommonRsp.parseFrom(response.getSerializedContent());
				if (rsp == null) {
					RobotLog.fail("GroupCompetitionHandler[send] requestLeaveTeam转换响应消息为null");
					return false;
				}
				if (!rsp.getResultType().equals(GCResultType.SUCCESS)) {
					RobotLog.fail("GroupCompetitionHandler[send] requestLeaveTeam服务器返回不成功，提示消息：" + rsp.getTips());
					return true;
				} else {
					client.getGCompTeamHolder().clearTeam();
					return true;
				}
			} catch (InvalidProtocolBufferException e) {
				RobotLog.fail("GroupCompetitionHandler[send] 失败", e);
				return false;
			}
		} else {
			return true;
		}
	}

}