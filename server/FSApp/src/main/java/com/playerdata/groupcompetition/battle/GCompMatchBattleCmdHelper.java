package com.playerdata.groupcompetition.battle;

import com.google.protobuf.ByteString;
import com.rwproto.GroupCompetitionBattleProto.GCBattleCommonRspMsg;
import com.rwproto.GroupCompetitionBattleProto.GCBattleReqType;
import com.rwproto.GroupCompetitionBattleProto.GCBattleResult;
import com.rwproto.GroupCompetitionBattleProto.GCPushBattleResultRspMsg;
import com.rwproto.GroupCompetitionBattleProto.GCPushHpInfoRspMsg;

/**
 * @Author HC
 * @date 2016年9月27日 下午8:31:05
 * @desc
 **/

public class GCompMatchBattleCmdHelper {

	/**
	 * 构造一个战斗结果的推送消息
	 * 
	 * @param index
	 * @param result
	 * @return
	 */
	public static ByteString buildPushBattleResultMsg(int index, GCBattleResult result) {
		// 构造一个战斗结果的消息
		GCBattleCommonRspMsg.Builder rsp = GCBattleCommonRspMsg.newBuilder();
		rsp.setReqType(GCBattleReqType.PUSH_BATTLE_RESULT);

		GCPushBattleResultRspMsg.Builder pushResult = GCPushBattleResultRspMsg.newBuilder();
		pushResult.setIndex(index);
		pushResult.setResult(result);
		rsp.setPushBattleResultRsp(pushResult);
		rsp.setIsSuccess(true);

		return rsp.build().toByteString();
	}

	/**
	 * 构造一个血量变化的推送消息
	 * 
	 * @param index
	 * @param myHpPercent
	 * @param enemyHpPercent
	 * @return
	 */
	public static ByteString buildPushHpInfoMsg(int index, float myHpPercent, float enemyHpPercent) {

		// 构造一个同步血量的消息
		GCBattleCommonRspMsg.Builder rsp = GCBattleCommonRspMsg.newBuilder();
		rsp.setReqType(GCBattleReqType.PUSH_HP_INFO);

		GCPushHpInfoRspMsg.Builder pushInfo = GCPushHpInfoRspMsg.newBuilder();
		pushInfo.setIndex(index);
		pushInfo.setMineHpPercent(myHpPercent);
		pushInfo.setEnemyHpPercent(enemyHpPercent);
		rsp.setPushHpInfoRsp(pushInfo);
		rsp.setIsSuccess(true);

		return rsp.build().toByteString();
	}
}