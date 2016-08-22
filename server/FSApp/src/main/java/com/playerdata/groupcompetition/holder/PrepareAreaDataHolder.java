package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.prepare.PositionInfo;
import com.playerdata.groupcompetition.prepare.SameSceneSynData;
import com.playerdata.groupcompetition.syn.SameSceneContainer;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class PrepareAreaDataHolder {
	
	public static eSynType synType = eSynType.GC_PREPARE_POSITION;
	
	private PrepareAreaDataHolder(){ }

	private static PrepareAreaDataHolder instance = new PrepareAreaDataHolder();
	
	public static PrepareAreaDataHolder getInstance(){
		return instance;
	}
	
	public int synData(long sceneId){
		int synCount = 0;
		Map<String, PositionInfo> synData = SameSceneContainer.getInstance().getSceneMembers(sceneId);
		if(null == synData || synData.isEmpty() || sceneId <= 0){
			return 0;
		}
		//用来同步数据的结构
		SameSceneSynData synObject = new SameSceneSynData();
		synObject.setId(String.valueOf(sceneId));
		synObject.setSynData(synData);
		
		List<Player> players = new ArrayList<Player>();
		Iterator<Entry<String, PositionInfo>> entryIterator = synData.entrySet().iterator();
		while(entryIterator.hasNext()){
			Entry<String, PositionInfo> entry = entryIterator.next();
			Player player = PlayerMgr.getInstance().findPlayerFromMemory(entry.getKey());
			if(null == player){
				//获取的map是单独创建的，所以，这里没有用迭代的remove删除
				SameSceneContainer.getInstance().removeUserFromScene(sceneId, entry.getKey());
				continue;
			}
			synCount++;
			players.add(player);
		}
		if(!players.isEmpty()){
			//多个用户同步相同的数据
			ClientDataSynMgr.synDataMutiple(players, synObject, synType, eSynOpType.UPDATE_SINGLE);
		}
		return synCount;
	}
}
