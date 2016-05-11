package com.rw.fsutil.cacheDao.loader;

import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataNotExistHandler;
import com.rw.fsutil.dao.kvdata.DataKvEntity;
import com.rw.fsutil.dao.kvdata.DataKvManager;
import com.rw.fsutil.dao.optimize.DataAccessFactory;

public class DataKvNotExistHandler<T> implements DataNotExistHandler<String, T> {

	private final Integer type;
	private final DataExtensionCreator<T> creator;
	private final ClassInfo classInfo;

	public DataKvNotExistHandler(Integer type, DataExtensionCreator<T> creator, ClassInfo classInfo) {
		this.type = type;
		this.creator = creator;
		this.classInfo = classInfo;
	}

	@Override
	public T callInLoadTask(final String key) {
		DataKvManager dataKvManager = DataAccessFactory.getDataKvManager();
		int count = dataKvManager.getDataKVRecordCount(key);
		if (count == 0) {
			return null;
		}
		T value = creator.create(key);
		try {
			final String json = classInfo.toJson(value);
			DataKvEntity entity = new DataKvEntity() {

				@Override
				public String getValue() {
					return json;
				}

				@Override
				public String getUserId() {
					return key;
				}

				@Override
				public Integer getType() {
					return type;
				}
			};
			boolean result = dataKvManager.insert(key, entity);
			if (result) {
				return value;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
