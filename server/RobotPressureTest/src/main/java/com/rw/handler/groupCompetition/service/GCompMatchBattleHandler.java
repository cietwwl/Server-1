package com.rw.handler.groupCompetition.service;

import com.rw.Client;
import com.rwproto.GroupCompetitionBattleProto.GCBattleCommonReqMsg;
import com.rwproto.GroupCompetitionBattleProto.GCBattleEndReqMsg;
import com.rwproto.GroupCompetitionBattleProto.GCBattleReqType;
import com.rwproto.GroupCompetitionBattleProto.GCBattleResult;
import com.rwproto.MsgDef.Command;

/**
 * @Author HC
 * @date 2016年10月13日 下午3:16:45
 * @desc 战斗的处理协议
 **/

public class GCompMatchBattleHandler {
	private static GCompMatchBattleHandler handler = new GCompMatchBattleHandler();

	public static GCompMatchBattleHandler getInstance() {
		return handler;
	}

	private GCompStartBattleMsgReceiver startMsgReceiver = new GCompStartBattleMsgReceiver();
	private GCompEndBattleMsgReceiver endMsgReceiver = new GCompEndBattleMsgReceiver();

	/**
	 * 请求战斗开始
	 * 
	 * @param client
	 */
	public void gcBattleStartReqHandler(Client client) {
		GCBattleCommonReqMsg.Builder req = GCBattleCommonReqMsg.newBuilder();
		req.setReqType(GCBattleReqType.BATTLE_START);
		client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION_BATTLE, req.build().toByteString(), startMsgReceiver);
	}

	/**
	 * 请求战斗结束
	 * 
	 * @param client
	 */
	public void gcBattleEndReqHandler(Client client) {
		GCBattleCommonReqMsg.Builder req = GCBattleCommonReqMsg.newBuilder();
		req.setReqType(GCBattleReqType.BATTLE_END);

		GCBattleEndReqMsg.Builder endReq = GCBattleEndReqMsg.newBuilder();
		endReq.setResult(GCBattleResult.WIN);

		req.setBattleEndReq(endReq);

		client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION_BATTLE, req.build().toByteString(), endMsgReceiver);
	}
}