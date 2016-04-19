package com.rw.dataaccess.impl;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.rw.dataaccess.DataKvCreator;
import com.rw.dataaccess.PlayerCreatedOperation;
import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.dao.optimize.DataAccessFactory;

public class PlayerCreatedOperationImpl implements PlayerCreatedOperation {

	private final List<DataKvCreator> list;

	public PlayerCreatedOperationImpl(List<DataKvCreator> list) {
		this.list = list;
	}

	@Override
	public boolean execute(PlayerCreatedParam param) {
		// insert database and put into cache
		String userId = param.getUserId();
		List<DataKvCreator> list = this.list;
		int size = list.size();
		ArrayList<DataKvEntityImpl> entityList = new ArrayList<DataKvEntityImpl>(size);
		for (int i = 0; i < size; i++) {
			DataKvCreator creator = list.get(i);
			PlayerCreatedProcessor processor = creator.getProcessor();
			Object pojo = processor.create(param);
			DataKVDao dao = creator.getDao();
			try {
				String json = dao.getClassInfo().toJson(pojo);
				DataKvEntityImpl entity = new DataKvEntityImpl(userId, json, creator.getType(), pojo);
				entityList.add(entity);
			} catch (Exception e) {
				GameLog.error("PlayerCreatedOperation", "#execute()", "创建角色转换操作异常：" + userId, e);
				return false;
			}
		}
		try {
			DataAccessFactory.getDataKvManager().batchInsert(userId, entityList);
			for (int i = 0; i < size; i++) {
				DataKvEntityImpl dataKvEntity = entityList.get(i);
				list.get(i).getDao().putIntoCache(userId, dataKvEntity.getPojo());
			}
		} catch (Throwable e) {
			GameLog.error("PlayerCreatedOperation", "#execute()", "创建角色数据库操作异常：" + userId, e);
			return false;
		}
		return true;
	}

}
