package com.playerdata.dataSyn.sameSceneSyn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SameSceneContainer {
	
	/**
	 * <场景id, <角色id, 存储信息>>
	 */
	private HashMap<Long, ConcurrentHashMap<String, ? extends SameSceneDataBaseIF>> container;
	private HashMap<Long, Integer> sceneCountMap;
	private Lock readLock;
	private Lock writeLock;
	
	private static AtomicLong ATOMIC_SCENE_ID;	//场景id的生成器，每个主id都是SERIAL_SCENE_COUNT的倍数
	private static int SERIAL_SCENE_COUNT = 16;	//每类场景的总场景个数（一个主场景，其它是子场景）
	private static int MAIN_SCENE_UP_PRESURE_COUNT = 20;	//主界面的上压力位，超过上压力位，会被分去子界面
	private static int MAIN_SCENE_DOWN_PRESURE_COUNT = 10;	//主界面的下压力位，低于下压力位，会回到主界面
	
	static{
		long time = System.nanoTime();
		ATOMIC_SCENE_ID = new AtomicLong(time - time%SERIAL_SCENE_COUNT);
	}
	
	private SameSceneContainer() {
		ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
		readLock = rwLock.readLock();
		writeLock = rwLock.writeLock();
		container = new HashMap<Long, ConcurrentHashMap<String, ? extends SameSceneDataBaseIF>>();
	}
	
	private static class InstanceHolder{
		private static SameSceneContainer instance = new SameSceneContainer();
	}
	
	public static SameSceneContainer getInstance(){
		return InstanceHolder.instance;
	}

	/**
	 * 将玩家添加到某个场景(或者更新)
	 * @param sceneId
	 * @param userId
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends SameSceneDataBaseIF> boolean putUserToScene(long sceneId, String userId, T value){
		ConcurrentHashMap<String, T> scene;
		readLock.lock();
		try {
			scene = (ConcurrentHashMap<String, T>) container.get(sceneId);
		} finally {
			readLock.unlock();
		}
		if(null == scene){
			if(0 == sceneId%SERIAL_SCENE_COUNT){
				//主场景不存在，直接返回false
				return false;
			}else{
				//子场景不存在，创建一个
				writeLock.lock();
				try {
					scene = new ConcurrentHashMap<String, T>();
					container.put(sceneId, scene);
				} finally {
					writeLock.unlock();
				}
			}
		}		
		DataAutoSynMgr.getInstance().addWaitScene(sceneId);
		SameSceneDataBaseIF data = scene.get(userId);
		if(null != data){
			value.setNewAdd(data.isNewAdd());
			value.setRemoved(data.isRemoved());
		}else if(0 == sceneId%SERIAL_SCENE_COUNT){
			writeLock.lock();
			try {
				Integer sceneCount = sceneCountMap.get(sceneId);
				if(null == sceneCount){
					sceneCount = 1;
					sceneCountMap.put(sceneId, sceneCount);
				}else{
					sceneCountMap.put(sceneId, sceneCount + 1);
				}
			} finally {
				writeLock.unlock();
			}
		}
		scene.put(userId, value);
		return true;
	}
	
	/**
	 * 将玩家添加到某个场景(或者更新)
	 * @param sceneId
	 * @param userId
	 * @param value
	 * @param mustMainScene 是否必须加入主场景
	 * @return
	 */
	public <T extends SameSceneDataBaseIF> boolean putUserToScene(long sceneId, String userId, T value, boolean mustMainScene){
		long mainSceneId = sceneId - sceneId%SERIAL_SCENE_COUNT;
		long subSceneId = mainSceneId + Long.valueOf(userId)%SERIAL_SCENE_COUNT;
		if(mainSceneId != sceneId){
			//判断场景id合法性（该函数必须传入主场景id）
			return false;
		}
		if(mustMainScene){
			//强调必须放入主场景
			return putIntoScene(mainSceneId, subSceneId, userId, value);
		}
		readLock.lock();
		try {
			Integer sceneCount = sceneCountMap.get(mainSceneId);
			if(null == sceneCount){
				sceneCount = 0;
				sceneCountMap.put(mainSceneId, sceneCount);
			}
			if(sceneCount > MAIN_SCENE_UP_PRESURE_COUNT){
				//主场景超过上压力位，添加到自身属于的子场景中
				//如果在主场景中，则把自己从主场景移除
				putIntoScene(subSceneId, mainSceneId, userId, value);
			}else if(sceneCount < MAIN_SCENE_DOWN_PRESURE_COUNT){
				//主场景人数过少，加入主场景，如果在子场景中，则把自己从子场景移除
				putIntoScene(mainSceneId, subSceneId, userId, value);
			}else{
				//主场景人数适中，如果自己不在子场景，就加入主场景，否则仍留子场景
				ConcurrentHashMap<String, ? extends SameSceneDataBaseIF> scene = container.get(subSceneId);
				if(null == scene || !scene.containsKey(userId)){
					putUserToScene(mainSceneId, userId, value);
				}else{
					putUserToScene(subSceneId, userId, value);
				}
			}
		} finally {
			readLock.unlock();
		}
		return true;
	}
	
	/**
	 * 并不是真的移除，只是加个移除的标记
	 * @param sceneId
	 * @param userId
	 */
	public void removeUserFromScene(long sceneId, String userId){
		ConcurrentHashMap<String, ? extends SameSceneDataBaseIF> scene;
		readLock.lock();
		try {
			scene = container.get(sceneId);
		} finally {
			readLock.unlock();
		}
		if(null == scene){
			return;
		}
		SameSceneDataBaseIF data = scene.get(userId);
		if(null == data) {
			return;
		}
		data.setRemoved(true);
		DataAutoSynMgr.getInstance().addWaitScene(sceneId);
	}
	
	/**
	 * 把玩家从场景中移除（实际的删除，但不同步数据）
	 * @param sceneId
	 * @param userId
	 */
	void deleteUserFromScene(long sceneId, String userId){
		ConcurrentHashMap<String, ? extends SameSceneDataBaseIF> scene;
		readLock.lock();
		try {
			scene = container.get(sceneId);
		} finally {
			readLock.unlock();
		}
		if(null == scene){
			return;
		}
		if(null != scene.remove(userId) && 0 == sceneId%SERIAL_SCENE_COUNT){
			//这里表示移除成功，并且是从主场景
			writeLock.lock();
			try {
				Integer sceneCount = sceneCountMap.get(sceneId);
				if(null != sceneCount){
					sceneCount -= 1;
					if(sceneCount < 0) sceneCount = 0;
					sceneCountMap.put(sceneId, sceneCount);
				}
			} finally {
				writeLock.unlock();
			}
		}
	}
	
	public long createNewScene(){
		writeLock.lock();
		try {
			long newSceneId = ATOMIC_SCENE_ID.addAndGet(SERIAL_SCENE_COUNT);
			ConcurrentHashMap<String, SameSceneDataBaseIF> scene = new ConcurrentHashMap<String, SameSceneDataBaseIF>();
			container.put(newSceneId, scene);
			return newSceneId;
		} finally {
			writeLock.unlock();
		}
	}
	
	public void addRemoveScene(long sceneId){
		if(0 == sceneId%SERIAL_SCENE_COUNT){
			//移除主场景的时候，移除所有的子场景（先移除的主场景，后移除的子场景）
			for(long i = sceneId + SERIAL_SCENE_COUNT - 1; i >= sceneId; i--){
				DataAutoSynMgr.getInstance().addRemoveScene(i);
			}
		}else{
			DataAutoSynMgr.getInstance().addRemoveScene(sceneId);
		}
	}
	
	void removeScene(long sceneId){
		writeLock.lock();
		try {
			container.remove(sceneId);
		} finally {
			writeLock.unlock();
		}
	}
	
	/**
	 * 获取场景中需要同步推数据的成员
	 * 
	 * <note>子场景id就只拿子场景成员，主场景id拿全部场景的成员</note>
	 * @param sceneId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends SameSceneDataBaseIF> Map<String, T> getSceneSynMembers(long sceneId){
		HashMap<String, T> resultMap;
		readLock.lock();
		try {
			ConcurrentHashMap<String, T> scene = (ConcurrentHashMap<String, T>) container.get(sceneId);
			if(null == scene) {
				return new HashMap<String, T>();
			}
			resultMap = new HashMap<String, T>(scene);
			if(sceneId%SERIAL_SCENE_COUNT == 0){
				for(long i = sceneId + 1; i < sceneId + SERIAL_SCENE_COUNT; i++){
					ConcurrentHashMap<String, T> subScene = (ConcurrentHashMap<String, T>) container.get(i);
					if(null != subScene && !subScene.isEmpty()){
						for(String key : subScene.keySet()){
							resultMap.put(key, null);
						}
					}
				}
			}
			return resultMap;
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * 获取场景中存在的人员(个人取数据用)
	 * 
	 * <note>取一个场景中所有的人，如果是子场景，一并取出主场景中所有的人</note>
	 * @param sceneId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends SameSceneDataBaseIF> Map<String, T> getExistMembers(long sceneId){
		HashMap<String, T> resultMap;
		readLock.lock();
		try {
			ConcurrentHashMap<String, T> scene = (ConcurrentHashMap<String, T>) container.get(sceneId);
			if(null == scene) {
				return new HashMap<String, T>();
			}else{
				resultMap = new HashMap<String, T>(scene);
			}
			long mainSceneId = sceneId - sceneId%SERIAL_SCENE_COUNT;
			if(mainSceneId != sceneId){
				ConcurrentHashMap<String, T> mainScene = (ConcurrentHashMap<String, T>) container.get(mainSceneId);
				if(null != mainScene && !mainScene.isEmpty()){
					for(Entry<String, T> entry : mainScene.entrySet()){
						resultMap.put(entry.getKey(), entry.getValue());
					}
				}
			}
			return resultMap;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 获取所有场景里所有的玩家id
	 * @param sceneId
	 * @return
	 */
	public List<String> getAllSceneUser(long sceneId){
		ArrayList<String> resultList;
		readLock.lock();
		try {
			ConcurrentHashMap<String, ? extends SameSceneDataBaseIF> scene = container.get(sceneId);
			if(null == scene) {
				return new ArrayList<String>();
			}
			resultList = new ArrayList<String>(scene.keySet());
			long mainSceneId = sceneId - sceneId%SERIAL_SCENE_COUNT;
			if(mainSceneId == sceneId){
				for(long i = mainSceneId + 1; i < mainSceneId + SERIAL_SCENE_COUNT; i++){
					ConcurrentHashMap<String, ? extends SameSceneDataBaseIF> subScene = container.get(i);
					if(null != subScene && !subScene.isEmpty()){	
						resultList.addAll(subScene.keySet());
					}
				}
			}
			return resultList;
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * 获取自己所在场景里所有的玩家id
	 * @param sceneId
	 * @param userId
	 * @return
	 */
	public List<String> getSelfSceneUser(long sceneId, String userId){
		ArrayList<String> resultList;
		long mainSceneId = sceneId - sceneId%SERIAL_SCENE_COUNT;
		long subSceneId = mainSceneId + Long.valueOf(userId)%SERIAL_SCENE_COUNT;
		readLock.lock();
		try {
			ConcurrentHashMap<String, ? extends SameSceneDataBaseIF> scene = container.get(mainSceneId);
			if(null == scene) {
				return new ArrayList<String>();
			}
			resultList = new ArrayList<String>(scene.keySet());
			if(mainSceneId != subSceneId){
				ConcurrentHashMap<String, ? extends SameSceneDataBaseIF> subScene = container.get(subSceneId);
				if(null != subScene && !subScene.isEmpty()){	
					resultList.addAll(subScene.keySet());
				}
			}
			return resultList;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 通过查找其中一个元素（确定元素的类型）
	 * @param sceneId
	 * @return
	 */
	public SameSceneDataBaseIF checkType(long sceneId){
		readLock.lock();
		try {
			ConcurrentHashMap<String, ? extends SameSceneDataBaseIF> scene = container.get(sceneId);
			if(null == scene || scene.isEmpty()) return null;
			SameSceneDataBaseIF oneValue = scene.values().iterator().next();
			return oneValue;
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * 加入一个场景，从另外一个场景移除
	 * (过程是先移除，后加入)
	 * @param putSceneId
	 * @param removeFrom
	 * @param userId
	 * @param value
	 * @return
	 */
	private <T extends SameSceneDataBaseIF> boolean putIntoScene(long putSceneId, long removeFrom, String userId, T value){
		removeUserFromScene(removeFrom, userId);
		return putUserToScene(putSceneId, userId, value);
	}
}
