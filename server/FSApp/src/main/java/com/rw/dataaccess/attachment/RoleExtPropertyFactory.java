package com.rw.dataaccess.attachment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.common.HPCUtil;
import com.rw.dataaccess.hero.HeroCreateParam;
import com.rw.dataaccess.hero.HeroExtPropertyType;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyData;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.common.FastTuple;
import com.rw.fsutil.dao.attachment.QueryRoleExtPropertyData;
import com.rw.fsutil.dao.attachment.RoleExtPropertyManager;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.util.DateUtils;

public class RoleExtPropertyFactory {

	private static boolean init;
	private static RoleExtPropertyStoreCache<? extends RoleExtProperty>[] playerExtCaches;
	private static FastTuple<Short, RoleExtPropertyCreator<RoleExtProperty, Object>, RoleExtPropertyStoreCache<RoleExtProperty>>[] playerExtCreators;

	private static RoleExtPropertyStoreCache<? extends RoleExtProperty>[] heroExtCaches;
	private static FastTuple<Short, RoleExtPropertyCreator<RoleExtProperty, Object>, RoleExtPropertyStoreCache<RoleExtProperty>>[] heroExtCreators;

	private static RoleExtPropertyManager roleExtPropertyManager;
	private static RoleExtPropertyManager heroExtPropertyManager;

	@SuppressWarnings("unchecked")
	public synchronized static void init(int defaultCapacity, String datasourceName) {
		if (init) {
			throw new ExceptionInInitializerError("duplicate init");
		}
		init = true;

		roleExtPropertyManager = DataAccessFactory.getRoleAttachmentManager();
		heroExtPropertyManager = DataAccessFactory.getHeroAttachmentManager();

		RoleExtPropertyType[] propertyTypeArray = PlayerExtPropertyType.values();
		HPCUtil.toMappedArray(propertyTypeArray, "type");
		playerExtCaches = new RoleExtPropertyStoreCache<?>[propertyTypeArray.length];
		playerExtCreators = new FastTuple[propertyTypeArray.length];
		init(roleExtPropertyManager, defaultCapacity, datasourceName, propertyTypeArray, playerExtCaches, playerExtCreators);

		propertyTypeArray = HeroExtPropertyType.values();
		HPCUtil.toMappedArray(propertyTypeArray, "type");
		heroExtCaches = new RoleExtPropertyStoreCache<?>[propertyTypeArray.length];
		heroExtCreators = new FastTuple[propertyTypeArray.length];
		init(heroExtPropertyManager, defaultCapacity, datasourceName, propertyTypeArray, heroExtCaches, heroExtCreators);
	}

	public static void init(RoleExtPropertyManager extPropertyManager, int defaultCapacity, String datasourceName, RoleExtPropertyType[] typeList,
			RoleExtPropertyStoreCache<? extends RoleExtProperty>[] array,
			FastTuple<Short, RoleExtPropertyCreator<RoleExtProperty, Object>, RoleExtPropertyStoreCache<RoleExtProperty>>[] creatorCacheTuple) {
		int len = typeList.length;
		for (int i = 0; i < len; i++) {
			RoleExtPropertyType propertyType = typeList[i];
			short type = propertyType.getType();
			String name = propertyType.getPropertyName();
			int capacity = propertyType.getCapacity();
			Class<? extends RoleExtProperty> propertyClass = propertyType.getPropertyClass();
			if (capacity <= 0) {
				capacity = defaultCapacity;
			}
			RoleExtPropertyCreator<RoleExtProperty, Object> creator;
			try {
				creator = (RoleExtPropertyCreator<RoleExtProperty, Object>) propertyType.getCreatorClass().newInstance();
			} catch (Throwable e) {
				e.printStackTrace();
				throw new ExceptionInInitializerError(e);
			}
			RoleExtPropertyStoreCache<RoleExtProperty> cache = new RoleExtPropertyStoreCache(extPropertyManager, propertyClass, name, capacity, datasourceName, type);
			array[propertyType.ordinal()] = cache;
			creatorCacheTuple[propertyType.ordinal()] = new FastTuple<Short, RoleExtPropertyCreator<RoleExtProperty, Object>, RoleExtPropertyStoreCache<RoleExtProperty>>(type, creator, cache);
		}
	}

	/**
	 * 获取指定类型的玩家的扩展属性缓存
	 * 
	 * @param type
	 * @param clazz
	 * @return
	 */
	public static <T extends RoleExtProperty> RoleExtPropertyStoreCache<T> getPlayerExtCache(PlayerExtPropertyType type, Class<T> clazz) {
		// 这里不进行PlayerAttachmentType中类型是否和class类型一致的判断
		return (RoleExtPropertyStoreCache<T>) playerExtCaches[type.ordinal()];
	}

