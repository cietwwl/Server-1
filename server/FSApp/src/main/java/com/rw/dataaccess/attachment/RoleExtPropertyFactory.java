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
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyData;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.attachment.QueryRoleExtPropertyData;
import com.rw.fsutil.dao.attachment.RoleExtPropertyManager;
import com.rw.fsutil.dao.optimize.DataAccessFactory;

public class RoleExtPropertyFactory {

	private static boolean init;
	private static RoleExtPropertyStoreCache<? extends RoleExtProperty>[] playerExtCaches;
	private static RoleExtCreateInfo[] playerExtCreators;

	private static RoleExtPropertyStoreCache<? extends RoleExtProperty>[] heroExtCaches;
	private static RoleExtCreateInfo[] heroExtCreators;

	private static RoleExtPropertyManager roleExtPropertyManager;
	private static RoleExtPropertyManager heroExtPropertyManager;

	public synchronized static void init(int defaultCapacity,int heroCapacity, String datasourceName) {
		if (init) {
			throw new ExceptionInInitializerError("duplicate init");
		}
		init = true;

		roleExtPropertyManager = DataAccessFactory.getRoleAttachmentManager();
		heroExtPropertyManager = DataAccessFactory.getHeroAttachmentManager();

		RoleExtPropertyType[] propertyTypeArray = PlayerExtPropertyType.values();
		HPCUtil.toMappedArray(propertyTypeArray, "type");
		playerExtCaches = new RoleExtPropertyStoreCache<?>[propertyTypeArray.length];
		playerExtCreators = new RoleExtCreateInfo[propertyTypeArray.length];
		init(roleExtPropertyManager, defaultCapacity, datasourceName, propertyTypeArray, playerExtCaches, playerExtCreators);

		propertyTypeArray = HeroExtPropertyType.values();
		HPCUtil.toMappedArray(propertyTypeArray, "type");
		heroExtCaches = new RoleExtPropertyStoreCache<?>[propertyTypeArray.length];
		heroExtCreators = new RoleExtCreateInfo[propertyTypeArray.length];
		init(heroExtPropertyManager, heroCapacity, datasourceName, propertyTypeArray, heroExtCaches, heroExtCreators);
	}

