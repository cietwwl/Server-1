package com.groupCopy.bm.groupCopy;

import java.util.Set;

import com.common.BeanCopyer;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyProgress;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyStatus;
import com.playerdata.Player;


/**
 * 
 */
public class GroupCopyMapBL {

	/**
	 * 开启帮派地图副本
	 * @param groupCopyMapRecordHolder
	 * @param mapId
	 * @return
	 */
	public static GroupCopyResult  openMap(GroupCopyMapRecordHolder groupCopyMapRecordHolder,String mapId){
		GroupCopyResult result = GroupCopyResult.newResult();
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
		
		result.setSuccess(true);
		return result;
		
	}
	
	//从配置转换成record
	private static GroupCopyMapRecord fromCfg(GroupCopyMapCfg copyMapCfg){
		if(copyMapCfg == null){
			return null;
		}
		GroupCopyMapRecord record = new GroupCopyMapRecord();				
		BeanCopyer.copy(record, copyMapCfg);		
		return record;
	}	
	
	
	/**
	 * 重置帮派副本
	 * @param groupCopyMapRecordHolder
	 * @param mapId
	 * @return
	 */
	public static GroupCopyResult resetMap(GroupCopyMapRecordHolder groupCopyMapRecordHolder,String mapId){
		
		GroupCopyResult result = GroupCopyResult.newResult();
		boolean success = false;
		GroupCopyMapRecord mapRecord = groupCopyMapRecordHolder.getItem(mapId);
		if(mapRecord != null){			
			mapRecord.setProgress(0);
			groupCopyMapRecordHolder.updateItem(mapRecord);
			success = true;
		}
		result.setSuccess(success);
		return result;
	}

	/**
	 * 内部同步副本进度
	 * @param player
	 * @param levelRecordHolder
	 * @param mapRecordHolder
	 * @param levelId
	 */
	public static void calculateMapProgress(Player player,
			GroupCopyLevelRecordHolder levelRecordHolder,
			GroupCopyMapRecordHolder mapRecordHolder, String levelId) {
		
		int totalHp = 0;
		int currentHp = 0;
		GroupCopyLevelRecord lvRecord;
		GroupCopyProgress progress;
		GroupCopyLevelCfg cfg = GroupCopyLevelCfgDao.getInstance().getCfgById(levelId);
		GroupCopyMapCfg mapCfg = GroupCopyMapCfgDao.getInstance().getCfgById(cfg.getChaterID());
		Set<String> lvList = mapCfg.getLvList();
		for (String id : lvList) {
			lvRecord = levelRecordHolder.getByLevel(id);
			progress = lvRecord.getProgress();
			totalHp += progress.getTotalHp();
			currentHp += progress.getCurrentHp();
		}
		double p = (double) (totalHp - currentHp)/ totalHp * 100;
		mapRecordHolder.updateMapProgress(levelId, p);
		
	}
	
	
	
}
