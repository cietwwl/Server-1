package com.groupCopy.bm.groupCopy;

import com.common.BeanCopyer;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyStatus;

public class GroupCopyMapBL {

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
}
