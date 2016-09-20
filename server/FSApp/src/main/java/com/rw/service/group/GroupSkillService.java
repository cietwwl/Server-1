package com.rw.service.group;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.group.GroupDataVersionMgr;
import com.rw.service.FsService;
import com.rwproto.GroupCommonProto.RequestType;
import com.rwproto.GroupSkillServiceProto.GroupSkillCommonReqMsg;
import com.rwproto.RequestProtos.Request;

/*
 * @author HC
 * @date 2016年1月20日 上午11:47:40
 * @Description 帮派协议处理Server
 */
public class GroupSkillService implements FsService<GroupSkillCommonReqMsg, RequestType> {

	@Override
	public ByteString doTask(GroupSkillCommonReqMsg request, Player player) {
		// TODO Auto-generated method stub
		GroupSkillHandler skillHandler = GroupSkillHandler.getInstance();
		ByteString result = null;
		try {
			RequestType reqType = request.getReqType();
			switch (reqType) {
			case RESEARCH_GROUP_SKILL_TYPE:// 研究帮派技能
				result = skillHandler.researchGroupSkillHandler(player, request);
				break;
			case STUDY_GROUP_SKILL_TYPE:// 学习帮派技能
				result = skillHandler.studyGroupSkillHandler(player, request);
				break;
			default:
				GameLog.error(LogModule.GroupSkill.getName(), "分发协议Service", "接收到了一个Unknown的消息，无法处理");
				break;
			}
			GroupDataVersionMgr.synByVersion(player, request.getVersion());
		} catch (Exception e) {
			GameLog.error(LogModule.GroupSkill.getName(), "分发协议Service", "出现了Exception异常", e);
		}
		return result;
	}

	@Override
	public GroupSkillCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		GroupSkillCommonReqMsg commonReq = GroupSkillCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return commonReq;
	}

	@Override
	public RequestType getMsgType(GroupSkillCommonReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}