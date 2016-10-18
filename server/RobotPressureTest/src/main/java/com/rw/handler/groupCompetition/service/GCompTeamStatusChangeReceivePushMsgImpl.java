package com.rw.handler.groupCompetition.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.RobotLog;
import com.rw.common.push.IReceivePushMsg;
import com.rwproto.GroupCompetitionProto.TeamStatusChange;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GCompTeamStatusChangeReceivePushMsgImpl implements IReceivePushMsg {

	@Override
	public void onReceivePushMsg(Client client, Response resp) {
		if(resp.getHeader().getSeqID() > 0) {
			return;
		}
		try {
			TeamStatusChange status = TeamStatusChange.parseFrom(resp.getSerializedContent());
			switch (status.getStatus()) {
			case BecomeLeader:
				client.getGCompTeamHolder().getTeam().setLeaderId(client.getUserId());
				client.getGCompTeamHolder().setTeamWaitingTimeout(System.currentTimeMillis() + GroupCompetitionHandler.maxTeamExistsTimemillis);
				break;
			case CanMatch:
				GroupCompetitionHandler.getHandler().sendStartMatching(client);
				break;
			default:
				break;
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
