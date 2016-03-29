package com.bm.group.groupCopy;

import com.common.BeanCopyer;
import com.playerdata.Player;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.rwbase.dao.groupCopy.db.GroupCopyLevelRecordHolder;
import com.rwbase.dao.groupCopy.db.GroupCopyMapRecord;
import com.rwbase.dao.groupCopy.db.GroupCopyMapRecordHolder;
import com.rwbase.dao.groupCopy.db.GroupCopyStatus;

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
	
	
	public synchronized boolean  openMap(String mapId){
		
		GroupCopyMapRecord mapRecord = groupCopyMapRecordHolder.getItem(mapId);
		if(mapRecord == null){
			GroupCopyMapCfg mapCfg = GroupCopyMapCfgDao.getInstance().getCfgById(mapId);
			mapRecord = fromCfg(mapCfg);
			mapRecord.setStatus(GroupCopyStatus.OPEN);
			groupCopyMapRecordHolder.addItem(mapRecord);
		}else{
			mapRecord.setStatus(GroupCopyStatus.OPEN);
			groupCopyMapRecordHolder.updateItem(mapRecord);
		}
		
		return true;
		
	}
	
	//从配置转换成record
	private GroupCopyMapRecord fromCfg(GroupCopyMapCfg copyMapCfg){
		if(copyMapCfg == null){
			return null;
		}
		GroupCopyMapRecord record = new GroupCopyMapRecord();				
		BeanCopyer.copy(record, copyMapCfg);		
		return record;
	}	
	
	
	public synchronized boolean resetMap(String mapId){
		boolean success = false;
		GroupCopyMapRecord mapRecord = groupCopyMapRecordHolder.getItem(mapId);
		if(mapRecord != null){			
			mapRecord.setProgress(0);
			groupCopyMapRecordHolder.updateItem(mapRecord);
			success = true;
		}
		
		return success;
	}
	


	public void synMapData(Player player, int version){
		
		groupCopyMapRecordHolder.synAllData(player, version);
		
	}
	public void synLevelData(Player player, int version){
		
		groupCopyMapRecordHolder.synAllData(player, version);
		
	}
	
	
	public void flush() {
		groupCopyLevelRecordHolder.flush();
		groupCopyMapRecordHolder.flush();
	}

}
