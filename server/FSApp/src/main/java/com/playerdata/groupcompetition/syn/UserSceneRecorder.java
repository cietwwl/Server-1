package com.playerdata.groupcompetition.syn;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 记录玩家所在的场景
 * @author aken
 */
public class UserSceneRecorder {
	
	private ConcurrentHashMap<SceneType, ConcurrentHashMap<String, Long>> record;
	
	private UserSceneRecorder(){
		record = new ConcurrentHashMap<SceneType, ConcurrentHashMap<String,Long>>();
	}
	
	private static class InstanceHolder{
		private static UserSceneRecorder instance = new UserSceneRecorder();
	}
	
	public static UserSceneRecorder getInstance(){
		return InstanceHolder.instance;
	}
	
	public void addUserToRecord(String userId, SceneType sceneType, long sceneId){
		ConcurrentHashMap<String, Long> typeContainer = record.get(sceneType);
		if(null == typeContainer){
			typeContainer = new ConcurrentHashMap<String, Long>();
			record.put(sceneType, typeContainer);
		}
		typeContainer.put(userId, sceneId);
	}
	
	public void removeUserFromRecord(String userId, SceneType sceneType){
		ConcurrentHashMap<String, Long> typeContainer = record.get(sceneType);
		if(null == typeContainer){
			return;
		}
		typeContainer.remove(userId);
	}
	
	public Long getUserSceneID(String userId, SceneType sceneType){
		ConcurrentHashMap<String, Long> typeContainer = record.get(sceneType);
		if(null == typeContainer){
			return null;
		}
		return typeContainer.get(userId);
	}
}
