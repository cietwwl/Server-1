package com.playerdata.groupcompetition.syn;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class DataAutoSynMgr {
	
	public static int SYN_COUNT_ONCE = 50;
	
	/**
	 * 等待数据同步的场景
	 */
	private WaitingQueue<Long> waitSynScene = new WaitingQueue<Long>();
	
	private static DataAutoSynMgr instance = new DataAutoSynMgr();

	public static DataAutoSynMgr getInstance() {
		return instance;
	}
	
	public void addWaitScene(Long sceneId){
		waitSynScene.addElement(sceneId);
	}
	
	/**
	 * 同步备战区的位置信息
	 * 所有资源点的
	 * @param player
	 */
	public void synDataAuto(){
		int synCount = 0;
		while(synCount < SYN_COUNT_ONCE){
			Long sceneId = waitSynScene.pollElement();
			if(null == sceneId){
				return;
			}
			Map<String, Object> synData = SameSceneContainer.getInstance().getSceneMembers(sceneId);
			if(null == synData || synData.isEmpty()){
				continue;
			}
			eSynType synType = SameSceneContainer.getInstance().getSceneSynType(sceneId);
			if(null == synType){
				continue;
			}
			String synStr = ClientDataSynMgr.toClientData(synData);
			Iterator<Entry<String, Object>> entryIterator = synData.entrySet().iterator();
			while(entryIterator.hasNext()){
				Entry<String, Object> entry = entryIterator.next();
				Player player = PlayerMgr.getInstance().findPlayerFromMemory(entry.getKey());
				if(null == player) entryIterator.remove();
				synCount++;
				ClientDataSynMgr.synData(player, synStr, synType, eSynOpType.UPDATE_SINGLE);
			}
		}
	}
}
