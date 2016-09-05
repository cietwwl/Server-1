package com.rw.dataaccess;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataaccess.impl.PlayerCreatedOperationImpl;
import com.rw.dataaccess.impl.PlayerLoadOperationImpl;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataCreator;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rwbase.common.MapItemStoreFactory;

@SuppressWarnings("rawtypes")
public class GameOperationFactory {

	private static PlayerCreatedOperationImpl operation;
	private static PlayerLoadOperationImpl loadOperation;
	private static PlayerLoadOperationImpl mapItemLoadOp;
	
	public static void init(int defaultCapacity) {
		DataKVType[] array = DataKVType.values();

		for (DataKVType type : array) {
			// 检查DataKVDao与UserGameDataProcessor的泛型是否一致
			if (getSuperclassGeneric(type.getDaoClass()) != getInterfacesGeneric(type.getCreatorClass())) {
				throw new ExceptionInInitializerError("DataKVDao与PlayerCreatedProcessor范型参数不一致:" + type.getDaoClass() + "," + type.getCreatorClass());
			}
		}
		int size = array.length;
		// 初始化dataKvMap：所有DAO与类型的对应关系
		Map<Integer, Class<? extends DataKVDao<?>>> dataKvMap = new HashMap<Integer, Class<? extends DataKVDao<?>>>();
		for (int i = 0; i < size; i++) {
			DataKVType type = array[i];
			dataKvMap.put(type.getType(), type.getDaoClass());
		}
		Map<Class<? extends DataKVDao<?>>, DataExtensionCreator<?>> extensionMap = new HashMap<Class<? extends DataKVDao<?>>, DataExtensionCreator<?>>();
		List<DataKvTypeEntity<PlayerCoreCreation<?>>> coreList = new ArrayList<DataKvTypeEntity<PlayerCoreCreation<?>>>();
		List<DataKvTypeEntity<DataExtensionCreator<?>>> extensionList = new ArrayList<DataKvTypeEntity<DataExtensionCreator<?>>>();

		// 临时缓存DataCreator实例
		HashMap<Class, DataCreator<?, ?>> creatorMap = new HashMap<Class, DataCreator<?, ?>>();
		for (int i = 0; i < size; i++) {
			DataKVType dataKVType = array[i];
			try {
				Class<? extends DataKVDao<?>> clz = dataKVType.getDaoClass();
				// 先行构造DataCreator实例
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
		List<DataKVType> kvTypeList = PreLoadType.getPreLoadKVType();
		int kvTypeSize = kvTypeList.size();
		int[] rangeArray = new int[kvTypeSize];
		for (int i = 0; i < kvTypeSize; i++) {
			rangeArray[i] = kvTypeList.get(i).getType();
		}
		// 初始化DataAccessFactory
		DataAccessFactory.init("dataSourceMT", dataKvMap, extensionMap, defaultCapacity, rangeArray,MapItemStoreFactory.getItemStoreInofs());
		// 接着初始化各个DAO实例，这两个有顺序依赖
		HashMap<Integer, DataKVDao<?>> cacheMap = new HashMap<Integer, DataKVDao<?>>();
		for (int i = 0; i < size; i++) {
			DataKVType dataKVType = array[i];
			try {
				Integer type = dataKVType.getType();
				Class<? extends DataKVDao<?>> clz = dataKVType.getDaoClass();
				// 根据现有规则，通过DAO内部静态方法获取DAO实例
				DataKVDao dao = null;
				Method[] methods = clz.getDeclaredMethods();
				for (Method method : methods) {
					if (method.getReturnType() == clz) {
						dao = (DataKVDao) method.invoke(null);
					}
				}
				if (dao == null) {
					throw new ExceptionInInitializerError("获取DAO实例失败：" + clz.getName());
				}
				cacheMap.put(type, dao);
				// 构造DataCreator实例
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
		// 初始化PlayerCreatedOperation
		operation = new PlayerCreatedOperationImpl(coreList, extensionList);
		loadOperation = new PlayerLoadOperationImpl(cacheMap);
	}

	public static PlayerCreatedOperation getCreatedOperation() {
		return operation;
	}
	
	public static PlayerLoadOperation getLoadOperation(){
		return loadOperation;
	}

	private static Class<?> getSuperclassGeneric(Class<?> clz) {
		Type type = clz.getGenericSuperclass();
		if (!(type instanceof ParameterizedType)) {
			throw new IllegalArgumentException("缺少父类的范型参数：" + clz);
		}
		ParameterizedType paramType = (ParameterizedType) type;
		return (Class<?>) paramType.getActualTypeArguments()[0];
	}

	private static Class<?> getInterfacesGeneric(Class<?> clz) {
		Class<?>[] interfaceClass = clz.getInterfaces();
		int index = -1;
		for (int i = 0; i < interfaceClass.length; i++) {
			Class<?> c = interfaceClass[i];
			if (c != DataExtensionCreator.class && c != PlayerCoreCreation.class) {
				continue;
			}
			index = i;
			break;
		}
		if (index == -1) {
			throw new IllegalArgumentException("缺少实现DataExtensionCreator or PlayerCoreCreation接口：" + clz);
		}
		Type[] typeArray = clz.getGenericInterfaces();
		Type type = typeArray[index];
		if (!(type instanceof ParameterizedType)) {
			throw new IllegalArgumentException("缺少实现接口的泛型参数：" + clz);
		}
		ParameterizedType paramType = (ParameterizedType) type;
		return (Class<?>) paramType.getActualTypeArguments()[0];
	}

}
