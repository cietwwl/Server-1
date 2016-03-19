package com.rw.service.skill;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.SkillServiceProtos.SkillRequest;

public class SkillService implements FsService {
	private SkillHandler skillHandler = SkillHandler.getInstance();

	public ByteString doTask(Request request, Player player) {

		ByteString result = null;
		try {
			SkillRequest skillReq = SkillRequest.parseFrom(request.getBody().getSerializedContent());

			switch (skillReq.getEventType()) {
			case QUERY_SKILL_INFO:
				result = skillHandler.querySkillInfo(player);
				break;
			case Skill_Upgrade:
				result = skillHandler.updateSkill(player, skillReq.getHeroId(), skillReq.getUpdateSkillListList());
				break;
			case Buy_Skill_Point:
				result = skillHandler.buySkillPoint(player);
				break;
			default:
				break;
			}

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}

		return result;
	}
}
