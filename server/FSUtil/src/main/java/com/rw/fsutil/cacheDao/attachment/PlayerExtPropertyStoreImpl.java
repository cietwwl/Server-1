package com.rw.fsutil.cacheDao.attachment;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.cacheDao.mapItem.MapItemUpdater;
import com.rw.fsutil.dao.attachment.NewAttachmentEntry;
import com.rw.fsutil.dao.attachment.RoleExtPropertyManager;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;

public class PlayerExtPropertyStoreImpl<T extends PlayerExtProperty> extends RowMapItemContainer<Integer, PlayerExtPropertyData<T>, T> implements PlayerExtPropertyStore<T> {

	private final short type;
	private final ObjectMapper mapper;
	private final RoleExtPropertyManager dataAccessManager;
	
	public PlayerExtPropertyStoreImpl(RoleExtPropertyManager dataAccessManager,List<PlayerExtPropertyData<T>> itemList, String searchId, MapItemUpdater<String, Integer> updater, short type, ObjectMapper mapper) {
		super(itemList, searchId, updater);
		this.type = type;
		this.mapper = mapper;
		this.dataAccessManager = dataAccessManager;
	}

	@Override
	public List<PlayerExtPropertyData<T>> insertAndDelete(String searchId, List<T> addList, List<Integer> delList) throws DuplicatedKeyException, DataNotExistException, Exception {
		int size = addList.size();
		ArrayList<NewAttachmentEntry> newList = new ArrayList<NewAttachmentEntry>(size);
		for (int i = 0; i < size; i++) {
			newList.add(PlayerExtPropertyUtil.convert(mapper, searchId, type, addList.get(i)));
		}
		size = delList.size();
		ArrayList<Long> deleteKeys = new ArrayList<Long>(size);
		for (int i = 0; i < size; i++) {
			Integer configId = delList.get(i);
			PlayerExtPropertyData<T> attachment = super.getItem(configId);
			if (attachment == null) {
				throw new DataNotExistException("record not exist:" + configId);
			}
			deleteKeys.add(attachment.getPrimaryKey());
		}
		long[] keys = dataAccessManager.insertAndDelete(searchId, newList, deleteKeys);
		return create(addList, keys, size);
	}

	@Override
	public PlayerExtPropertyData<T> insert(String searchId, T item) throws DuplicatedKeyException, Exception {
		long id = dataAccessManager.insert(searchId, PlayerExtPropertyUtil.convert(mapper, searchId, type, item));
		PlayerExtPropertyData<T> entity = new PlayerExtPropertyData<T>(id, item);
		return entity;
	}

	@Override
	public List<PlayerExtPropertyData<T>> insert(String searchId, List<T> itemList) throws DuplicatedKeyException, Exception {
		int size = itemList.size();
		ArrayList<NewAttachmentEntry> list = new ArrayList<NewAttachmentEntry>(size);
		for (int i = 0; i < size; i++) {
			list.add(PlayerExtPropertyUtil.convert(mapper, searchId, type, itemList.get(i)));
		}
		long[] keys = dataAccessManager.insert(searchId, list);
		return create(itemList, keys, size);
	}

	public List<PlayerExtPropertyData<T>> create(List<T> itemList, long[] keys, int size) {
		ArrayList<PlayerExtPropertyData<T>> result = new ArrayList<PlayerExtPropertyData<T>>(size);
		for (int i = 0; i < size; i++) {
			T t = itemList.get(i);
			long key = keys[i];
			result.add(new PlayerExtPropertyData<T>(key, t));
		}
		return result;
	}

	// private NewAttachmentEntry convert(T t) throws JsonGenerationException,
	// JsonMappingException, IOException {
	// String extension = mapper.writeValueAsString(t);
	// NewAttachmentEntry entry = new NewAttachmentEntry(searchId, type,
	// t.getId(), extension);
	// return entry;
	// }

	@Override
	public List<Integer> delete(String searchId, List<Integer> list) throws Exception {
		int size = list.size();
		ArrayList<Long> idList = new ArrayList<Long>();
		HashMap<Long, Integer> tempMapping = new HashMap<Long, Integer>((int) (size / 0.75f + 1));
		for (int i = 0; i < size; i++) {
			Integer configId = list.get(i);
			PlayerExtPropertyData<T> entity = super.getItem(configId);
			if (entity == null) {
				FSUtilLogger.error("PlayerAttachmentStore find item to delete fail:" + list.get(i));
				continue;
			}
			Long id = entity.getPrimaryKey();
			tempMapping.put(id, configId);
			idList.add(id);
		}
		List<Long> result = dataAccessManager.delete(searchId, idList);
		size = result.size();
		ArrayList<Integer> returnList = new ArrayList<Integer>(size);
		for (int i = 0; i < size; i++) {
			Long id = result.get(i);
			Integer configId = tempMapping.get(id);
			if (configId == null) {
				FSUtilLogger.error("PlayerAttachmentStore delete return error result:" + id + "," + idList + "," + list);
				continue;
			}
			returnList.add(configId);
		}
		return returnList;
	}

	@Override
	public boolean delete(String searchId, Integer key) throws DataNotExistException, Exception {
		PlayerExtPropertyData<T> entity = super.getItem(key);
		if (entity == null) {
			return false;
		}
		return dataAccessManager.delete(searchId, entity.getPrimaryKey());
	}

	public void removeUpdateFlag(Integer key) {
		updatedMap.remove(key);
	}

	@Override
	public T get(Integer cfgId) {
		PlayerExtPropertyData<T> entity = super.getItem(cfgId);
		return entity == null ? null : entity.getAttachment();
	}

	@Override
	public Enumeration<T> getExtPropertyEnumeration() {
		final Enumeration<PlayerExtPropertyData<T>> entityEnumeration = super.getEnum();
		return new Enumeration<T>() {

			@Override
			public boolean hasMoreElements() {
				return entityEnumeration.hasMoreElements();
			}

			@Override
			public T nextElement() {
				return entityEnumeration.nextElement().getAttachment();
			}
		};
	}

}
