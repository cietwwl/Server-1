package com.playerdata.groupcompetition.syn;

import java.util.HashMap;

import com.playerdata.groupcompetition.prepare.PositionInfo;
import com.playerdata.groupcompetition.prepare.PrepareAreaMgr;

public class SameSceneContainerMgr {
	
	private HashMap<Class<? extends ISameSceneDataSignal>, SameSceneContainer<? extends ISameSceneDataSignal>> map = new HashMap<Class<? extends ISameSceneDataSignal>, SameSceneContainer<? extends ISameSceneDataSignal>>();

	private SameSceneContainerMgr(){
		map.put(PositionInfo.class, new SameSceneContainer<PositionInfo>(PrepareAreaMgr.synType));
	}
	
	private static class InstanceHolder{
		private static SameSceneContainerMgr instance = new SameSceneContainerMgr();
	}
	
	public static SameSceneContainerMgr getInstance(){
		return InstanceHolder.instance;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ISameSceneDataSignal> SameSceneContainer<T> getContainer(Class<T> clzz){
		return (SameSceneContainer<T>) map.get(clzz);
	}
}
