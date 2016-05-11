package com.rw.dataaccess;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataaccess.impl.PlayerCreatedOperationImpl;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataCreator;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.fsutil.dao.optimize.DataAccessFactory;

@SuppressWarnings("rawtypes")
public class GameOperationFactory {

	private static PlayerCreatedOperationImpl operation;

	public static void init(int defaultCapacity) {
		DataKVType[] array = DataKVType.values();
		int size = array.length;
		//初始化dataKvMap：所有DAO与类型的对应关系
		Map<Integer, Class<? extends DataKVDao<?>>> dataKvMap = new HashMap<Integer, Class<? extends DataKVDao<?>>>();
		for (int i = 0; i < size; i++) {
			DataKVType type = array[i];
			dataKvMap.put(type.getType(), type.getClazz());
		}
		Map<Class<? extends DataKVDao<?>>, DataExtensionCreator<?>> extensionMap = new HashMap<Class<? extends DataKVDao<?>>, DataExtensionCreator<?>>();
		List<DataKvTypeEntity<PlayerCoreCreation<?>>> coreList = new ArrayList<DataKvTypeEntity<PlayerCoreCreation<?>>>();
		List<DataKvTypeEntity<DataExtensionCreator<?>>> extensionList = new ArrayList<DataKvTypeEntity<DataExtensionCreator<?>>>();

		//临时缓存DataCreator实例
		HashMap<Class, DataCreator<?, ?>> creatorMap = new HashMap<Class, DataCreator<?,?>>();
		for (int i = 0; i < size; i++) {
			DataKVType dataKVType = array[i];
			try {
				Class<? extends DataKVDao<?>> clz = dataKVType.getClazz();
				//先行构造DataCreator实例
				DataCreator<?, ?> creator = dataKVType.getCreatorClass().newInstance();
				creatorMap.put(clz, creator);
				if (creator instanceof DataExtensionCreator<?>) {
					DataExtensionCreator<?> extCreator = (DataExtensionCreator<?>) creator;
					extensionMap.put(clz, extCreator);
				} 
			} catch (Throwable e) {
				throw new ExceptionInInitializerError(e);
			}
		}
		//初始化DataAccessFactory
		DataAccessFactory.init(dataKvMap, extensionMap, defaultCapacity);
		//接着初始化各个DAO实例，这两个有顺序依赖
		for (int i = 0; i < size; i++) {
			DataKVType dataKVType = array[i];
			try {
				Integer type = dataKVType.getType();
				Class<? extends DataKVDao<?>> clz = dataKVType.getClazz();
				//根据现有规则，通过DAO内部静态方法获取DAO实例
				DataKVDao dao = null;
				Method[] methods = clz.getDeclaredMethods();
				for (Method method : methods) {
					if (method.getReturnType() == clz) {
						dao = (DataKVDao) method.invoke(null, null);
					}
				}
				if (dao == null) {
					throw new ExceptionInInitializerError("获取DAO实例失败：" + clz.getName());
				}
				//构造DataCreator实例
				DataCreator<?, ?> creator = creatorMap.get(clz);
				if (creator instanceof DataExtensionCreator<?>) {
					DataExtensionCreator<?> extCreator = (DataExtensionCreator<?>) creator;
					DataKvTypeEntity<DataExtensionCreator<?>> entity = new DataKvTypeEntity<DataExtensionCreator<?>>(type, dao, extCreator);
					extensionList.add(entity);
				} else if (creator instanceof PlayerCoreCreation<?>) {
					PlayerCoreCreation<?> coreCreation = (PlayerCoreCreation<?>) creator;
					DataKvTypeEntity<PlayerCoreCreation<?>> entity = new DataKvTypeEntity<PlayerCoreCreation<?>>(type, dao, coreCreation);
					coreList.add(entity);
				} else {
					throw new ExceptionInInitializerError("Illegal dataCreator:" + dataKVType.getCreatorClass());
				}
			} catch (Throwable e) {
				throw new ExceptionInInitializerError(e);
			}
		}
		//初始化PlayerCreatedOperation
		operation = new PlayerCreatedOperationImpl(coreList, extensionList);
	}

	public static PlayerCreatedOperation getCreatedOperation() {
		return operation;
	}
	
}
