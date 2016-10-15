package com.rw.fsutil.cacheDao.attachment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.cacheDao.mapItem.MapItemUpdater;
import com.rw.fsutil.common.NameFilterIntrospector;
import com.rw.fsutil.dao.annotation.ClassHelper;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.annotation.OwnerId;
import com.rw.fsutil.dao.attachment.QueryRoleExtPropertyData;
import com.rw.fsutil.dao.attachment.RoleExtPropertyManager;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.MapItemCache;
import com.rw.fsutil.dao.optimize.CacheCompositKey;
import com.rw.fsutil.dao.optimize.DAOStoreCache;
import com.rw.fsutil.dao.optimize.DoubleKey;
import com.rw.fsutil.dao.optimize.PersistentGenericHandler;

public class RoleExtPropertyStoreCache<T extends RoleExtProperty> implements MapItemUpdater<String, Integer>, DAOStoreCache<T, QueryRoleExtPropertyData> {

	private final MapItemCache<String, PlayerExtPropertyStoreImpl<T>> cache;
	private final Short type;
	private final Class<T> entityClass;
	private final ClassInfo clasInfo;
	private final ObjectMapper mapper;
	private final RoleExtPropertyManager dataAccessManager;

	public RoleExtPropertyStoreCache(RoleExtPropertyManager extPropertyManager, Class<T> entityClass, String cacheName, int capacity, String datasourceName, short type) {
		this.mapper = new ObjectMapper();
		this.type = type;
		this.entityClass = entityClass;
		this.clasInfo = new ClassInfo(entityClass, ClassHelper.getFirstAnnotateFieldName(entityClass, OwnerId.class));
		NameFilterIntrospector nameFilter = new NameFilterIntrospector(clasInfo.getPrimaryKey(),clasInfo.getOwnerFieldName());
		this.mapper.setAnnotationIntrospector(nameFilter); 
		this.dataAccessManager = extPropertyManager;
		this.cache = DataCacheFactory.createMapItemDache(entityClass, cacheName, capacity, 60, loader, null, null, null);
	}

	@Override
	public void submitUpdateTask(String key, Integer key2) {
		cache.submitUpdateTask(key, key2);
	}

	@Override
	public void submitUpdateList(String key, List<Integer> keyList) {
		cache.submitUpdateList(key, keyList);
	}

	@Override
	public void submitRecordTask(String key) {
		cache.submitRecordTask(key);
	}

	public PlayerExtPropertyStore<T> getStore(String userId) throws InterruptedException, Throwable {
		return this.cache.getOrLoadFromDB(userId);
	}

	public boolean contains(String userId) {
		return this.cache.containsKey(userId);
	}

	public PlayerExtPropertyStore<T> getStoreFromMemory(String userId) {
		return this.cache.getFromMemory(userId);
	}

	public boolean putIfAbsent(final String key, List<PlayerExtPropertyData<T>> datas) {
		PlayerExtPropertyStoreImpl<T> storeImpl = new PlayerExtPropertyStoreImpl<T>(dataAccessManager, datas, key, RoleExtPropertyStoreCache.this, type, mapper);
		return cache.preInsertIfAbsent(key, storeImpl);
	}

	public boolean putIfAbsentByDBString(final String key, final List<QueryRoleExtPropertyData> datas) {
		return this.cache.preInsertIfAbsent(key, new Callable<PlayerExtPropertyStoreImpl<T>>() {

			@Override
			public PlayerExtPropertyStoreImpl<T> call() throws Exception {
				return create(key, datas);
			}
		});
	}

