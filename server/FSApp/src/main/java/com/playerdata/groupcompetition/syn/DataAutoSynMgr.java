package com.playerdata.groupcompetition.syn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class DataAutoSynMgr {
	
	/**
	 * 每次同步推的人数（或者是循环查找的最大次数）
	 */
	public static int SYN_COUNT_ONCE = 50;
	/**
	 * 等待数据同步的场景
	 */
	private WaitingQueue<Long> waitSynScene = new WaitingQueue<Long>();
	
	private static DataAutoSynMgr instance = new DataAutoSynMgr();

	public static DataAutoSynMgr getInstance() {
		return instance;
	}
	
	/**
	 * 添加一个等待同步的场景
	 * @param sceneId
	 */
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
		int loopCount = 0;
		while(synCount < SYN_COUNT_ONCE && loopCount++ < SYN_COUNT_ONCE){
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
			//用来同步数据的结构
			SameSceneSynData synObject = new SameSceneSynData();
			synObject.setId(String.valueOf(sceneId));
			synObject.setSynData(synData);
			List<Player> players = new ArrayList<Player>();
			Iterator<Entry<String, Object>> entryIterator = synData.entrySet().iterator();
			while(entryIterator.hasNext()){
				Entry<String, Object> entry = entryIterator.next();
				Player player = PlayerMgr.getInstance().findPlayerFromMemory(entry.getKey());
				if(null == player){
					//获取的map是单独创建的，所以，这里没有用迭代的remove删除
					SameSceneContainer.getInstance().removeUserFromScene(sceneId, entry.getKey());
					continue;
				}
				synCount++;
				players.add(player);
			}
			if(!players.isEmpty()){
				//多个用户同步相同的数据
				ClientDataSynMgr.synDataMutiple(players, synObject, synType, eSynOpType.UPDATE_SINGLE);
			}
		}
	}
}
