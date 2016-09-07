package com.rw.dataaccess.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.log.FSTraceLogger;
import com.log.GameLog;
import com.rw.dataaccess.PlayerLoadOperation;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.dao.kvdata.DataKvEntity;
import com.rw.fsutil.dao.optimize.DataAccessFactory;

/**
 * <pre>
 * 玩家加载指令，暂时只针对DataKv的内容
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class PlayerLoadOperationImpl implements PlayerLoadOperation {

	private final DataKVDao<?>[] dataKVArray;
	private final int maxType;
	private final AtomicInteger generator = new AtomicInteger();

	public PlayerLoadOperationImpl(HashMap<Integer, DataKVDao<?>> map) {
		if (map.isEmpty()) {
			this.dataKVArray = new DataKVDao[0];
			this.maxType = 0;
		} else {
			TreeSet<Integer> set = new TreeSet<Integer>();
			for (Integer key : map.keySet()) {
				set.add(key);
			}
			this.maxType = set.last();
			this.dataKVArray = new DataKVDao[maxType + 1];
			for (Map.Entry<Integer, DataKVDao<?>> entry : map.entrySet()) {
				dataKVArray[entry.getKey()] = entry.getValue();
			}
		}
	}

	@Override
	public void execute(String userId) {
		long start = System.currentTimeMillis();
		List<DataKvEntity> list = DataAccessFactory.getDataKvManager().getRangeDataKvEntitys(userId);
		int size = list.size();
		for (int i = 0; i < size; i++) {
			DataKvEntity entity = list.get(i);
			Integer type = entity.getType();
			if (type > maxType) {
				FSUtilLogger.error("out of range of load kv type:" + type + ",max = " + maxType);
				continue;
			}
			DataKVDao<?> kvDao = dataKVArray[type];
			if (kvDao == null) {
				GameLog.error("PlayerLoadOperation", "#execute()", "获取DataKvDao失败：" + type);
				continue;
			}
			kvDao.putIntoCacheByDBString(entity.getUserId(), entity.getValue());
		}
		int seqId = generator.incrementAndGet();
		FSTraceLogger.logger("LOAD_KV", System.currentTimeMillis() - start, "LOAD_KV", seqId, userId, null, true);
	}
}