	private PlayerExtPropertyStoreImpl<T> create(String key, List<QueryRoleExtPropertyData> datas) throws JsonParseException, JsonMappingException, Exception {
		int size = datas.size();
		Field keyField = this.clasInfo.getIdField();
		Field roleField = this.clasInfo.getOwnerField();
		ArrayList<PlayerExtPropertyData<T>> result = new ArrayList<PlayerExtPropertyData<T>>(size);
		for (int i = 0; i < size; i++) {
			QueryRoleExtPropertyData query = datas.get(i);
			T entity = mapper.readValue(query.getExtension(), entityClass);
			if (keyField != null) {
				keyField.set(entity, query.getSubType());
			}
			if (roleField != null) {
				roleField.set(entity, query.getOwnerId());
			}
			if (entity == null) {
				throw new RuntimeException("parse entity fail:" + entityClass + "," + query.getExtension());
			}
			result.add(new PlayerExtPropertyData<T>(query.getId(), entity));
		}
		return new PlayerExtPropertyStoreImpl<T>(dataAccessManager, result, key, RoleExtPropertyStoreCache.this, type, mapper);
	}

	private PersistentGenericHandler<String, PlayerExtPropertyStoreImpl<T>, CacheCompositKey<String, Integer>> loader = new PersistentGenericHandler<String, PlayerExtPropertyStoreImpl<T>, CacheCompositKey<String, Integer>>() {

		@Override
		public PlayerExtPropertyStoreImpl<T> load(String key) throws DataNotExistException, Exception {
			List<QueryRoleExtPropertyData> datas = dataAccessManager.loadEntitys(key, type);
			return create(key, datas);
		}

		@Override
		public boolean delete(String key) throws DataNotExistException, Exception {
			return false;
		}

		@Override
		public boolean insert(String key, PlayerExtPropertyStoreImpl<T> value) throws DuplicatedKeyException, Exception {
			return false;
		}

		@Override
		public boolean updateToDB(String key, PlayerExtPropertyStoreImpl<T> value) {
			return false;
		}

		@Override
		public String getTableName(String key) {
			return dataAccessManager.getTableName(key);
		}

		@Override
		public Map<String, String> getUpdateSqlMapping() {
			return dataAccessManager.getTableSqlMapping();
		}

		@Override
		public boolean extractParams(CacheCompositKey<String, Integer> key, PlayerExtPropertyStoreImpl<T> value, List<Object[]> updateList) {
			Integer key2 = key.getSecondKey();
			PlayerExtPropertyData<T> data = value.getItem(key2);
			if (data == null) {
				return false;
			}
			value.removeUpdateFlag(key2);
			String ext;
			try {
				ext = mapper.writeValueAsString(data.getAttachment());
			} catch (Exception e) {
				// TODO Logger object info
				e.printStackTrace();
				return false;
			}
			if (ext == null) {
				return false;
			}
			return updateList.add(new Object[] { ext, data.getPrimaryKey() });
		}

		@Override
		public boolean extractParams(String key, PlayerExtPropertyStoreImpl<T> value, Map<CacheCompositKey<String, Integer>, Object[]> map) {
			HashMap<Integer, PlayerExtPropertyData<T>> dirtyMap = value.getDirtyItems();
			for (Map.Entry<Integer, PlayerExtPropertyData<T>> entry : dirtyMap.entrySet()) {
				Integer k = entry.getKey();
				PlayerExtPropertyData<T> data = entry.getValue();
				String ext;
				try {
					ext = mapper.writeValueAsString(entry.getValue().getAttachment());
				} catch (Exception e) {
					// TODO Logger object info
					e.printStackTrace();
					return false;
				}
				if (ext == null) {
					FSUtilLogger.error("extract params is null:" + key + "," + k + "," + cache.getName());
					continue;
				}
				map.put(new DoubleKey<String, Integer>(key, k), new Object[] { ext, data.getPrimaryKey() });
			}
			return true;
		}

		@Override
		public boolean hasChanged(String key, PlayerExtPropertyStoreImpl<T> value) {
			return value.hasChanged();
		}

	};

	public ObjectMapper getMapper() {
		return mapper;
	}

	@Override
	public Class<T> getEntityClass() {
		return entityClass;
	}

}
