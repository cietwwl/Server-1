package com.rw.service.TaoistMagic.datamodel;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.DataKVDao;

public class TaoistMagicHolder extends DataKVDao<TaoistMagicRecord>{
	private static TaoistMagicHolder instance;

	public static TaoistMagicHolder getInstance() {
		if (instance == null) {
			instance = new TaoistMagicHolder();
		}
		return instance;
	}
	
	public TaoistMagicRecord getOrCreate(String userId){
		TaoistMagicRecord result = super.get(userId);
		if (result == null || result.getUserId() == null){
			result = TaoistMagicRecord.Create(userId);
			if (!super.commit(result)){
				GameLog.error("道术", userId, "写入新建的道术纪录失败");
				return null; 
			}
		}
		return result;
	}

	public boolean setLevel(TaoistMagicRecord record, int tid, int level) {
		if (record.setLevel(tid, level)){
			if (!super.commit(record)){
				GameLog.error("道术", record.getUserId(), "无法更新道术记录");
			}
			return true;
		}
		return false;
	}

}
