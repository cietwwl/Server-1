package com.playerdata.dataSyn.sameSceneSyn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.prepare.PositionInfo;
import com.playerdata.groupcompetition.prepare.PrepareAreaMgr;
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
				synCount += synData(sceneId, PrepareAreaMgr.synType, new SameSceneSynData());
			}
		}
	}
	
	/**
	 * 给一个场景内的所有玩家同步最新的数据（只同步有改变的数据）
	 * @param sceneId
	 * @param synType
	 * @param synObject
	 * @return
	 */
	private <T extends SameSceneDataBaseIF> int synData(long sceneId, eSynType synType, SameSceneSynDataIF synObject){
		int synCount = 0;
		Map<String, T> synData = SameSceneContainer.getInstance().getSceneMembers(sceneId);
		if(null == synData || synData.isEmpty() || sceneId <= 0){
			return 0;
		}
		//统计需要同步的玩家
		List<Player> players = new ArrayList<Player>();
		List<String> removedPlayers = new ArrayList<String>();
		List<String> newAddPlayers = new ArrayList<String>();
		Iterator<Entry<String, T>> entryIterator = synData.entrySet().iterator();
		while(entryIterator.hasNext()){
			Entry<String, T> entry = entryIterator.next();
			Player player = PlayerMgr.getInstance().findPlayerFromMemory(entry.getKey());
			if (null == player) {
				//把玩家标记为离开
				entry.getValue().setRemoved(true);
			}else{
				synCount++;
				players.add(player);
			}
			if(entry.getValue().isRemoved()){
				//元素是否被删除
				removedPlayers.add(entry.getKey());
				entryIterator.remove();
				//从场景中删除
				SameSceneContainer.getInstance().deleteUserFromScene(sceneId, entry.getKey());
			}else if(entry.getValue().isNewAdd()){
				//是否新添加
				newAddPlayers.add(entry.getKey());
			}else if(!entry.getValue().isChanged()){
				//元素没有改变
				entryIterator.remove();
			}
			entry.getValue().setNewAdd(false);
			entry.getValue().setChanged(false);
		}
		if(!players.isEmpty() && (!synData.isEmpty() || !newAddPlayers.isEmpty() || !removedPlayers.isEmpty())){
			//用来同步数据的结构
			synObject.setId(String.valueOf(sceneId));
			synObject.setSynData(synData);
			synObject.setAddMembers(newAddPlayers);
			synObject.setRemoveMembers(removedPlayers);
			//多个用户同步相同的数据
			ClientDataSynMgr.synDataMutiple(players, synObject, synType, eSynOpType.UPDATE_SINGLE);
		}
		return synCount;
	}
	
	/**
	 * 给一个玩家同步全部数据（包括旧的没改变的数据）
	 * @param player
	 * @param sceneId
	 * @param synType
	 * @param synObject
	 */
	public <T extends SameSceneDataBaseIF> void synDataToOnePlayer(Player player, long sceneId, eSynType synType, SameSceneSynDataIF synObject){
		Map<String, T> synData = SameSceneContainer.getInstance().getSceneMembers(sceneId);
		if(null == player || null == synData || synData.isEmpty() || sceneId <= 0){
			return;
		}
		Iterator<Entry<String, T>> entryIterator = synData.entrySet().iterator();
		while(entryIterator.hasNext()){
			Entry<String, T> entry = entryIterator.next();
			if(entry.getValue().isRemoved()){
				//元素是否被删除
				entryIterator.remove();
			}
		}
		//用来同步数据的结构
		synObject.setId(String.valueOf(sceneId));
		synObject.setSynData(synData);
		//多个用户同步相同的数据
		ClientDataSynMgr.synData(player, synObject, synType, eSynOpType.UPDATE_SINGLE);
	}
}
