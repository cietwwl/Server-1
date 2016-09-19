package com.playerdata.teambattle.data;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class TBTeamItemHolder{
	
	private static TBTeamItemHolder instance = new TBTeamItemHolder();
	
	public static TBTeamItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.TEAM_BATTLE_TEAM;
	
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
	
	public void synData(Player player, String teamID){
		String[] strArr = teamID.split("_");
		if(strArr.length != 2) return;
		String hardID = strArr[0];
		TBTeamItem teamItem = getItemStore(hardID).getItem(teamID);
		ClientDataSynMgr.synData(player, teamItem, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	private MapItemStore<TBTeamItem> getItemStore(String hardID) {
		MapItemStoreCache<TBTeamItem> cache = MapItemStoreFactory.getTBTeamItemCache();
		return cache.getMapItemStore(hardID, TBTeamItem.class);
	}
}