	public static void init(RoleExtPropertyManager extPropertyManager, int defaultCapacity, String datasourceName, RoleExtPropertyType[] typeList,
			RoleExtPropertyStoreCache<? extends RoleExtProperty>[] array, RoleExtCreateInfo[] createInfoArray) {
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
			createInfoArray[propertyType.ordinal()] = new RoleExtCreateInfo(type, creator, cache);
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

	static void firstCrate(RoleExtPropertyManager extPropertyManager, Object param, String roleId, RoleExtPropertyStoreCache<? extends RoleExtProperty>[] roleExtCaches,
			RoleExtCreateInfo[] roleExtCreators) {
		int len = roleExtCaches.length;
		ArrayList<RoleExtCreateInfoWrap> requiredLoadList = new ArrayList<RoleExtCreateInfoWrap>(len);
		for (int i = 0; i < len; i++) {
			RoleExtCreateInfo createInfo = roleExtCreators[i];
			RoleExtPropertyCreator<RoleExtProperty, Object> creator = createInfo.creator;
			// TODO 检查openLv
			if (!creator.requiredToPreload(param)) {
				continue;
			}
			requiredLoadList.add(new RoleExtCreateInfoWrap(createInfo));
		}
		firstCreateExpProperty(extPropertyManager, requiredLoadList, roleId, param);
	}

	/* 返回是否进行了数据库加载操作的时间戳,方便调用者做时间统计,如果没有进行数据库操作,返回0 */
	static long preload(RoleExtPropertyManager extPropertyManager, Object param, String roleId, RoleExtPropertyStoreCache<? extends RoleExtProperty>[] roleExtCaches,
			RoleExtCreateInfo[] roleExtCreators) {
		// long start = System.currentTimeMillis();
		int len = roleExtCreators.length;
		ArrayList<RoleExtCreateInfo> requiredLoadList = new ArrayList<RoleExtCreateInfo>(len);
		ArrayList<Short> typeList = new ArrayList<Short>(len);

		for (int i = 0; i < len; i++) {
			RoleExtCreateInfo createInfo = roleExtCreators[i];
			RoleExtPropertyCreator<RoleExtProperty, Object> creator = createInfo.creator;
			// TODO 检查openLv
			if (!creator.requiredToPreload(param)) {
				continue;
			}
			if (createInfo.cache.contains(roleId)) {
				continue;
			}
			requiredLoadList.add(createInfo);
			if (typeList != null) {
				typeList.add(createInfo.type);
			}
		}

		int size = typeList.size();
		if (size == 0) {
			return 0;
		}
		// load from database
		List<QueryRoleExtPropertyData> loadDatas;
		if (size == 1) {
			loadDatas = extPropertyManager.loadEntitys(roleId, typeList.get(0));
		} else if (size < roleExtCreators.length) {
			loadDatas = extPropertyManager.loadRangeEntitys(roleId, typeList);
		} else {
			loadDatas = extPropertyManager.loadAllEntitys(roleId);
		}
		long loadTimeStamp = System.currentTimeMillis();
		HashMap<Short, ArrayList<QueryRoleExtPropertyData>> loadDatasMap = new HashMap<Short, ArrayList<QueryRoleExtPropertyData>>();
		// 按类型分区从数据库中读取的数据
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
		int requireLoadSize = requiredLoadList.size();
		int actualLoadSize = loadDatasMap.size();
		if (actualLoadSize < requireLoadSize) {
			// 收集需要创建的RoleExtCreateInfo
			ArrayList<RoleExtCreateInfoWrap> createList = new ArrayList<RoleExtCreateInfoWrap>(requireLoadSize - actualLoadSize);
			for (int i = requireLoadSize; --i >= 0;) {
				RoleExtCreateInfo createInfo = requiredLoadList.get(i);
				List<QueryRoleExtPropertyData> data = loadDatasMap.get(createInfo.type);
				if (data != null) {
					createInfo.cache.putIfAbsentByDBString(roleId, data);
				} else {
					createList.add(new RoleExtCreateInfoWrap(createInfo));
				}
			}
			// 创建新记录
			firstCreateExpProperty(extPropertyManager, createList, roleId, param);
		} else {
			for (int i = requireLoadSize; --i >= 0;) {
				RoleExtCreateInfo createInfo = requiredLoadList.get(i);
				List<QueryRoleExtPropertyData> data = loadDatasMap.get(createInfo.type);
				if (data != null) {
					createInfo.cache.putIfAbsentByDBString(roleId, data);
				}
			}
		}
		return loadTimeStamp;
		// System.out.println("消耗:" + (System.currentTimeMillis() - start) + ","
		// + param);
	}

	// 收集需要加载的类型
	static ArrayList<RoleExtCreateInfo> collectRequireType(String roleId, RoleExtPropertyStoreCache<? extends RoleExtProperty>[] roleExtCaches, RoleExtCreateInfo[] roleExtCreators, Object param,
			ArrayList<Short> typeList) {
		int len = roleExtCaches.length;
		ArrayList<RoleExtCreateInfo> requiredLoadList = new ArrayList<RoleExtCreateInfo>(len);
		for (int i = 0; i < len; i++) {
			RoleExtCreateInfo createInfo = roleExtCreators[i];
			RoleExtPropertyCreator<RoleExtProperty, Object> creator = createInfo.creator;
			// TODO 检查openLv
			if (!creator.requiredToPreload(param)) {
				continue;
			}
			if (createInfo.cache.contains(roleId)) {
				continue;
			}
			requiredLoadList.add(createInfo);
			if (typeList != null) {
				typeList.add(createInfo.type);
			}
		}
		return requiredLoadList;
	}

	// 首次创建扩展属性
	static void firstCreateExpProperty(RoleExtPropertyManager extPropertyManager, ArrayList<RoleExtCreateInfoWrap> createList, String roleId, Object param) {
		ArrayList<InsertRoleExtDataWrap<RoleExtProperty>> insertDatas = new ArrayList<InsertRoleExtDataWrap<RoleExtProperty>>();
		int createSize = createList.size();
		// 回调逻辑创建逻辑对象集
		for (int i = createSize; --i >= 0;) {
			RoleExtCreateInfoWrap createWrap = createList.get(i);
			List<RoleExtProperty> createPropList = createWrap.createInfo.creator.firstCreate(param);
			if (createPropList == null) {
				continue;
			}
			RoleExtPropertyStoreCache<RoleExtProperty> cache = createWrap.createInfo.cache;
			// 创建一个没有记录的对象
			if (createPropList.isEmpty()) {
				cache.putIfAbsent(roleId, Collections.<RoleExtPropertyData<RoleExtProperty>> emptyList());
				continue;
			}
			try {
				// 收集不同类型的创建记录
				List<InsertRoleExtDataWrap<RoleExtProperty>> insertData = convertNewEntry(cache.getMapper(), roleId, createWrap.createInfo.type, createPropList);
				createWrap.setDatas(insertData);
				insertDatas.addAll(insertData);
			} catch (Exception e) {
				FSUtilLogger.error("PlayerExtProperty create fail:" + createWrap.createInfo.type, e);
			}
		}
		if (insertDatas.isEmpty()) {
			return;
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
			RoleExtCreateInfoWrap createWrap = createList.get(i);
			List<InsertRoleExtDataWrap<RoleExtProperty>> insertData = createWrap.getDatas();
			if (insertData == null) {
				continue;
			}
			List<RoleExtPropertyData<RoleExtProperty>> list = create(insertData);
			createWrap.createInfo.cache.putIfAbsent(roleId, list);
		}
	}

	/**
	 * 初次创建角色扩展属性
	 * 
	 * @param userId
	 * @param createTime
	 * @param level
	 */
	public static void firstCreatePlayerExtProperty(String userId, long createTime, int level) {
		PlayerPropertyParams param = new PlayerPropertyParams(userId, level, createTime, System.currentTimeMillis());
		firstCrate(roleExtPropertyManager, param, userId, playerExtCaches, playerExtCreators);
	}

	/**
	 * 加载并创建角色相关属性(如果需要创建)
	 * 
	 * @param userId
	 * @param createTime
	 * @param level
	 */
	public static long loadAndCreatePlayerExtProperty(String userId, long createTime, int level) {
		PlayerPropertyParams param = new PlayerPropertyParams(userId, level, createTime, System.currentTimeMillis());
		return preload(roleExtPropertyManager, param, userId, playerExtCaches, playerExtCreators);
	}

	/**
	 * 初次创建英雄相关属性
	 * 
	 * @param heroId
	 * @param heroCreateParam
	 */
	public static void fristCreateHeroExtProperty(String heroId, HeroCreateParam heroCreateParam) {
		firstCrate(heroExtPropertyManager, heroCreateParam, heroId, heroExtCaches, heroExtCreators);
	}

	/**
	 * 加载并创建英雄相关属性(如果需要创建)
	 * 
	 * @param heroId
	 * @param heroCreateParam
	 */
	public static long loadAndCreateHeroExtProperty(String heroId, HeroCreateParam heroCreateParam) {
		return preload(heroExtPropertyManager, heroCreateParam, heroId, heroExtCaches, heroExtCreators);
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

	public static List<RoleExtPropertyData<RoleExtProperty>> create(List<InsertRoleExtDataWrap<RoleExtProperty>> insertData) {
		if(insertData == null){
			return Collections.EMPTY_LIST;
		}
		int size = insertData.size();
		ArrayList<RoleExtPropertyData<RoleExtProperty>> result = new ArrayList<RoleExtPropertyData<RoleExtProperty>>(size);
		for (int i = 0; i < size; i++) {
			InsertRoleExtDataWrap<?> data = insertData.get(i);
			RoleExtProperty t = data.getExtProperty();
			long key = data.getId();
			result.add(new RoleExtPropertyData<RoleExtProperty>(key, t));
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
