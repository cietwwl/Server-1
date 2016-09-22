package com.playerdata.groupcompetition.service;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GroupCompetitionProto.TeamMemberRequest;
import com.rwproto.GroupCompetitionProto.TeamRequest;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;

public class GroupCompetitionEventsService implements FsService<GeneratedMessage, Command> {
	
	private static Map<Class<? extends GeneratedMessage>, Command> cmdMap = new HashMap<Class<? extends GeneratedMessage>,Command>();

	static {
		cmdMap.put(TeamRequest.class, Command.MSG_GROUP_COMPETITION_TEAM_REQ);
		cmdMap.put(TeamMemberRequest.class, Command.MSG_GROUP_COMPETITION_TEAM_MEMBER_REQ);
		cmdMap.put(TeamMemberRequest.class, Command.MSG_GROUP_COMPETITION_TEAM_STATUS_REQ);
	}
	
	@Override
	public ByteString doTask(GeneratedMessage request, Player player) {
		Command cmd = cmdMap.get(request.getClass());
		if (cmd != null) {
			switch (cmd) {
			case MSG_GROUP_COMPETITION_TEAM_REQ:
				break;
			case MSG_GROUP_COMPETITION_TEAM_MEMBER_REQ:
				break;
			case MSG_GROUP_COMPETITION_TEAM_STATUS_REQ:
				break;
			default:
				break;
			}
		}
		return null;
	}

	@Override
	public GeneratedMessage parseMsg(Request request) throws InvalidProtocolBufferException {
		switch (request.getHeader().getCommand()) {
		case MSG_GROUP_COMPETITION_TEAM_REQ:
			return TeamRequest.parseFrom(request.getBody().getSerializedContent());
		case MSG_GROUP_COMPETITION_TEAM_MEMBER_REQ:
			return TeamMemberRequest.parseFrom(request.getBody().getSerializedContent());
		case MSG_GROUP_COMPETITION_TEAM_STATUS_REQ:
			return TeamMemberRequest.parseFrom(request.getBody().getSerializedContent());
		default:
			return null;
		}
	}

	@Override
	public Command getMsgType(GeneratedMessage request) {
		return cmdMap.get(request.getClass());
	}
}
