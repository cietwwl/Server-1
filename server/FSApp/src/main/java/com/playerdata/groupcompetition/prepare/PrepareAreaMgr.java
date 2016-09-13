package com.playerdata.groupcompetition.prepare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.playerdata.groupcompetition.syn.SameSceneContainer;
import com.rwproto.DataSynProtos.eSynType;

public class PrepareAreaMgr {
	
	private final eSynType synType = eSynType.GC_PREPARE_POSITION;
	private HashMap<String, Long> groupScene;
	
	private static PrepareAreaMgr instance = new PrepareAreaMgr();

	public static PrepareAreaMgr getInstance() {
		return instance;
	}
	
	
	
	/**
	 * 备战阶段开始
	 * 为每个帮派生成一个备战区
	 */
	public void prepareStart(){
		List<String> prepareGroups = getPrepareGroups();
		if(null == prepareGroups || prepareGroups.isEmpty()){
			return;
		}
		groupScene = new HashMap<String, Long>();
		//为每个帮派生成一个准备区
		for(String groupId : prepareGroups){
			long sceneId = SameSceneContainer.getInstance().createNewScene(synType);
			groupScene.put(groupId, sceneId);
		}
	}
	
	/**
	 * 备战阶段结束
	 * 清除所有的备战区
	 */
	public void prepareEnd(){
		if(null == groupScene){
			return;
		}
		//清除每个帮派的准备区
		for(Long sceneId : groupScene.values()){
			SameSceneContainer.getInstance().removeScene(sceneId);
		}
		groupScene = null;
	}
	
	public List<String> getPrepareGroups(){
		return new ArrayList<String>();
	}
}
