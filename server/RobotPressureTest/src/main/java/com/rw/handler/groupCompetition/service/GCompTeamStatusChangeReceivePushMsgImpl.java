package com.rw.handler.groupCompetition.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.RobotLog;
import com.rw.common.push.IReceivePushMsg;
import com.rwproto.GroupCompetitionProto.TeamStatusChange;
import com.rwproto.GroupCompetitionProto.TeamStatusType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GCompTeamStatusChangeReceivePushMsgImpl implements IReceivePushMsg {

	@Override
	public void onReceivePushMsg(Client client, Response resp) {
		try {
			TeamStatusChange status = TeamStatusChange.parseFrom(resp.getSerializedContent());
			if (status.getStatus() == TeamStatusType.CanMatch) {
				// 可匹配
				GroupCompetitionHandler.getHandler().sendStartMatching(client);
			}
		} catch (InvalidProtocolBufferException e) {
			RobotLog.fail("接收：MSG_GROUP_COMPETITION_TEAM_STATUS_CHANGE，转化数据失败！", e);
		}
	}

	@Override
	public Command getCommand() {
		return Command.MSG_GROUP_COMPETITION_TEAM_STATUS_CHANGE;
	}

}
