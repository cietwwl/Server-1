package com.playerdata.dataSyn.sameSceneSyn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SameSceneContainer {
	
	/**
	 * <场景id, <角色id, 存储信息>>
	 */
	private HashMap<Long, ConcurrentHashMap<String, ? extends SameSceneDataBaseIF>> container;
	private Lock readLock;
	private Lock writeLock;
	
	
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
			return false;
		}
		DataAutoSynMgr.getInstance().addWaitScene(sceneId);
		return null == scene.put(userId, value);
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
	public void deleteUserFromScene(long sceneId, String userId){
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
		scene.remove(userId);
	}
	
	public long createNewScene(){
		writeLock.lock();
		try {
			long newSceneId = System.nanoTime();
			ConcurrentHashMap<String, SameSceneDataBaseIF> scene = new ConcurrentHashMap<String, SameSceneDataBaseIF>();
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
	public <T extends SameSceneDataBaseIF> Map<String, T> getSceneMembers(long sceneId){
		readLock.lock();
		try {
			ConcurrentHashMap<String, T> scene = (ConcurrentHashMap<String, T>) container.get(sceneId);
			if(null == scene) return new HashMap<String, T>();
			return new HashMap<String, T>(scene);
		} finally {
			readLock.unlock();
		}
	}
	
	/*
	 * 获取某个场景里所有的玩家id
	 */
	public List<String> getAllSceneUser(long sceneId){
		readLock.lock();
		try {
			ConcurrentHashMap<String, ? extends SameSceneDataBaseIF> scene = container.get(sceneId);
			if(null == scene) return new ArrayList<String>();
			return new ArrayList<String>(scene.keySet());
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
}
