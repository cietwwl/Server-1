package com.rw.handler.groupCompetition.data.battle;

import com.rw.Client;
import com.rw.handler.groupCompetition.service.GCompMatchBattleHandler;
import com.rwproto.DataSynProtos.MsgDataSyn;

/**
 * @Author HC
 * @date 2016年10月13日 下午2:52:45
 * @desc 机器人这里没必要拿匹配的数据，当收到这个消息的时候，过3秒就开始请求战斗就好了
 **/

public class GCompMatchBattleSynDataHolder {

	private static GCompMatchBattleSynDataHolder holder = new GCompMatchBattleSynDataHolder();

	public static GCompMatchBattleSynDataHolder getInstance() {
		return holder;
	}

	public void syn(MsgDataSyn synData) {
	}

	/**
	 * 当收到匹配数据的时候，直接请求开始战斗并发送战斗结果到服务器
	 * 
	 * @return
	 */
	public boolean sendGCOmpMatchBattleReq(Client client) {
		GCompMatchBattleHandler.getInstance().gcBattleStartReqHandler(client);// 请求战斗开始
		GCompMatchBattleHandler.getInstance().gcBattleEndReqHandler(client);// 请求战斗结束
		return true;
	}
}