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
import com.rw.fsutil.cacheDao.mapItem.RoleExtConvertor;
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
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.dao.cache.trace.RoleExtChangedListener;
import com.rw.fsutil.dao.optimize.CacheCompositKey;
import com.rw.fsutil.dao.optimize.DAOStoreCache;
import com.rw.fsutil.dao.optimize.DoubleKey;
import com.rw.fsutil.dao.optimize.PersistentGenericHandler;

public class RoleExtPropertyStoreCache<T extends RoleExtProperty> implements MapItemUpdater<String, Integer>, DAOStoreCache<T, QueryRoleExtPropertyData> {

	private final MapItemCache<String, RoleExtPropertyStoreImpl<T>> cache;
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
		NameFilterIntrospector nameFilter = new NameFilterIntrospector(entityClass, clasInfo.getPrimaryKey(), clasInfo.getOwnerFieldName());
		this.mapper.setAnnotationIntrospector(nameFilter);
		this.dataAccessManager = extPropertyManager;
		DataValueParser<T> parser = DataCacheFactory.getParser(entityClass);
		// this.cache = DataCacheFactory.createMapItemDache(entityClass, cacheName, capacity, 60, loader, null, null, null);
		this.cache = DataCacheFactory.createMapItemDache(entityClass, cacheName, capacity, 60, loader, null, parser != null ? new RoleExtConvertor<T>(parser) : null, RoleExtChangedListener.class);

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

	public RoleExtPropertyStore<T> getStore(String userId) throws InterruptedException, Throwable {
		return this.cache.getOrLoadFromDB(userId);
	}

	public boolean contains(String userId) {
		return this.cache.containsKey(userId);
	}

	public RoleExtPropertyStore<T> getStoreFromMemory(String userId) {
		return this.cache.getFromMemory(userId);
	}

	public boolean putIfAbsent(final String key, List<RoleExtPropertyData<T>> datas) {
		RoleExtPropertyStoreImpl<T> storeImpl = new RoleExtPropertyStoreImpl<T>(dataAccessManager, datas, key, RoleExtPropertyStoreCache.this, type, mapper);
		return cache.preInsertIfAbsent(key, storeImpl);
	}

	public boolean putIfAbsentByDBString(final String key, final List<QueryRoleExtPropertyData> datas) {
		return this.cache.preInsertIfAbsent(key, new Callable<RoleExtPropertyStoreImpl<T>>() {

			@Override
			public RoleExtPropertyStoreImpl<T> call() throws Exception {
				return create(key, datas);
			}
		});
	}

	private RoleExtPropertyStoreImpl<T> create(String key, List<QueryRoleExtPropertyData> datas) throws JsonParseException, JsonMappingException, Exception {
		int size = datas.size();
		Field keyField = this.clasInfo.getIdField();
		Field roleField = this.clasInfo.getOwnerField();
		ArrayList<RoleExtPropertyData<T>> result = new ArrayList<RoleExtPropertyData<T>>(size);
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
			result.add(new RoleExtPropertyData<T>(query.getId(), entity));
		}
		return new RoleExtPropertyStoreImpl<T>(dataAccessManager, result, key, RoleExtPropertyStoreCache.this, type, mapper);
	}

	private PersistentGenericHandler<String, RoleExtPropertyStoreImpl<T>, CacheCompositKey<String, Integer>> loader = new PersistentGenericHandler<String, RoleExtPropertyStoreImpl<T>, CacheCompositKey<String, Integer>>() {

		@Override
		public RoleExtPropertyStoreImpl<T> load(String key) throws DataNotExistException, Exception {
			List<QueryRoleExtPropertyData> datas = dataAccessManager.loadEntitys(key, type);
			return create(key, datas);
		}

		@Override
		public boolean delete(String key) throws DataNotExistException, Exception {
			return false;
		}

		@Override
		public boolean insert(String key, RoleExtPropertyStoreImpl<T> value) throws DuplicatedKeyException, Exception {
			return false;
		}

		@Override
		public boolean updateToDB(String key, RoleExtPropertyStoreImpl<T> value) {
			HashMap<Integer, RoleExtPropertyData<T>> dirtyMap = value.getDirtyItems();
			for (Map.Entry<Integer, RoleExtPropertyData<T>> entry : dirtyMap.entrySet()) {
				RoleExtPropertyData<T> data = entry.getValue();
				String ext;
				try {
					ext = mapper.writeValueAsString(entry.getValue().getAttachment());
				} catch (Exception e) {
					FSUtilLogger.error("保存数据解析Json失败:" + ",key=" + key + ",pk=" + data.getPrimaryKey());
					return false;
				}
				if (ext == null) {
					FSUtilLogger.error("保存数据提取内容失败:" + ",key=" + key + ",pk=" + data.getPrimaryKey());
					continue;
				}
				try {
					boolean updateResult = dataAccessManager.updateAttachmentExtention(key, ext, data.getPrimaryKey());
					if (!updateResult) {
						FSUtilLogger.error("保存数据失败:" + ",key=" + key + ",pk=" + data.getPrimaryKey() + ",ext=" + ext);
					}
				} catch (Throwable t) {
					FSUtilLogger.error("保存数据异常:" + ",key=" + key + ",pk=" + data.getPrimaryKey() + ",ext=" + ext, t);
				}
			}
			return true;
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
		public boolean extractParams(CacheCompositKey<String, Integer> key, RoleExtPropertyStoreImpl<T> value, List<Object[]> updateList) {
			Integer key2 = key.getSecondKey();
			RoleExtPropertyData<T> data = value.getItem(key2);
			if (data == null) {
				return false;
			}
			value.removeUpdateFlag(key2);
			String ext;
			try {
				ext = mapper.writeValueAsString(data.getAttachment());
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			if (ext == null) {
				return false;
			}
			return updateList.add(new Object[] { ext, data.getPrimaryKey() });
		}

		@Override
		public boolean extractParams(String key, RoleExtPropertyStoreImpl<T> value, Map<CacheCompositKey<String, Integer>, Object[]> map) {
			HashMap<Integer, RoleExtPropertyData<T>> dirtyMap = value.getDirtyItems();
			for (Map.Entry<Integer, RoleExtPropertyData<T>> entry : dirtyMap.entrySet()) {
				Integer k = entry.getKey();
				RoleExtPropertyData<T> data = entry.getValue();
				String ext;
				try {
					ext = mapper.writeValueAsString(entry.getValue().getAttachment());
				} catch (Exception e) {
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
		public boolean hasChanged(String key, RoleExtPropertyStoreImpl<T> value) {
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
