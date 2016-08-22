package com.playerdata.groupcompetition.syn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.holder.PrepareAreaDataHolder;
import com.playerdata.groupcompetition.prepare.PositionInfo;
import com.playerdata.groupcompetition.prepare.SameSceneSynData;
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
			SameSceneDataBaseIF oneValue = SameSceneContainer.getInstance().checkType(sceneId);
			if(null == oneValue){
				continue;
			}
			if(oneValue instanceof PositionInfo){				
				synCount += synData(sceneId, PrepareAreaDataHolder.synType, new SameSceneSynData());
			}
		}
	}
	
	public <T extends SameSceneDataBaseIF> int synData(long sceneId, eSynType synType, SameSceneSynDataIF synObject){
		int synCount = 0;
		Map<String, T> synData = SameSceneContainer.getInstance().getSceneMembers(sceneId);
		if(null == synData || synData.isEmpty() || sceneId <= 0){
			return 0;
		}
		//统计需要同步的玩家
		List<Player> players = new ArrayList<Player>();
		Iterator<Entry<String, T>> entryIterator = synData.entrySet().iterator();
		while(entryIterator.hasNext()){
			Entry<String, T> entry = entryIterator.next();
			Player player = PlayerMgr.getInstance().findPlayerFromMemory(entry.getKey());
			if(null == player){
				//获取的map是单独创建的，所以，这里没有用迭代的remove删除
				SameSceneContainer.getInstance().removeUserFromScene(sceneId, entry.getKey());
				continue;
			}
			synCount++;
			players.add(player);
			//删除没有变动的元素，不多余同步，把元素重新置为未变动
			if(!entry.getValue().isChanged()){
				entryIterator.remove();
			}else{
				entry.getValue().setChanged(false);
			}
		}
		if(!players.isEmpty() && !synData.isEmpty()){
			//用来同步数据的结构
			synObject.setId(String.valueOf(sceneId));
			synObject.setSynData(synData);
			//多个用户同步相同的数据
			ClientDataSynMgr.synDataMutiple(players, synObject, synType, eSynOpType.UPDATE_SINGLE);
		}
		return synCount;
	}
}
