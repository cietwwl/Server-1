package com.playerdata.teambattle.data;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
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
	
	public boolean removeTeam(TBTeamItem teamItem){
		if(StringUtils.isBlank(teamItem.getHardID()) || StringUtils.isBlank(teamItem.getTeamID())) return false;
		return getItemStore(teamItem.getHardID()).removeItem(teamItem.getTeamID());
	}
	
	public TBTeamItem getItem(String hardID, String teamID){
		return getItemStore(hardID).getItem(teamID);
	}
	
	public void synData(Player player, String teamID){
		String hardID = getHardIDFromTeamID(teamID);
		if(StringUtils.isBlank(hardID)) return;
		TBTeamItem teamItem = getItemStore(hardID).getItem(teamID);
		synData(player, teamItem);
	}
	
	public void synData(Player player, TBTeamItem teamItem){
		if(teamItem == null) return;
		ClientDataSynMgr.synData(player, teamItem, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void synData(TBTeamItem teamItem){
		for(TeamMember member : teamItem.getMembers()){
			Player player = PlayerMgr.getInstance().find(member.getUserID());
			if(player!= null) synData(player, teamItem);
		}
	}
	
	public MapItemStore<TBTeamItem> getItemStore(String hardID) {
		MapItemStoreCache<TBTeamItem> cache = MapItemStoreFactory.getTBTeamItemCache();
		return cache.getMapItemStore(hardID, TBTeamItem.class);
	}
	
	public String getHardIDFromTeamID(String teamID){
		if(StringUtils.isBlank(teamID)) return null;
		String[] strArr = teamID.split("_");
		if(strArr.length != 2) return null;
		return strArr[0];
	}
}
