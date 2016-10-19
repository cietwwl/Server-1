package com.rw.dataaccess.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.log.FSTraceLogger;
import com.log.GameLog;
import com.rw.dataaccess.DataKVCacheInfo;
import com.rw.dataaccess.DataKVType;
import com.rw.dataaccess.PlayerLoadOperation;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
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

	private final DataKVCacheInfo[] preloadArrays;
	private final DataKVDao<?>[] dataKVArray;
	private final int maxType;

	public PlayerLoadOperationImpl(Map<DataKVType, DataKVDao<?>> map) {
		if (map.isEmpty()) {
			this.dataKVArray = new DataKVDao[0];
			this.preloadArrays = new DataKVCacheInfo[0];
			this.maxType = 0;
		} else {
			TreeSet<Integer> set = new TreeSet<Integer>();
			for (DataKVType key : map.keySet()) {
				set.add(key.getType());
			}
			this.maxType = set.last();
			this.dataKVArray = new DataKVDao[maxType + 1];
			this.preloadArrays = new DataKVCacheInfo[map.size()];
			int index = 0;
			for (Map.Entry<DataKVType, DataKVDao<?>> entry : map.entrySet()) {
				DataKVType dataKVType = entry.getKey();
				DataKVDao<?> cache = entry.getValue();
				this.dataKVArray[dataKVType.getType()] = cache;
				this.preloadArrays[index++] = new DataKVCacheInfo(dataKVType, cache);
			}
		}
	}

	@Override
	public void execute(String userId, long createTime, int level) {
		int len = preloadArrays.length;
		ArrayList<Integer> typeList = new ArrayList<Integer>(len);
		for (int i = 0; i < len; i++) {
			DataKVCacheInfo preloadInfo = preloadArrays[i];
			if (preloadInfo.cache.contains(userId)) {
				continue;
			}
			typeList.add(preloadInfo.type.getTypeValue());
		}
		long start = System.currentTimeMillis();
		List<DataKvEntity> list = DataAccessFactory.getDataKvManager().getRangeDataKvEntitys(userId, typeList);
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
		long end = System.currentTimeMillis();
		long cost = end - start;
		FSTraceLogger.recordRun("LOAD_KV", cost);
		RoleExtPropertyFactory.loadAndCreatePlayerExtProperty(userId, createTime, level);
		FSTraceLogger.recordRun("LOAD_EXT", System.currentTimeMillis() - end);
	}
}
