package com.rwbase.dao.randomBoss.db;

import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.cacheDao.DataKVDao;

public class RandomBossRecordDAO extends DataKVDao<RandomBossRecord>{

	private static RandomBossRecordDAO instance = new RandomBossRecordDAO();
	
	private RandomBossRecordDAO(){}
	
	public static RandomBossRecordDAO getInstance(){
		return instance;
	}

	
	/**
	 * 创建新的boss数据
	 * @param id
	 * @param ownerID
	 * @param leftHp
	 * @param bossTemplateId
	 * @return
	 */
	public RandomBossRecord create(String id, String ownerID, long leftHp, 
			String bossTemplateId){
		RandomBossRecord record = super.get(id);
		if(record != null){
			GameLog.error(LogModule.COMMON, RandomBossRecordDAO.class.getName(), "创建随机boss时，发现已经存在相同id的boss:" + id, null);
			return null;
		}
		
		record = new RandomBossRecord();
		record.setId(id);
		record.setOwerID(ownerID);
		record.setLeftHp(leftHp);
		record.setBossTemplateId(bossTemplateId);
		record.setBornTime(System.currentTimeMillis());
		update(record);
		return record;
	}
	
	
	
	
	
}
