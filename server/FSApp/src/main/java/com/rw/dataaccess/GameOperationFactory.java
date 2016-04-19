package com.rw.dataaccess;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.rw.dataaccess.impl.PlayerCreatedOperationImpl;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.dao.optimize.DataAccessFactory;

public class GameOperationFactory {

	private static HashMap<Integer, DataKvCreator<?>> map;
	private static PlayerCreatedOperationImpl operation;

	public static void init(int defaultCapacity) {
		DataKVType[] array = DataKVType.values();
		int size = array.length;
		map = new HashMap<Integer, DataKvCreator<?>>(size);
		HashMap<Integer, Class<? extends DataKVDao>> dataKvMap = new HashMap<Integer, Class<? extends DataKVDao>>();
		ArrayList<DataKvCreator> list = new ArrayList<DataKvCreator>(size);
		for (int i = 0; i < size; i++) {
			DataKVType type = array[i];
			dataKvMap.put(type.getType(), type.getClazz());
		}
		DataAccessFactory.init(dataKvMap, defaultCapacity);
		for (int i = 0; i < size; i++) {
			DataKVType type = array[i];
			try {
				int t = type.getType();
				DataKVDao dao = null;
				Class<? extends DataKVDao<?>> clz = type.getClazz();
				Method[] methods = clz.getDeclaredMethods();
				for (Method method : methods) {
					if (method.getReturnType() == clz) {
						dao = (DataKVDao) method.invoke(null, null);
					}
				}

				PlayerCreatedProcessor<?> processor = type.getProcessorClass().newInstance();
				DataKvCreator<?> entity = new DataKvCreator(t, dao, processor);
				map.put(t, entity);
				list.add(entity);
			} catch (Throwable e) {
				throw new ExceptionInInitializerError(e);
			}
		}
		operation = new PlayerCreatedOperationImpl(list);
	}

	public static PlayerCreatedOperation getCreatedOperation() {
		return operation;
	}
}
