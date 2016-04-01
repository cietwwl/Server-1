package com.groupCopy.bm.groupCopy;

import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecordHolder;
import com.playerdata.Player;

/**
 * 
 * 
 * @author Allen
 *
 */
public class GroupCopyMgr {

	private GroupCopyLevelRecordHolder groupCopyLevelRecordHolder;

	private GroupCopyMapRecordHolder groupCopyMapRecordHolder;


	public GroupCopyMgr(String groupIdP) {
		groupCopyLevelRecordHolder = new GroupCopyLevelRecordHolder(groupIdP);
		groupCopyMapRecordHolder = new GroupCopyMapRecordHolder(groupIdP);
	}
	
	
	public synchronized GroupCopyResult  openMap(String mapId){
		return GroupCopyMapBL.openMap(groupCopyMapRecordHolder, mapId);
	}
	
	public synchronized GroupCopyResult resetMap(String mapId){
		return GroupCopyMapBL.resetMap(groupCopyMapRecordHolder, mapId);
	}
	
	
	public synchronized GroupCopyResult  beginFight(Player player, String levelId){
		return GroupCopyLevelBL.beginFight(player, groupCopyLevelRecordHolder, levelId);
	}
	public synchronized GroupCopyResult  endFight(Player player, String levelId){
		return GroupCopyLevelBL.endFight(player, groupCopyLevelRecordHolder, levelId);
	}


	public synchronized void synMapData(Player player, int version){
		
		groupCopyMapRecordHolder.synAllData(player, version);
		
	}
	public synchronized void synLevelData(Player player, int version){
		
		groupCopyLevelRecordHolder.synAllData(player, version);
		
	}

}
