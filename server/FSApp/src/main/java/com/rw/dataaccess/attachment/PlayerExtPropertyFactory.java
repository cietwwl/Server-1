package com.rw.dataaccess.attachment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.cacheDao.attachment.PlayerExtProperty;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyData;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStoreCache;
import com.rw.fsutil.common.FastTuple;
import com.rw.fsutil.dao.attachment.QueryAttachmentEntry;
import com.rw.fsutil.dao.optimize.DataAccessFactory;

public class PlayerExtPropertyFactory {

	private static PlayerExtPropertyStoreCache<? extends PlayerExtProperty>[] array;
	private static boolean init;
	private static FastTuple<Short, PlayerExtPropertyCreator<PlayerExtProperty>, PlayerExtPropertyStoreCache<PlayerExtProperty>>[] creatorCacheTuple;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized static void init(int defaultCapacity, String datasourceName) {
		if (init) {
			throw new ExceptionInInitializerError("duplicate init");
		}
		init = true;
		PlayerExtPropertyType[] propertyTypeArray = PlayerExtPropertyType.values();
		int len = propertyTypeArray.length;
		array = new PlayerExtPropertyStoreCache<?>[len];
		creatorCacheTuple = new FastTuple[len];
		for (int i = 0; i < len; i++) {
			PlayerExtPropertyType propertyType = propertyTypeArray[i];
			short type = propertyType.getType();
			String name = propertyType.getPropertyName();
			int capacity = propertyType.getCapacity();
			Class<? extends PlayerExtProperty> propertyClass = propertyType.getPropertyClass();
			if (capacity <= 0) {
				capacity = defaultCapacity;
			}
			PlayerExtPropertyCreator<PlayerExtProperty> creator;
			try {
				creator = (PlayerExtPropertyCreator<PlayerExtProperty>) propertyType.getCreatorClass().newInstance();
			} catch (Throwable e) {
				e.printStackTrace();
				throw new ExceptionInInitializerError(e);
			}
			PlayerExtPropertyStoreCache<PlayerExtProperty> cache = new PlayerExtPropertyStoreCache(propertyClass, name, capacity, datasourceName, type);
			array[propertyType.ordinal()] = cache;
			creatorCacheTuple[propertyType.ordinal()] = new FastTuple<Short, PlayerExtPropertyCreator<PlayerExtProperty>, PlayerExtPropertyStoreCache<PlayerExtProperty>>(type, creator, cache);
		}
	}

	public static <T extends PlayerExtProperty> PlayerExtPropertyStoreCache<T> get(PlayerExtPropertyType type, Class<T> clazz) {
		// 这里不进行PlayerAttachmentType中类型是否和class类型一致的判断
		return (PlayerExtPropertyStoreCache<T>) array[type.ordinal()];
	}

	public static void preloadAndCreateProperty(final String userId, final long createTime, final int level) {
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
				return System.currentTimeMillis();
			}

			@Override
			public long getCreateTime() {
				return createTime;
			}
		};
		int len = array.length;
		long currentTimeMillis = param.getCurrentTime();
		ArrayList<FastTuple<Short, PlayerExtPropertyCreator<PlayerExtProperty>, PlayerExtPropertyStoreCache<PlayerExtProperty>>> loadList = new ArrayList<FastTuple<Short, PlayerExtPropertyCreator<PlayerExtProperty>, PlayerExtPropertyStoreCache<PlayerExtProperty>>>(
				len);
		ArrayList<Short> typeList = new ArrayList<Short>();
		for (int i = len; --i >= 0;) {
			FastTuple<Short, PlayerExtPropertyCreator<PlayerExtProperty>, PlayerExtPropertyStoreCache<PlayerExtProperty>> tuple = creatorCacheTuple[i];
			PlayerExtPropertyCreator<?> creator = tuple.secondValue;
			// TODO 检查openLv
			if (!creator.validateOpenTime(currentTimeMillis)) {
				continue;
			}
			if (tuple.thirdValue.contains(userId)) {
				continue;
			}
			loadList.add(tuple);
			typeList.add(tuple.firstValue);
		}
		if (typeList.isEmpty()) {
			return;
		}
		// load from database
		List<QueryAttachmentEntry> datas = DataAccessFactory.getRoleAttachmentManager().loadRangeEntitys(userId, typeList);
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
				FastTuple<Short, PlayerExtPropertyCreator<PlayerExtProperty>, PlayerExtPropertyStoreCache<PlayerExtProperty>> tuple = loadList.get(i);
				Short type = tuple.firstValue;
				List<QueryAttachmentEntry> data = datasMap.get(type);
				if (data != null) {
					tuple.thirdValue.putIfAbsentByDBString(userId, data);
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
					List<NewAttachmentInsertData<PlayerExtProperty>> insertData = convertNewEntry(createData.cache.getMapper(), userId, createData.type, createPropList);
					createData.setDatas(insertData);
					insertDatas.addAll(insertData);
				} catch (Exception e) {
					FSUtilLogger.error("PlayerExtProperty create fail:" + createData.type, e);
				}
			}
			try {
				// 插入新记录到数据库并生成id，赋值
				long[] keys = DataAccessFactory.getRoleAttachmentManager().insert(userId, insertDatas);
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
				PlayerExtPropertyStoreCache<PlayerExtProperty> cache = createData.cache;
				cache.putIfAbsent(userId, list);
				try {
					System.out.println(cache.getAttachmentStore(userId).get(10));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			for (int i = loadSize; --i >= 0;) {
				FastTuple<Short, PlayerExtPropertyCreator<PlayerExtProperty>, PlayerExtPropertyStoreCache<PlayerExtProperty>> tuple = loadList.get(i);
				Short type = tuple.firstValue;
				List<QueryAttachmentEntry> data = datasMap.get(type);
				if (data != null) {
					tuple.thirdValue.putIfAbsentByDBString(userId, data);
					try {
						System.out.println(tuple.thirdValue.getAttachmentStore(userId).get(10));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static List<NewAttachmentInsertData<PlayerExtProperty>> convertNewEntry(ObjectMapper mapper, String searchId, short type, List<PlayerExtProperty> itemList) throws JsonGenerationException,
			JsonMappingException, IOException {
		int size = itemList.size();
		ArrayList<NewAttachmentInsertData<PlayerExtProperty>> list = new ArrayList<NewAttachmentInsertData<PlayerExtProperty>>(size);
		for (int i = 0; i < size; i++) {
			list.add(convert(mapper, searchId, type, itemList.get(i)));
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

	private static <T extends PlayerExtProperty> NewAttachmentInsertData<T> convert(ObjectMapper mapper, String searchId, short type, T t) throws JsonGenerationException, JsonMappingException,
			IOException {
		String extension = mapper.writeValueAsString(t);
		NewAttachmentInsertData<T> entry = new NewAttachmentInsertData<T>(t, searchId, type, t.getId(), extension);
		return entry;
	}

}
