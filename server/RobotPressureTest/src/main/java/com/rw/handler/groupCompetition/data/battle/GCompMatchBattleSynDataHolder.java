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

	private volatile boolean matchFinish;// 显示是否匹配成功
	private volatile boolean randomMatching; // 是否在随机匹配中

	public void syn(MsgDataSyn synData) {
	}

	/**
	 * 当收到匹配数据的时候，直接请求开始战斗并发送战斗结果到服务器
	 * 
	 * @return
	 */
	public boolean sendGCOmpMatchBattleReq(Client client) {
		matchFinish = true;
		GCompMatchBattleHandler.getInstance().gcBattleStartReqHandler(client);// 请求战斗开始
		GCompMatchBattleHandler.getInstance().gcBattleEndReqHandler(client);// 请求战斗结束
		return true;
	}

	/**
	 * 是否在战斗中
	 * 
	 * @return
	 */
	public boolean isInitBattle() {
		return matchFinish;
	}

	/**
	 * 当有战斗结果的时候更新一下
	 * 
	 * @return
	 */
	public void reset() {
		matchFinish = false;
		randomMatching = false;
	}

	public boolean isRandomMatching() {
		return randomMatching;
	}

	public void setRandomMatching(boolean randomMatching) {
		this.randomMatching = randomMatching;
	}
}