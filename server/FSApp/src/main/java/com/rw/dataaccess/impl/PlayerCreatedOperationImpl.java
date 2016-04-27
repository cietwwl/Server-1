package com.rw.dataaccess.impl;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.rw.dataaccess.DataKvTypeEntity;
import com.rw.dataaccess.PlayerCreatedOperation;
import com.rw.dataaccess.PlayerParam;
import com.rw.dataaccess.PlayerCoreCreation;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataCreator;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.fsutil.dao.optimize.DataAccessFactory;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class PlayerCreatedOperationImpl implements PlayerCreatedOperation {

	private final List<DataKvTypeEntity<PlayerCoreCreation<?>>> coreList;
	private final List<DataKvTypeEntity<DataExtensionCreator<?>>> extensionList;

	public PlayerCreatedOperationImpl(List<DataKvTypeEntity<PlayerCoreCreation<?>>> coreList, List<DataKvTypeEntity<DataExtensionCreator<?>>> extensionList) {
		this.coreList = new ArrayList<DataKvTypeEntity<PlayerCoreCreation<?>>>(coreList);
		this.extensionList = new ArrayList<DataKvTypeEntity<DataExtensionCreator<?>>>(extensionList);
	}

	@Override
	public boolean execute(PlayerParam param) {
		// insert database and put into cache
		String userId = param.getUserId();
		List<DataKvTypeEntity<PlayerCoreCreation<?>>> coreList = this.coreList;
		List<DataKvTypeEntity<DataExtensionCreator<?>>> extList = this.extensionList;

		int coreSize = coreList.size();
		int extSize = extList.size();
		int total = coreSize + extSize;
		ArrayList<DataKvEntityImpl> entityList = new ArrayList<DataKvEntityImpl>(total);
		//create data by PlayerCoreCreation
		for (int i = 0; i < coreSize; i++) {
			DataKvTypeEntity<PlayerCoreCreation<?>> creator = coreList.get(i);
			if (!create(userId, entityList, param, creator)) {
				return false;
			}
		}
		//create data by DataExtensionCreator
		for (int i = 0; i < extSize; i++) {
			DataKvTypeEntity<DataExtensionCreator<?>> creator = extList.get(i);
			if (!create(userId, entityList, userId, creator)) {
				return false;
			}
		}
		try {
			//insert into db
			DataAccessFactory.getDataKvManager().batchInsert(userId, entityList);
			//put into cache safety
			for (int i = 0; i < total; i++) {
				DataKvEntityImpl dataKvEntity = entityList.get(i);
				dataKvEntity.getDataKVDao().putIntoCache(userId, dataKvEntity.getPojo());
			}
		} catch (Throwable e) {
			GameLog.error("PlayerCreatedOperation", "#execute()", "创建角色数据库操作异常：" + userId, e);
			return false;
		}
		return true;
	}

	/** create data and transform to JSON **/
	private <P> boolean create(String userId, ArrayList<DataKvEntityImpl> entityList, P param, DataKvTypeEntity<? extends DataCreator<?, P>> typeEntity) {
		DataCreator<?, P> processor = typeEntity.getDataCreator();
		Object pojo = processor.create(param);
		DataKVDao<?> dao = typeEntity.getDao();
		try {
			String json = dao.getClassInfo().toJson(pojo);
			DataKvEntityImpl entity = new DataKvEntityImpl(userId, json, typeEntity.getType(), pojo, dao);
			entityList.add(entity);
		} catch (Exception e) {
			GameLog.error("PlayerCreatedOperation", "#execute()", "创建角色转换操作异常：" + userId, e);
			return false;
		}
		return true;
	}

}