	/**
	 * 获取指定类型的英雄扩展属性缓存
	 * 
	 * @param type
	 * @param clazz
	 * @return
	 */
	public static <T extends RoleExtProperty> RoleExtPropertyStoreCache<T> getHeroExtCache(HeroExtPropertyType type, Class<T> clazz) {
		// 这里不进行PlayerAttachmentType中类型是否和class类型一致的判断
		return (RoleExtPropertyStoreCache<T>) heroExtCaches[type.ordinal()];
	}

	private static List<RoleExtProperty> checkAndCreate(String roleId, PlayerExtPropertyStore<RoleExtProperty> store, RoleExtPropertyCreator<RoleExtProperty, Object> creator, Object param) {
		try {
			return creator.checkAndCreate(store, param);
		} catch (Exception e) {
			FSUtilLogger.error("check roleExt exception:" + roleId, e);
			return Collections.emptyList();
		}
	}

	public static void preload(RoleExtPropertyManager extPropertyManager, Object param, long currentTimeMillis, String roleId, RoleExtPropertyStoreCache<? extends RoleExtProperty>[] playerExtCaches,
			FastTuple<Short, RoleExtPropertyCreator<RoleExtProperty, Object>, RoleExtPropertyStoreCache<RoleExtProperty>>[] playerExtCreators) {
//		long start = System.currentTimeMillis();
		int len = playerExtCaches.length;
		ArrayList<FastTuple<Short, RoleExtPropertyCreator<RoleExtProperty, Object>, RoleExtPropertyStoreCache<RoleExtProperty>>> loadList = new ArrayList<FastTuple<Short, RoleExtPropertyCreator<RoleExtProperty, Object>, RoleExtPropertyStoreCache<RoleExtProperty>>>(
				len);
		ArrayList<Short> typeList = new ArrayList<Short>();
		for (int i = len; --i >= 0;) {
			FastTuple<Short, RoleExtPropertyCreator<RoleExtProperty, Object>, RoleExtPropertyStoreCache<RoleExtProperty>> tuple = playerExtCreators[i];
			RoleExtPropertyCreator<RoleExtProperty, Object> creator = tuple.secondValue;
			// TODO 检查openLv
			if (!creator.requiredToPreload(param)) {
				continue;
			}
			if (tuple.thirdValue.contains(roleId)) {
				continue;
			}
			loadList.add(tuple);
			typeList.add(tuple.firstValue);
		}
		int size = typeList.size();
		if (size == 0) {
			return;
		}
		// load from database
		List<QueryRoleExtPropertyData> loadDatas;
		if (size < playerExtCreators.length) {
			loadDatas = extPropertyManager.loadRangeEntitys(roleId, typeList);
		} else {
			loadDatas = extPropertyManager.loadAllEntitys(roleId);
		}
		HashMap<Short, ArrayList<QueryRoleExtPropertyData>> loadDatasMap = new HashMap<Short, ArrayList<QueryRoleExtPropertyData>>();
		// 按类型分区
		for (int i = loadDatas.size(); --i >= 0;) {
			QueryRoleExtPropertyData entity = loadDatas.get(i);
			Short type = entity.getType();
			ArrayList<QueryRoleExtPropertyData> list = loadDatasMap.get(type);
			if (list == null) {
				list = new ArrayList<QueryRoleExtPropertyData>();
				loadDatasMap.put(type, list);
			}
			list.add(entity);
		}
		int loadSize = loadList.size();
		if (loadDatasMap.size() < loadSize) {
			// 收集需要创建的RoleExtCreateData
			ArrayList<RoleExtCreateData> createList = new ArrayList<RoleExtCreateData>(len);
			ArrayList<InsertRoleExtDataWrap<RoleExtProperty>> insertDatas = new ArrayList<InsertRoleExtDataWrap<RoleExtProperty>>();
			for (int i = loadSize; --i >= 0;) {
				FastTuple<Short, RoleExtPropertyCreator<RoleExtProperty, Object>, RoleExtPropertyStoreCache<RoleExtProperty>> tuple = loadList.get(i);
				Short type = tuple.firstValue;
				List<QueryRoleExtPropertyData> data = loadDatasMap.get(type);
				if (data != null) {
					tuple.thirdValue.putIfAbsentByDBString(roleId, data);
				} else {
					createList.add(new RoleExtCreateData(tuple.firstValue, tuple.secondValue, tuple.thirdValue));
				}
			}
			int createSize = createList.size();
			// 回调逻辑创建逻辑对象集
			for (int i = createSize; --i >= 0;) {
				RoleExtCreateData createData = createList.get(i);
				List<RoleExtProperty> createPropList = createData.creator.firstCreate(param);
				if (createPropList == null) {
					continue;
				}
				// 创建一个没有记录的对象
				if (createPropList.isEmpty()) {
					createData.cache.putIfAbsent(roleId, Collections.<PlayerExtPropertyData<RoleExtProperty>> emptyList());
					continue;
				}
				try {
					// 收集不同类型的创建记录
					List<InsertRoleExtDataWrap<RoleExtProperty>> insertData = convertNewEntry(createData.cache.getMapper(), roleId, createData.type, createPropList);
					createData.setDatas(insertData);
					insertDatas.addAll(insertData);
				} catch (Exception e) {
					FSUtilLogger.error("PlayerExtProperty create fail:" + createData.type, e);
				}
			}
			try {
				// 插入新记录到数据库并生成id，赋值
				long[] keys = extPropertyManager.insert(roleId, insertDatas);
				for (int i = insertDatas.size(); --i >= 0;) {
					InsertRoleExtDataWrap<?> insertData = insertDatas.get(i);
					insertData.setId(keys[i]);
				}
			} catch (Exception e) {
				FSUtilLogger.error("PlayerExtProperty create fail cause by insert into db:", e);
			}

			// 生成新的PlayerExtProperty
			for (int i = createSize; --i >= 0;) {
				RoleExtCreateData createData = createList.get(i);
				List<InsertRoleExtDataWrap<RoleExtProperty>> insertData = createData.getDatas();
				if (insertData == null) {
					continue;
				}
				List<PlayerExtPropertyData<RoleExtProperty>> list = create(insertData);
				RoleExtPropertyStoreCache<RoleExtProperty> cache = createData.cache;
				cache.putIfAbsent(roleId, list);
			}
		} else {
			for (int i = loadSize; --i >= 0;) {
				FastTuple<Short, RoleExtPropertyCreator<RoleExtProperty, Object>, RoleExtPropertyStoreCache<RoleExtProperty>> tuple = loadList.get(i);
				Short type = tuple.firstValue;
				List<QueryRoleExtPropertyData> data = loadDatasMap.get(type);
				if (data != null) {
					tuple.thirdValue.putIfAbsentByDBString(roleId, data);
				}
			}
		}
//		System.out.println("消耗:" + (System.currentTimeMillis() - start) + "," + param);
	}

