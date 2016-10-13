package com.rw.handler.groupCompetition.service;

import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.push.IReceivePushMsg;
import com.rw.handler.groupCompetition.util.GCompUtil;
import com.rwproto.GroupCompetitionProto.JoinTeamReq;
import com.rwproto.GroupCompetitionProto.TeamInvitation;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupCompetitionReceivePushMsgImpl implements IReceivePushMsg {

	@Override
	public void onReceivePushMsg(Client client, Response resp) {
		try {
			TeamInvitation invitation = TeamInvitation.parseFrom(resp.getSerializedContent());
			if (invitation != null && client.getGCompTeamHolder().getTeam() == null) {
				List<String> heroIds = GCompUtil.getTeamHeroIds(client);
				JoinTeamReq.Builder builder = JoinTeamReq.newBuilder();
				builder.setTeamId(invitation.getTeamId());
				builder.addAllHeroId(heroIds);
				client.getMsgHandler().sendMsg(Command.MSG_GROUP_JOIN_TEAM_REQ, builder.build().toByteString(), new GCompJoinTeamMsgReceiver()); // 加入队伍
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Command getCommand() {
		return Command.MSG_GROUP_COMPETITION_TEAM_MEMBER_REQ;
	}

}
