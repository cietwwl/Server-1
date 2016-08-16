package com.playerdata.groupcompetition.syn;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.rwproto.DataSynProtos.eSynType;

public class SameSceneContainer {
	
	/**
	 * <场景id, <角色id, 存储信息>>
	 */
	private HashMap<Long, ConcurrentHashMap<String, Object>> container;
	/**
	 * 每个场景用不同的同步结构
	 */
	private HashMap<Long, eSynType> synTypeMap;
	
	
	private Lock readLock;
	private Lock writeLock;
	
	
	private SameSceneContainer() {
		ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
		readLock = rwLock.readLock();
		writeLock = rwLock.writeLock();
		container = new HashMap<Long, ConcurrentHashMap<String, Object>>();
	}
	
	private static class InstanceHolder{
		private static SameSceneContainer instance = new SameSceneContainer();
	}
	
	public static SameSceneContainer getInstance(){
		return InstanceHolder.instance;
	}

	public boolean putUserToScene(long sceneId, String userId, Object value){
		readLock.lock();
		try {
			ConcurrentHashMap<String, Object> scene = container.get(sceneId);
			if(null == scene){
				return false;
			}
			DataAutoSynMgr.getInstance().addWaitScene(sceneId);
			return null == scene.put(userId, value);
		} finally {
			readLock.unlock();
		}
	}
	
	public void removeUserFromScene(long sceneId, String userId){
		readLock.lock();
		try {
			ConcurrentHashMap<String, Object> scene = container.get(sceneId);
			if(null == scene){
				return;
			}
			scene.remove(userId);
			DataAutoSynMgr.getInstance().addWaitScene(sceneId);
		} finally {
			readLock.unlock();
		}
	}
	
	public long createNewScene(eSynType type){
		writeLock.lock();
		try {
			long newSceneId = System.nanoTime();
			ConcurrentHashMap<String, Object> scene = new ConcurrentHashMap<String, Object>();
			container.put(newSceneId, scene);
			synTypeMap.put(newSceneId, type);
			return newSceneId;
		} finally {
			writeLock.unlock();
		}
	}
	
	public void removeScene(long sceneId){
		writeLock.lock();
		try {
			container.remove(sceneId);
			synTypeMap.remove(sceneId);
		} finally {
			writeLock.unlock();
		}
	}
	
	public Map<String, Object> getSceneMembers(long sceneId){
		readLock.lock();
		try {
			ConcurrentHashMap<String, Object> scene = container.get(sceneId);
			if(null == scene) return new HashMap<String, Object>();
			return new HashMap<String, Object>(scene);
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * 获取同步数据类型
	 * @param sceneId
	 * @return
	 */
	public eSynType getSceneSynType(long sceneId){
		readLock.lock();
		try {
			return synTypeMap.get(sceneId);
		} finally {
			readLock.unlock();
		}
	}
}
