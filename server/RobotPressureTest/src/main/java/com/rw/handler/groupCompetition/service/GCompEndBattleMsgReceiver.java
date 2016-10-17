package com.rw.handler.groupCompetition.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.GroupCompetitionBattleProto.GCBattleCommonRspMsg;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/**
 * @Author HC
 * @date 2016年10月13日 下午3:05:09
 * @desc 接受战斗结束的消息
 **/

class GCompEndBattleMsgReceiver implements MsgReciver {

	@Override
	public Command getCmd() {
		return Command.MSG_GROUP_COMPETITION_BATTLE;
	}

	@Override
	public boolean execute(Client client, Response response) {
		ByteString seriallizedContent = response.getSerializedContent();
		try {
			GCBattleCommonRspMsg rsp = GCBattleCommonRspMsg.parseFrom(seriallizedContent);

			if (rsp == null) {
				RobotLog.fail("GCompMatchBattleEnd[receive] GCBattleEndReqMsg转换响应消息为null");
				return false;
			}

			if (!rsp.getIsSuccess()) {
				String tipMsg = rsp.getTipMsg();
				RobotLog.fail(String.format("GCompMatchBattleEnd[receive] GCBattleEndReqMsg处理结果为false  %s", (tipMsg == null || tipMsg.isEmpty()) ? "" : tipMsg));
			}
		} catch (InvalidProtocolBufferException e) {
			RobotLog.fail("GCompMatchBattleEnd[receive] 失败", e);
			return false;
		}
		return true;
	}
}