package com.rw.handler.groupCompetition.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.RobotLog;
import com.rw.common.push.IReceivePushMsg;
import com.rwproto.GroupCompetitionBattleProto.GCBattleCommonRspMsg;
import com.rwproto.GroupCompetitionBattleProto.GCBattleReqType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/**
 * @Author HC
 * @date 2016年10月14日 上午10:41:03
 * @desc
 **/

public class GCompTeamBattleResultReceivePushMsgImpl implements IReceivePushMsg {

	private static GCompTeamBattleResultReceivePushMsgImpl instance = new GCompTeamBattleResultReceivePushMsgImpl();

	public static GCompTeamBattleResultReceivePushMsgImpl getInstance() {
		return instance;
	}

	@Override
	public void onReceivePushMsg(Client client, Response resp) {
		ByteString seriallizedContent = resp.getSerializedContent();
		try {
			GCBattleCommonRspMsg rsp = GCBattleCommonRspMsg.parseFrom(seriallizedContent);
			if (rsp == null) {
				RobotLog.fail("GCompMatchBattleStart[receive] GCBattleStartRspMsg转换响应消息为null");
				return;
			}

			if (rsp.getReqType() != GCBattleReqType.PUSH_MEMBER_SCORE) {
				return;
			}

			if (!rsp.getIsSuccess()) {
				String tipMsg = rsp.getTipMsg();
				RobotLog.fail(String.format("GCompMatchBattleStart[receive] GCPushMemberScoreRspMsg处理结果为false  %s", (tipMsg == null || tipMsg.isEmpty()) ? "" : tipMsg));
				return;
			}

			client.getgCompMatchBattleSynDataHolder().reset();
		} catch (InvalidProtocolBufferException e) {
			RobotLog.fail("GCompMatchBattleStart[receive] GCPushMemberScoreRspMsg失败", e);
		}
	}

	@Override
	public Command getCommand() {
		return Command.MSG_GROUP_COMPETITION_BATTLE;
	}
}