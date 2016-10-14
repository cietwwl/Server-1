package com.rw.common.push;

import java.util.EnumMap;

import com.rw.Client;
import com.rw.handler.chat.ChatPushMsgHandler;
import com.rw.handler.groupCompetition.service.GCompTeamBattleResultReceivePushMsgImpl;
import com.rw.handler.groupCompetition.service.GCompTeamStatusChangeReceivePushMsgImpl;
import com.rw.handler.groupCompetition.service.GroupCompetitionReceivePushMsgImpl;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/**
 * @Author HC
 * @date 2016年8月8日 下午5:01:00
 * @desc
 **/

public class PushMsgHandlerFactory {
	private static PushMsgHandlerFactory instance = new PushMsgHandlerFactory();

	public static PushMsgHandlerFactory getFactory() {
		return instance;
	}

	private EnumMap<Command, IReceivePushMsg> msgMap = new EnumMap<Command, IReceivePushMsg>(Command.class);

	private PushMsgHandlerFactory() {
		registerPushMsgHandler(new ChatPushMsgHandler());
		registerPushMsgHandler(new GCompTeamStatusChangeReceivePushMsgImpl());
		registerPushMsgHandler(new GroupCompetitionReceivePushMsgImpl());
		registerPushMsgHandler(new GCompTeamBattleResultReceivePushMsgImpl());
	}

	/**
	 * 注册处理消息
	 * 
	 * @param command
	 * @param msgHandler
	 */
	public void registerPushMsgHandler(IReceivePushMsg msgHandler) {
		if (msgHandler == null) {
			return;
		}

		Command command = msgHandler.getCommand();
		if (msgMap.containsKey(command)) {
			return;
		}

		msgMap.put(command, msgHandler);
	}

	/**
	 * 获取消息的处理类
	 * 
	 * @param command
	 * @return
	 */
	private IReceivePushMsg getPushMsgHandler(Command command) {
		if (!msgMap.containsKey(command)) {
			return null;
		}

		return msgMap.get(command);
	}

	/**
	 * 当推送消息到达之后
	 * 
	 * @param command
	 * @param resp
	 */
	public void onMsgReceive(Client client, Response resp) {
		if (resp == null) {
			return;
		}

		Command command = resp.getHeader().getCommand();
		if (command.equals(Command.MSG_GROUP_COMPETITION_TEAM_REQ)) {
			System.out.println();
		}
		IReceivePushMsg handler = getPushMsgHandler(command);
		if (handler == null) {
			return;
		}

		handler.onReceivePushMsg(client, resp);
	}
}