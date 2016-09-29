package com.rw.dataaccess.attachment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.common.HPCUtil;
import com.rw.dataaccess.hero.HeroCreateParam;
import com.rw.dataaccess.hero.HeroExtPropertyType;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.cacheDao.attachment.PlayerExtProperty;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyData;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.common.FastTuple;
import com.rw.fsutil.dao.attachment.QueryAttachmentEntry;
import com.rw.fsutil.dao.attachment.RoleExtPropertyManager;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.util.DateUtils;

public class RoleExtPropertyFactory {

	private static boolean init;
	private static RoleExtPropertyStoreCache<? extends PlayerExtProperty>[] playerExtCaches;
	private static FastTuple<Short, RoleExtPropertyCreator<PlayerExtProperty, Object>, RoleExtPropertyStoreCache<PlayerExtProperty>>[] playerExtCreators;

	private static RoleExtPropertyStoreCache<? extends PlayerExtProperty>[] heroExtCaches;
	private static FastTuple<Short, RoleExtPropertyCreator<PlayerExtProperty, Object>, RoleExtPropertyStoreCache<PlayerExtProperty>>[] heroExtCreators;

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
			RoleExtPropertyStoreCache<? extends PlayerExtProperty>[] array,
			FastTuple<Short, RoleExtPropertyCreator<PlayerExtProperty, Object>, RoleExtPropertyStoreCache<PlayerExtProperty>>[] creatorCacheTuple) {
		int len = typeList.length;
		for (int i = 0; i < len; i++) {
			RoleExtPropertyType propertyType = typeList[i];
			short type = propertyType.getType();
			String name = propertyType.getPropertyName();
			int capacity = propertyType.getCapacity();
			Class<? extends PlayerExtProperty> propertyClass = propertyType.getPropertyClass();
			if (capacity <= 0) {
				capacity = defaultCapacity;
			}
			RoleExtPropertyCreator<PlayerExtProperty, Object> creator;
			try {
				creator = (RoleExtPropertyCreator<PlayerExtProperty, Object>) propertyType.getCreatorClass().newInstance();
			} catch (Throwable e) {
				e.printStackTrace();
				throw new ExceptionInInitializerError(e);
			}
			RoleExtPropertyStoreCache<PlayerExtProperty> cache = new RoleExtPropertyStoreCache(extPropertyManager, propertyClass, name, capacity, datasourceName, type);
			array[propertyType.ordinal()] = cache;
			creatorCacheTuple[propertyType.ordinal()] = new FastTuple<Short, RoleExtPropertyCreator<PlayerExtProperty, Object>, RoleExtPropertyStoreCache<PlayerExtProperty>>(type, creator, cache);
		}
	}

	/**
	 * 获取指定类型的玩家的扩展属性缓存
	 * 
	 * @param type
	 * @param clazz
	 * @return
	 */
	public static <T extends PlayerExtProperty> RoleExtPropertyStoreCache<T> getPlayerExtCache(PlayerExtPropertyType type, Class<T> clazz) {
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
	public static <T extends PlayerExtProperty> RoleExtPropertyStoreCache<T> getHeroExtCache(HeroExtPropertyType type, Class<T> clazz) {
		// 这里不进行PlayerAttachmentType中类型是否和class类型一致的判断
		return (RoleExtPropertyStoreCache<T>) heroExtCaches[type.ordinal()];
	}

	public static void preload(RoleExtPropertyManager extPropertyManager, Object param, long currentTimeMillis, String roleId,
			RoleExtPropertyStoreCache<? extends PlayerExtProperty>[] playerExtCaches,
			FastTuple<Short, RoleExtPropertyCreator<PlayerExtProperty, Object>, RoleExtPropertyStoreCache<PlayerExtProperty>>[] playerExtCreators) {
		int len = playerExtCaches.length;
		ArrayList<FastTuple<Short, RoleExtPropertyCreator<PlayerExtProperty, Object>, RoleExtPropertyStoreCache<PlayerExtProperty>>> loadList = new ArrayList<FastTuple<Short, RoleExtPropertyCreator<PlayerExtProperty, Object>, RoleExtPropertyStoreCache<PlayerExtProperty>>>(
				len);
		ArrayList<Short> typeList = new ArrayList<Short>();
		for (int i = len; --i >= 0;) {
			FastTuple<Short, RoleExtPropertyCreator<PlayerExtProperty, Object>, RoleExtPropertyStoreCache<PlayerExtProperty>> tuple = playerExtCreators[i];
			RoleExtPropertyCreator<PlayerExtProperty, Object> creator = tuple.secondValue;
			// TODO 检查openLv
			if (!creator.validateOpenTime(currentTimeMillis)) {
				continue;
			}
			if (tuple.thirdValue.contains(roleId)) {
				continue;
			}
			loadList.add(tuple);
			typeList.add(tuple.firstValue);
		}
		if (typeList.isEmpty()) {
			return;
		}
		// load from database
		List<QueryAttachmentEntry> datas = extPropertyManager.loadRangeEntitys(roleId, typeList);
		HashMap<Short, ArrayList<QueryAttachmentEntry>> datasMap = new HashMap<Short, ArrayList<QueryAttachmentEntry>>();
		for (int i = datas.size(); --i >= 0;) {
			QueryAttachmentEntry entity = datas.get(i);
			Short type = entity.getType();
			ArrayList<QueryAttachmentEntry> list = datasMap.get(type);
			if (list == null) {
				list = new ArrayList<QueryAttachmentEntry>();
				datasMap.put(type, list);
			}
			list.add(entity);
		}
		int loadSize = loadList.size();
		if (datasMap.size() < loadSize) {
			ArrayList<PlayerExtCreateData> createList = new ArrayList<PlayerExtCreateData>(len);
			ArrayList<NewAttachmentInsertData<PlayerExtProperty>> insertDatas = new ArrayList<NewAttachmentInsertData<PlayerExtProperty>>();
			for (int i = loadSize; --i >= 0;) {
				FastTuple<Short, RoleExtPropertyCreator<PlayerExtProperty, Object>, RoleExtPropertyStoreCache<PlayerExtProperty>> tuple = loadList.get(i);
				Short type = tuple.firstValue;
				List<QueryAttachmentEntry> data = datasMap.get(type);
				if (data != null) {
					tuple.thirdValue.putIfAbsentByDBString(roleId, data);
				} else {
					createList.add(new PlayerExtCreateData(tuple.firstValue, tuple.secondValue, tuple.thirdValue));
				}
			}
			int createSize = createList.size();
			// 回调逻辑创建逻辑对象集
			for (int i = createSize; --i >= 0;) {
				PlayerExtCreateData createData = createList.get(i);
				List<PlayerExtProperty> createPropList = createData.creator.firstCreate(param);
				try {
					List<NewAttachmentInsertData<PlayerExtProperty>> insertData = convertNewEntry(createData.cache.getMapper(), roleId, createData.type, createPropList);
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
					NewAttachmentInsertData<?> insertData = insertDatas.get(i);
					insertData.setId(keys[i]);
				}
			} catch (Exception e) {
				FSUtilLogger.error("PlayerExtProperty create fail cause by insert into db", e);
			}

			// 生成新的PlayerExtProperty
			for (int i = createSize; --i >= 0;) {
				PlayerExtCreateData createData = createList.get(i);
				List<PlayerExtPropertyData<PlayerExtProperty>> list = create(createData.getDatas());
				RoleExtPropertyStoreCache<PlayerExtProperty> cache = createData.cache;
				cache.putIfAbsent(roleId, list);
			}
		} else {
			for (int i = loadSize; --i >= 0;) {
				FastTuple<Short, RoleExtPropertyCreator<PlayerExtProperty, Object>, RoleExtPropertyStoreCache<PlayerExtProperty>> tuple = loadList.get(i);
				Short type = tuple.firstValue;
				List<QueryAttachmentEntry> data = datasMap.get(type);
				if (data != null) {
					tuple.thirdValue.putIfAbsentByDBString(roleId, data);
				}
			}
		}
	}

	public static void loadAndCreateHeroExtProperty(String heroId, HeroCreateParam heroCreateParam) {
		preload(heroExtPropertyManager, heroCreateParam, DateUtils.getSecondLevelMillis(), heroId, heroExtCaches, heroExtCreators);
	}

	public static void loadAndCreatePlayerExtProperty(final String userId, final long createTime, final int level) {
		final long current = System.currentTimeMillis();
		PlayerPropertyParams param = new PlayerPropertyParams() {

			@Override
			public String getUserId() {
				return userId;
			}

			@Override
			public int getLevel() {
				return level;
			}

			@Override
			public long getCurrentTime() {
				return current;
			}

			@Override
			public long getCreateTime() {
				return createTime;
			}
		};
		preload(roleExtPropertyManager, param, current, userId, playerExtCaches, playerExtCreators);
	}

	public static List<NewAttachmentInsertData<PlayerExtProperty>> convertNewEntry(ObjectMapper mapper, String searchId, short type, List<PlayerExtProperty> itemList) throws JsonGenerationException,
			JsonMappingException, IOException {
		int size = itemList.size();
		ArrayList<NewAttachmentInsertData<PlayerExtProperty>> list = new ArrayList<NewAttachmentInsertData<PlayerExtProperty>>(size);
		for (int i = 0; i < size; i++) {
			list.add(convertInsertData(mapper, searchId, type, itemList.get(i)));
		}
		return list;
	}

	public static List<PlayerExtPropertyData<PlayerExtProperty>> create(List<NewAttachmentInsertData<PlayerExtProperty>> insertData) {
		int size = insertData.size();
		ArrayList<PlayerExtPropertyData<PlayerExtProperty>> result = new ArrayList<PlayerExtPropertyData<PlayerExtProperty>>(size);
		for (int i = 0; i < size; i++) {
			NewAttachmentInsertData<?> data = insertData.get(i);
			PlayerExtProperty t = data.getExtProperty();
			long key = data.getId();
			result.add(new PlayerExtPropertyData<PlayerExtProperty>(key, t));
		}
		return result;
	}

	public static <T extends PlayerExtProperty> NewAttachmentInsertData<T> convertInsertData(ObjectMapper mapper, String searchId, short type, T t) throws JsonGenerationException,
			JsonMappingException, IOException {
		String extension = mapper.writeValueAsString(t);
		NewAttachmentInsertData<T> entry = new NewAttachmentInsertData<T>(t, searchId, type, t.getId(), extension);
		return entry;
	}

}
