package com.rwbase.dao.dropitem;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.common.DataAccessTimeoutException;

public class DropRecordDAO extends DataKVDao<DropRecord> {

	private static DropRecordDAO instance = new DropRecordDAO();
	private ConcurrentHashMap<String, FutureTask<DropRecord>> createMap;

	private DropRecordDAO() {
		this.createMap = new ConcurrentHashMap<String, FutureTask<DropRecord>>();
	}

	public static DropRecordDAO getInstance() {
		return instance;
	}

	public DropRecord getDropRecord(final String userId) throws DataAccessTimeoutException {
		DropRecord record = get(userId);
		if (record != null) {
			return record;
		}
		for (;;) {
			FutureTask<DropRecord> task = createMap.get(userId);
			if (task != null) {
				try {
					return task.get();
				} catch (InterruptedException e) {
					throw new DataAccessTimeoutException(e);
				} catch (ExecutionException e) {
					continue;
				}
			}
			task = new FutureTask<DropRecord>(new Callable<DropRecord>() {

				@Override
				public DropRecord call() {
					try {
						DropRecord record = get(userId);
						if (record != null) {
							return record;
						}
//						GameLog.error("DropRecord", id, errorReason);
						GameLog.error("DropRecord", "#trace", "重新创建首掉："+userId);
						record = new DropRecord();
						record.setUserId(userId);
						DropRecordDAO.this.update(record);
						return record;
					} finally {
						createMap.remove(userId);
					}
				}
			});
			if (createMap.putIfAbsent(userId, task) != null) {
				continue;
			}
			task.run();
			try {
				return task.get();
			} catch (InterruptedException e) {
				throw new DataAccessTimeoutException(e);
			} catch (ExecutionException e) {
				throw new DataAccessTimeoutException(e);
			}
		}
	}
}