	public static void loadAndCreateHeroExtProperty(String heroId, HeroCreateParam heroCreateParam) {
		preload(heroExtPropertyManager, heroCreateParam, DateUtils.getSecondLevelMillis(), heroId, heroExtCaches, heroExtCreators);
	}

	public static void loadAndCreatePlayerExtProperty(final String userId, final long createTime, final int level) {
		long current = System.currentTimeMillis();
		PlayerPropertyParams param = new PlayerPropertyParams(userId, level, createTime, current);
		preload(roleExtPropertyManager, param, current, userId, playerExtCaches, playerExtCreators);
	}

	public static List<InsertRoleExtDataWrap<RoleExtProperty>> convertNewEntry(ObjectMapper mapper, String searchId, short type, List<RoleExtProperty> itemList) throws JsonGenerationException,
			JsonMappingException, IOException {
		int size = itemList.size();
		ArrayList<InsertRoleExtDataWrap<RoleExtProperty>> list = new ArrayList<InsertRoleExtDataWrap<RoleExtProperty>>(size);
		for (int i = 0; i < size; i++) {
			list.add(convertInsertData(mapper, searchId, type, itemList.get(i)));
		}
		return list;
	}

	public static List<PlayerExtPropertyData<RoleExtProperty>> create(List<InsertRoleExtDataWrap<RoleExtProperty>> insertData) {
		int size = insertData.size();
		ArrayList<PlayerExtPropertyData<RoleExtProperty>> result = new ArrayList<PlayerExtPropertyData<RoleExtProperty>>(size);
		for (int i = 0; i < size; i++) {
			InsertRoleExtDataWrap<?> data = insertData.get(i);
			RoleExtProperty t = data.getExtProperty();
			long key = data.getId();
			result.add(new PlayerExtPropertyData<RoleExtProperty>(key, t));
		}
		return result;
	}

	public static <T extends RoleExtProperty> InsertRoleExtDataWrap<T> convertInsertData(ObjectMapper mapper, String searchId, short type, T t) throws JsonGenerationException, JsonMappingException,
			IOException {
		String extension = mapper.writeValueAsString(t);
		InsertRoleExtDataWrap<T> entry = new InsertRoleExtDataWrap<T>(t, searchId, type, t.getId(), extension);
		return entry;
	}

}
