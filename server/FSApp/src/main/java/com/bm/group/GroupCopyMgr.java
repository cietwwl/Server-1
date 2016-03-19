package com.bm.group;

import com.playerdata.Player;
import com.rwbase.dao.groupCopy.db.GroupCopyLevelRecordHolder;
import com.rwbase.dao.groupCopy.db.GroupCopyMapRecordHolder;

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


	public void synData(Player player, int version){
		
		groupCopyLevelRecordHolder.synAllData(player, version);
		groupCopyMapRecordHolder.synAllData(player, version);
		
	}
	
	
	public void flush() {
		groupCopyLevelRecordHolder.flush();
		groupCopyMapRecordHolder.flush();
	}

}
