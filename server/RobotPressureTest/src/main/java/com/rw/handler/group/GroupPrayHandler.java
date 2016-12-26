package com.rw.handler.group;

import com.rw.Client;
import com.rw.handler.RandomMethodIF;
import com.rw.handler.group.msg.GroupPrayOpenMainViewMsgReceiver;
import com.rwproto.GroupPrayProto.GroupPrayCommonReqMsg;
import com.rwproto.GroupPrayProto.ReqType;
import com.rwproto.MsgDef.Command;

/**
 * @Author HC
 * @date 2016年12月26日 下午3:08:08
 * @desc
 **/

public class GroupPrayHandler implements RandomMethodIF {

	private static GroupPrayHandler handler = new GroupPrayHandler();

	public static GroupPrayHandler getHandler() {
		return handler;
	}

	private GroupPrayOpenMainViewMsgReceiver openMainViewMsgReceiver = new GroupPrayOpenMainViewMsgReceiver(Command.MSG_GROUP_PRAY, "帮派祈福", "打开主界面");

	private GroupPrayHandler() {
	}

	/**
	 * 打开祈福的主界面
	 * 
	 * @param client
	 * @return
	 */
	public boolean openPrayMainViewHandler(Client client) {
		GroupPrayCommonReqMsg.Builder req = GroupPrayCommonReqMsg.newBuilder();
		req.setReqType(ReqType.OPEN_MAIN_VIEW);

		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_PRAY, req.build().toByteString(), openMainViewMsgReceiver);
	}

	/**
	 * 发起祈福的请求
	 * 
	 * @param client
	 * @return
	 */
	public boolean needGroupPrayHandler(Client client) {
		GroupPrayCommonReqMsg.Builder req = GroupPrayCommonReqMsg.newBuilder();
		req.setReqType(ReqType.NEED_PRAY);

		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_PRAY, req.build().toByteString(), null);
	}

	@Override
	public boolean executeMethod(Client client) {
		return openPrayMainViewHandler(client);
	}
}