package com.rw.service.group;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GroupPrayProto.GroupPrayCommonReqMsg;
import com.rwproto.GroupPrayProto.ReqType;
import com.rwproto.RequestProtos.Request;

/**
 * @Author HC
 * @date 2016年12月22日 下午5:59:15
 * @desc 帮派祈福的协议分发
 **/

public class GroupPrayService implements FsService<GroupPrayCommonReqMsg, ReqType> {

	@Override
	public ByteString doTask(GroupPrayCommonReqMsg request, Player player) {
		ReqType reqType = request.getReqType();
		switch (reqType) {
		case OPEN_MAIN_VIEW:// 打开界面
			return GroupPrayHandler.getHandler().openPrayMainViewHandler(player);
		case NEED_PRAY:// 请求祈福
			return GroupPrayHandler.getHandler().needPrayHandler(player, request);
		case SEND_PRAY:// 赠送给某个成员某张卡
			return GroupPrayHandler.getHandler().sendPrayHandler(player, request);
			// case GET_PRAY_REWARD:// 领取祈福的奖励
			// return GroupPrayHandler.getHandler().getPrayRewardHandler(player);
		default:// 没有找到协议号
			GameLog.error("帮派祈福分发协议", player.getUserId(), "出现了Unkown的协议号Id，不能处理");
			return null;
		}
	}

	@Override
	public GroupPrayCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		return GroupPrayCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
	}

	@Override
	public ReqType getMsgType(GroupPrayCommonReqMsg request) {
		return request.getReqType();
	}
}