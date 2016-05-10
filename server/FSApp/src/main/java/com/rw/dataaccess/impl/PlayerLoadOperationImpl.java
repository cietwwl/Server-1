package com.rw.dataaccess.impl;

import java.util.HashMap;
import java.util.List;

import com.log.GameLog;
import com.rw.dataaccess.PlayerLoadOperation;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.dao.kvdata.DataKvEntity;
import com.rw.fsutil.dao.optimize.DataAccessFactory;

/**
 * <pre>
 * 玩家加载指令，暂时只针对DataKv的内容
 * </pre>
 * @author Jamaz
 *
 */
public class PlayerLoadOperationImpl implements PlayerLoadOperation {

	private HashMap<Integer, DataKVDao<?>> map;

	public PlayerLoadOperationImpl(HashMap<Integer, DataKVDao<?>> map) {
		this.map = map;
	}

	@Override
	public void execute(String userId) {
		List<DataKvEntity> list = DataAccessFactory.getDataKvManager().getAllDataKvEntitys(userId);
		int size = list.size();
		for (int i = 0; i < size; i++) {
			DataKvEntity entity = list.get(i);
			Integer type = entity.getType();
			DataKVDao<?> kvDao = map.get(type);
			if (kvDao == null) {
				GameLog.error("PlayerLoadOperation", "#execute()", "获取DataKvDao失败：" + type);
				continue;
			}
			kvDao.putIntoCacheByDBString(entity.getUserId(), entity.getValue());
		}
	}

}
