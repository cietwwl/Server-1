package com.playerdata.teambattle.data;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynType;

public class TBTeamItemHolder{
	
	private static TBTeamItemHolder instance = new TBTeamItemHolder();
	
	public static TBTeamItemHolder getInstance(){
		return instance;
	}

	final private eSynType synSelfType = eSynType.GFDefendArmyData;
	
	public boolean addNewTeam(TBTeamItem teamItem){
		if(StringUtils.isBlank(teamItem.getHardID()) || StringUtils.isBlank(teamItem.getTeamID())) return false;
		return getItemStore(teamItem.getHardID()).addItem(teamItem);
	}
	
	public boolean updateTeam(TBTeamItem teamItem){
		if(StringUtils.isBlank(teamItem.getHardID()) || StringUtils.isBlank(teamItem.getTeamID())) return false;
		return getItemStore(teamItem.getHardID()).updateItem(teamItem);
	}
	
	public TBTeamItem getItem(String hardID, String teamId){	
		return getItemStore(hardID).getItem(teamId);
	}
	
	private MapItemStore<TBTeamItem> getItemStore(String hardID) {
		MapItemStoreCache<TBTeamItem> cache = MapItemStoreFactory.getTBTeamItemCache();
		return cache.getMapItemStore(hardID, TBTeamItem.class);
	}
}
