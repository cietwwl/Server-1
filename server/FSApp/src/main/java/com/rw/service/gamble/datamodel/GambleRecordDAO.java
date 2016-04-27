package com.rw.service.gamble.datamodel;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.DataKVDao;

public class GambleRecordDAO extends DataKVDao<GambleRecord> {
	private static GambleRecordDAO m_instance;

	public static GambleRecordDAO getInstance() {
		if (m_instance == null) {
			m_instance = new GambleRecordDAO();
		}
		return m_instance;
	}
	
	public void reset(String userId){
		GambleRecord result = super.get(userId);
		if (result != null && result.getUserId() != null){
			result.resetHistory();
			if (!super.commit(result)){
				GameLog.error("钓鱼台", userId, "重置钓鱼台历史纪录失败");
			}
		}
	}
	
	public GambleRecord getOrCreate(String userId){
		GambleRecord result = super.get(userId);
		if (result == null || result.getUserId() == null){
			result = new GambleRecord(userId);
			if (!super.commit(result)){
				GameLog.error("钓鱼台", userId, "写入新建的钓鱼台历史纪录失败");
				return null; 
			}
		}
		return result;
	}
}
