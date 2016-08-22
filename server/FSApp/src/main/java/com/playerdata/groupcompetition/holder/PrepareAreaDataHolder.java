package com.playerdata.groupcompetition.holder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.rwproto.DataSynProtos.eSynType;

public class PrepareAreaDataHolder {
	
	public static eSynType synType = eSynType.GC_PREPARE_POSITION;
	private HashMap<String, HashSet<String>> userOwn = new HashMap<String, HashSet<String>>();
	
	private PrepareAreaDataHolder(){ }

	private static PrepareAreaDataHolder instance = new PrepareAreaDataHolder();
	
	public static PrepareAreaDataHolder getInstance(){
		return instance;
	}
	
	/**
	 * 通知有变动的玩家
	 * @param userId
	 * @param changedUsers
	 */
	public void informChange(String userId, Set<String> changedUsers){
		HashSet<String> newAdd = new HashSet<String>();
		HashSet<String> ownSet = userOwn.get(userId);
		if(null == ownSet){
			ownSet = new HashSet<String>();
			userOwn.put(userId, ownSet);
		}
		for(String changed : changedUsers){
			if(!ownSet.contains(changed)){
				ownSet.add(changed);
				newAdd.add(changed);
			}
		}
	}
	
	/**
	 * 同步玩家的详细信息
	 * @param newAdd
	 */
	public void synDetailMembersInfo(HashSet<String> newAdd){
		
	}
}
