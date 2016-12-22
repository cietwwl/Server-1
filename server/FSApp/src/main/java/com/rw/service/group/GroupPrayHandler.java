package com.rw.service.group;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwproto.GroupPrayProto.GroupPrayCommonReqMsg;
import com.rwproto.GroupPrayProto.GroupPrayCommonRspMsg;
import com.rwproto.GroupPrayProto.ReqType;

/**
 * @Author HC
 * @date 2016年12月22日 下午5:59:30
 * @desc 帮派祈福的处理
 **/

public class GroupPrayHandler {
	private static GroupPrayHandler handler = new GroupPrayHandler();

	public static GroupPrayHandler getHandler() {
		return handler;
	}

	protected GroupPrayHandler() {
	}

	/**
	 * 打开祈福主界面的处理
	 * 
	 * @param player
	 * @return
	 */
	public ByteString openPrayMainViewHandler(Player player) {
		GroupPrayCommonRspMsg.Builder rsp = GroupPrayCommonRspMsg.newBuilder();
		rsp.setReqType(ReqType.OPEN_MAIN_VIEW);
		return rsp.build().toByteString();
	}

	/**
	 * 请求祈福的处理
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString needPrayHandler(Player player, GroupPrayCommonReqMsg req) {
		GroupPrayCommonRspMsg.Builder rsp = GroupPrayCommonRspMsg.newBuilder();
		rsp.setReqType(ReqType.NEED_PRAY);
		return rsp.build().toByteString();
	}

	/**
	 * 赠送某张卡给群成员的处理
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString sendPrayHandler(Player player, GroupPrayCommonReqMsg req) {
		GroupPrayCommonRspMsg.Builder rsp = GroupPrayCommonRspMsg.newBuilder();
		rsp.setReqType(ReqType.SEND_PRAY);
		return rsp.build().toByteString();
	}

	/**
	 * 获取祈福的奖励
	 * 
	 * @param player
	 * @return
	 */
	public ByteString getPrayRewardHandler(Player player) {
		GroupPrayCommonRspMsg.Builder rsp = GroupPrayCommonRspMsg.newBuilder();
		rsp.setReqType(ReqType.GET_PRAY_REWARD);
		return rsp.build().toByteString();
	}
}