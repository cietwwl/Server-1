package com.rw.service.groupsecret;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GroupSecretProto.GroupSecretCommonReqMsg;
import com.rwproto.GroupSecretProto.RequestType;
import com.rwproto.RequestProtos.Request;

/*
 * @author HC
 * @date 2016年5月26日 下午9:49:32
 * @Description 
 */
public class GroupSecretService implements FsService {

	@SuppressWarnings("finally")
	@Override
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		GroupSecretHandler handler = GroupSecretHandler.getHandler();
		try {
			GroupSecretCommonReqMsg req = GroupSecretCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
			RequestType reqType = req.getReqType();
			switch (reqType) {
			case OPEN_MAIN_VIEW:
				result = handler.openGroupSecretMainViewHandler(player);
				break;
			case CREATE_GROUP_SECRET:
				result = handler.createGroupSecretHandler(player, req.getCreateReqMsg());
				break;
			}
		} catch (Exception e) {
			GameLog.error("帮派秘境模块", player.getUserId(), "解析消息出现了错误", e);
		} finally {
			return result;
		}
	}
}