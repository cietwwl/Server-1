package com.playerdata.groupcompetition.syn;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SameSceneContainer {
	
	/**
	 * <场景id, <角色id, 存储信息>>
	 */
	private HashMap<Long, ConcurrentHashMap<String, ? extends ISameSceneData>> container;
	private Lock readLock;
	private Lock writeLock;
	
	
	private SameSceneContainer() {
		ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
		readLock = rwLock.readLock();
		writeLock = rwLock.writeLock();
		container = new HashMap<Long, ConcurrentHashMap<String, ? extends ISameSceneData>>();
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
	public <T extends ISameSceneData> boolean putUserToScene(long sceneId, String userId, T value){
		ConcurrentHashMap<String, T> scene;
		readLock.lock();
		try {
			scene = (ConcurrentHashMap<String, T>) container.get(sceneId);
		} finally {
			readLock.unlock();
		}
		if(null == scene){
			return false;
		}
		DataAutoSynMgr.getInstance().addWaitScene(sceneId);
		return null == scene.put(userId, value);
	}
	
	public void removeUserFromScene(long sceneId, String userId){
		ConcurrentHashMap<String, ? extends ISameSceneData> scene;
		readLock.lock();
		try {
			scene = container.get(sceneId);
		} finally {
			readLock.unlock();
		}
		if(null == scene){
			return;
		}
		scene.remove(userId);
		DataAutoSynMgr.getInstance().addWaitScene(sceneId);
	}
	
	public long createNewScene(){
		writeLock.lock();
		try {
			long newSceneId = System.nanoTime();
			ConcurrentHashMap<String, ISameSceneData> scene = new ConcurrentHashMap<String, ISameSceneData>();
			container.put(newSceneId, scene);
			return newSceneId;
		} finally {
			writeLock.unlock();
		}
	}
	
	public void removeScene(long sceneId){
		writeLock.lock();
		try {
			container.remove(sceneId);
		} finally {
			writeLock.unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ISameSceneData> Map<String, T> getSceneMembers(long sceneId){
		readLock.lock();
		try {
			ConcurrentHashMap<String, T> scene = (ConcurrentHashMap<String, T>) container.get(sceneId);
			if(null == scene) return new HashMap<String, T>();
			return new HashMap<String, T>(scene);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 通过查找其中一个元素（确定元素的类型）
	 * @param sceneId
	 * @return
	 */
	public ISameSceneData checkType(long sceneId){
		readLock.lock();
		try {
			ConcurrentHashMap<String, ? extends ISameSceneData> scene = container.get(sceneId);
			if(null == scene || scene.isEmpty()) return null;
			ISameSceneData oneValue = scene.values().iterator().next();
			return oneValue;
		} finally {
			readLock.unlock();
		}
	}
}
