package com.playerdata.groupcompetition.syn;

import com.playerdata.groupcompetition.holder.PrepareAreaDataHolder;
import com.playerdata.groupcompetition.prepare.PositionInfo;

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
			ISameSceneData oneValue = SameSceneContainer.getInstance().checkType(sceneId);
			if(oneValue instanceof PositionInfo){
				synCount += PrepareAreaDataHolder.getInstance().synData(sceneId);
			}
		}
	}
}
