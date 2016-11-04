package com.rw.handler.groupFight.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.groupFight.dataForClient.GFDefendArmySimpleLeader;
import com.rwproto.DataSynProtos.MsgDataSyn;
import com.rwproto.DataSynProtos.eSynType;

public class GFDefendArmyItemHolder{
	
	private static GFDefendArmyItemHolder instance = new GFDefendArmyItemHolder();
	
	public static GFDefendArmyItemHolder getInstance(){
		return instance;
	}

	final private eSynType synSelfType = eSynType.GFDefendArmyData;
	final private eSynType synSimpleLeaderType = eSynType.GFightSimpleLeader;
	
	private Map<String,AtomicInteger> versionMap = new ConcurrentHashMap<String,AtomicInteger>();
	
	private  Map<String, GFDefendArmyItem> listSelf = new HashMap<String, GFDefendArmyItem>();
	private SynDataListHolder<GFDefendArmyItem> listSelfHolder = new SynDataListHolder<GFDefendArmyItem>(GFDefendArmyItem.class);
	
	private  Map<String, GFDefendArmySimpleLeader> listLeader = new HashMap<String, GFDefendArmySimpleLeader>();
	private SynDataListHolder<GFDefendArmySimpleLeader> listLeaderHolder = new SynDataListHolder<GFDefendArmySimpleLeader>(GFDefendArmySimpleLeader.class);
	
	
	public void synSelf(MsgDataSyn msgDataSyn) {
		listSelfHolder.Syn(msgDataSyn);
		// 更新数据
		List<GFDefendArmyItem> itemList = listSelfHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			GFDefendArmyItem selfArmy = itemList.get(i);
			listSelf.put(selfArmy.getArmyID(), selfArmy);
		}
	}
	
	public void synLeader(MsgDataSyn msgDataSyn){
		listLeaderHolder.Syn(msgDataSyn);
		// 更新数据
		List<GFDefendArmySimpleLeader> itemList = listLeaderHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			GFDefendArmySimpleLeader simpleLeader = itemList.get(i);
			listLeader.put(simpleLeader.getArmyID(), simpleLeader);
		}
	}
}
