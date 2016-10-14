package com.playerdata.dataSyn.sameSceneSyn;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.netty.UserChannelMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class DataAutoSynMgr {
	
	/**
	 * 每次同步推的人数（或者是循环查找的最大次数）
	 */
	public static int SYN_COUNT_ONCE = 60;
	/**
	 * 等待数据同步的场景
	 */
	private WaitingQueue<Long> waitSynScene = new WaitingQueue<Long>();
	/**
	 * 等待删除的场景
	 */
	private WaitingQueue<Long> waitRemoveScene = new WaitingQueue<Long>();
	
	private static DataAutoSynMgr instance = new DataAutoSynMgr();

	public static DataAutoSynMgr getInstance() {
		return instance;
	}
	
	/**
	 * 添加一个等待同步的场景
	 * @param sceneId
	 */
	void addWaitScene(Long sceneId){
		waitSynScene.addElement(sceneId);
	}
	
	/**
	 * 添加一个等待删除的场景
	 * @param sceneId
	 */
	void addRemoveScene(Long sceneId){
		waitRemoveScene.addElement(sceneId);
	}
	
	/**
	 * 同步备战区的位置信息
	 * 所有资源点的
	 */
	public void synDataAuto(){
		int synCount = synRemoveData();
		int loopCount = 0;
		while(synCount < SYN_COUNT_ONCE && loopCount++ < SYN_COUNT_ONCE){
			Long sceneId = waitSynScene.pollElement();
			if(null == sceneId){
				return;
			}
			SameSceneDataBaseIF oneValue = SameSceneContainer.getInstance().checkType(sceneId);
			SameSceneType sceneType = SameSceneType.getEnum(oneValue);
			if(null != sceneType){
				SameSceneSynDataIF dataIF = sceneType.getSynDataObject();
				if(null != dataIF){
					synCount += synData(sceneId, sceneType.getSynType(), dataIF);
				}
			}
		}
	}
	
	/**
	 * 同步已经删除的同屏数据
	 */
	private int synRemoveData(){
		int synCount = 0;
		int loopCount = 0;
		while(synCount < SYN_COUNT_ONCE && loopCount++ < SYN_COUNT_ONCE){
			Long sceneId = waitRemoveScene.pollElement();
			if(null == sceneId){
				break;
			}
			SameSceneDataBaseIF oneValue = SameSceneContainer.getInstance().checkType(sceneId);
			SameSceneType sceneType = SameSceneType.getEnum(oneValue);
			if(null != sceneType){
				SameSceneSynDataIF dataIF = sceneType.getSynDataObject();
				if(null != dataIF){
					synCount += synRemoveScene(sceneId, sceneType.getSynType(), dataIF);
					SameSceneContainer.getInstance().removeScene(sceneId);
				}
			}
		}
		return synCount;
	}
	
	/**
	 * 给一个场景内的所有玩家同步最新的数据（只同步有改变的数据）
	 * @param sceneId
	 * @param synType
	 * @param synObject
	 * @return
	 */
	private <T extends SameSceneDataBaseIF> int synData(long sceneId, eSynType synType, SameSceneSynDataIF synObject){
		Map<String, T> synData = SameSceneContainer.getInstance().getSceneSynMembers(sceneId);
		if(null == synData || synData.isEmpty() || sceneId <= 0){
			return 0;
		}
		//统计需要同步的玩家
		List<Player> players = new ArrayList<Player>();
		List<String> removedPlayers = new ArrayList<String>();
		List<String> newAddPlayers = new ArrayList<String>();
		Iterator<Entry<String, T>> entryIterator = synData.entrySet().iterator();
		long synTime = System.currentTimeMillis();
		while(entryIterator.hasNext()){
			Entry<String, T> entry = entryIterator.next();
			ChannelHandlerContext ctx = UserChannelMgr.get(entry.getKey());
			if(entry.getValue() == null){
				//value为null说明是给子场景中的人同步主场景中的数据
				if (ctx != null) {
					Player player = PlayerMgr.getInstance().findPlayerFromMemory(entry.getKey());
					if(null != player){
						players.add(player);
					}
				}
				continue;
			}
			if (ctx == null) {
				if(entry.getValue().isDisConn(synTime)){
					//把玩家标记为离开
					entry.getValue().setRemoved(true);
				}else{
					entry.getValue().setDisConnTime(synTime);
				}
			}else{
				entry.getValue().setDisConnTime(0);
			}
			//判断玩家的三种状态
			if(entry.getValue().isRemoved()){
				//元素是否被删除
				removedPlayers.add(entry.getKey());
				entryIterator.remove();
				//从场景中删除
				SameSceneContainer.getInstance().deleteUserFromScene(sceneId, entry.getKey());
			}else{
				if(entry.getValue().isNewAdd()){
					//是否新添加
					newAddPlayers.add(entry.getKey());
				}else if(!entry.getValue().isChanged()){
					//元素没有改变
					entryIterator.remove();
				}
				Player player = PlayerMgr.getInstance().findPlayerFromMemory(entry.getKey());
				players.add(player);
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
		return players.size();
	}
	
	/**
	 * 移除一个场景（存在的合法时间已到）
	 * @param sceneId
	 * @param synType
	 * @param synObject 其中之会有需要删除的玩家id(如果id中包含自己，就退出同屏界面)
	 * @return
	 */
	private <T extends SameSceneDataBaseIF> int synRemoveScene(long sceneId, eSynType synType, SameSceneSynDataIF synObject){
		Map<String, T> synData = SameSceneContainer.getInstance().getSceneSynMembers(sceneId);
		if(null == synData || synData.isEmpty() || sceneId <= 0){
			return 0;
		}
		List<Player> players = new ArrayList<Player>();
		List<String> removedPlayers = new ArrayList<String>();
		Iterator<Entry<String, T>> entryIterator = synData.entrySet().iterator();
		while(entryIterator.hasNext()){
			Entry<String, T> entry = entryIterator.next();
			Player player = PlayerMgr.getInstance().findPlayerFromMemory(entry.getKey());
			if (null != player) {
				players.add(player);
				removedPlayers.add(player.getUserId());
			}
		}
		if(!players.isEmpty()){
			//用来同步数据的结构
			synObject.setId(String.valueOf(sceneId));
			synObject.setRemoveMembers(removedPlayers);
			//多个用户同步相同的数据
			ClientDataSynMgr.synDataMutiple(players, synObject, synType, eSynOpType.UPDATE_SINGLE);
		}
		return players.size();
	}
	
	/**
	 * 给一个玩家自己场景同步全部数据（包括旧的没改变的数据）
	 * @param player
	 * @param sceneId
	 * @param synType
	 * @param synObject
	 */
	public <T extends SameSceneDataBaseIF> void synDataToOnePlayer(Player player, long sceneId, eSynType synType, SameSceneSynDataIF synObject){
		Map<String, T> synData = SameSceneContainer.getInstance().getExistMembers(sceneId);
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
	
	/**
	 * 给同屏所有玩家同步某个数据
	 * @param sceneId
	 * @param synType
	 * @param synObject
	 */
	public void synDataToPlayersInScene(long sceneId, eSynType synType, Object synObject){
		List<String> sceneUserIds = SameSceneContainer.getInstance().getAllSceneUser(sceneId);
		if(null == sceneUserIds || null == synObject || sceneUserIds.isEmpty() || sceneId <= 0){
			return;
		}
		List<Player> players = new ArrayList<Player>();
		for(String userId : sceneUserIds){
			Player player = PlayerMgr.getInstance().findPlayerFromMemory(userId);
			if (null != player) {
				players.add(player);
			}
		}
		//多个用户同步相同的数据
		ClientDataSynMgr.synDataMutiple(players, synObject, synType, eSynOpType.UPDATE_SINGLE);
	}
}
