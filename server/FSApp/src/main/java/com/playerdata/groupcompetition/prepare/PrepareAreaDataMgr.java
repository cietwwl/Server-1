package com.playerdata.groupcompetition.prepare;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.syn.WaitingQueue;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class PrepareAreaDataMgr {
	
	/**
	 * 等待数据同步的玩家
	 */
	private WaitingQueue<String> waitSynUser = new WaitingQueue<String>();
	/**
	 * 每个帮派一个备战区，备战开始时，统一创建Map存储<帮派id, 多人位置map>
	 * 某备战区内，保存多玩家位置信息
	 */
	private HashMap<String, ConcurrentHashMap<String, PositionInfo>> prepareAreaInfos;
	
	private final eSynType synType = eSynType.GC_PREPARE_POSITION;
	
	private static PrepareAreaDataMgr instance = new PrepareAreaDataMgr();

	public static PrepareAreaDataMgr getInstance() {
		return instance;
	}
	
	/**
	 * 同步备战区的位置信息
	 * 所有资源点的
	 * @param player
	 */
	public void synData(Player player, String synData){		
		ClientDataSynMgr.synData(player, synData, synType, eSynOpType.UPDATE_SINGLE);
	}
}
