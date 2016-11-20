package com.rwbase.dao.arena;

import java.util.ArrayList;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rwbase.dao.arena.pojo.RecordInfo;
import com.rwbase.dao.arena.pojo.TableArenaRecord;

public class TableArenaRecordDAO extends DataKVDao<TableArenaRecord> {

	private static TableArenaRecordDAO instance = new TableArenaRecordDAO();

	TableArenaRecordDAO() {
	}

	public static TableArenaRecordDAO getInstance() {
		return instance;
	}

	public TableArenaRecord get(String userId) {
		TableArenaRecord record = super.get(userId);
		if (record != null) {
			return record;
		}
		record = new TableArenaRecord();
		record.setUserId(userId);
		record.setRecordList(new ArrayList<RecordInfo>());
		update(record);
		return record;
	}
	
	/**
	 * 获取缓存数量大小
	 * 
	 * @return
	 */
	protected int getCacheSize() {
		return 3000;
	}

	/**
	 * 获取更新周期间隔(单位：秒)
	 * 
	 * @return
	 */
	protected int getUpdatedSeconds() {
		return 1200;
	}

}