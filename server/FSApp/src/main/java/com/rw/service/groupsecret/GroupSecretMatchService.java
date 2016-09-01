package com.rw.service.groupsecret;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwbase.dao.groupsecret.GroupSecretVersionMgr;
import com.rwproto.GroupSecretMatchProto.GroupSecretMatchCommonReqMsg;
import com.rwproto.GroupSecretMatchProto.MatchRequestType;
import com.rwproto.RequestProtos.Request;

/*
 * @author HC
 * @date 2016年6月2日 下午4:25:22
 * @Description 
 */
public class GroupSecretMatchService implements FsService<GroupSecretMatchCommonReqMsg, MatchRequestType> {

	@SuppressWarnings("finally")
	@Override
	public ByteString doTask(GroupSecretMatchCommonReqMsg request, Player player) {
		// TODO Auto-generated method stub
		GroupSecretMatchHandler handler = GroupSecretMatchHandler.getHandler();
		ByteString result = null;
		try {
			MatchRequestType reqType = request.getReqType();
			switch (reqType) {
			case SEARCHING_ENEMY:
				result = handler.getSerchingEnemyHandler(player);
				break;
			case ATTACK_ENEMY_START:
				result = handler.attackEnemyStartHandler(player, request.getAttackStartReq());
				break;
			case ATTACK_ENEMY_END:
				result = handler.attackEnemyEndHandler(player, request.getAttackEndReq());
				break;
			case GET_REWARD:
				result = handler.getRobRewardHandler(player);
				break;
			default:
				GameLog.error("帮派秘境探索模块", player.getUserId(), "不知道客户端申请了什么鬼协议，反正这里解析不了！！！");
				break;
			}
			GroupSecretVersionMgr.synByVersion(player, request.getVersion());
		} catch (Exception e) {
			GameLog.error("帮派秘境模块", player.getUserId(), "解析消息出现了错误", e);
		} finally {
			return result;
		}
	}

	@Override
	public GroupSecretMatchCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		GroupSecretMatchCommonReqMsg req = GroupSecretMatchCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public MatchRequestType getMsgType(GroupSecretMatchCommonReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}