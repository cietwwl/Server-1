package com.rw.service.skill;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.SkillServiceProtos.SkillEventType;
import com.rwproto.SkillServiceProtos.SkillRequest;

public class SkillService implements FsService<SkillRequest, SkillEventType> {
	private SkillHandler skillHandler = SkillHandler.getInstance();

	@Override
	public ByteString doTask(SkillRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {

			switch (request.getEventType()) {
			case QUERY_SKILL_INFO:
				result = skillHandler.querySkillInfo(player);
				break;
			case Skill_Upgrade:
				result = skillHandler.updateSkill(player, request.getHeroId(), request.getUpdateSkillListList());
				break;
			case Buy_Skill_Point:
				result = skillHandler.buySkillPoint(player);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public SkillRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		SkillRequest skillReq = SkillRequest.parseFrom(request.getBody().getSerializedContent());
		return skillReq;
	}

	@Override
	public SkillEventType getMsgType(SkillRequest request) {
		// TODO Auto-generated method stub
		return request.getEventType();
	}
}
