package com.playerdata.dataSyn.sameSceneSyn;

import com.playerdata.groupcompetition.prepare.PositionInfo;
import com.playerdata.groupcompetition.prepare.PrepareAreaMgr;
import com.playerdata.groupcompetition.prepare.SameSceneSynData;
import com.rwproto.DataSynProtos.eSynType;

public enum SameSceneType {
	/**存储的基础数据结构,同步的枚举,同步的数据结构*/
	GCompSameScene(PositionInfo.class, PrepareAreaMgr.synType, SameSceneSynData.class);
	
	private Class<? extends SameSceneDataBaseIF> baseDataClazz;
	private eSynType synType;
	private Class<? extends SameSceneSynDataIF> synDataClazz;
	
	SameSceneType(Class<? extends SameSceneDataBaseIF> baseIF, eSynType synType, Class<? extends SameSceneSynDataIF> synIF){
		this.baseDataClazz = baseIF;
		this.synType = synType;
		this.synDataClazz = synIF;
	}
	
	public static SameSceneType getEnum(SameSceneDataBaseIF dataIF){
		if(null == dataIF) return null;
		for(SameSceneType synType : SameSceneType.values()){
			if(dataIF.getClass().equals(synType.baseDataClazz)){
				return synType;
			}
		}
		return null;
	}
	
	public eSynType getSynType(){
		return synType;
	}
	
	public SameSceneSynDataIF getSynDataObject(){
		try {
			return synDataClazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
