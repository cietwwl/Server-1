package com.rw.fsutil.cacheDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.dao.annotation.ClassHelper;

/**
 * 前台数据(支持单表和多表) 数据库+memcached
 * 
 * @author Ace
 *
 * @param <T>
 * @param <ID>
 */
public class DataKVCacheDao<T> {

	private DataKVDao<T> dataKVDao;
	
	protected Map<String, T> dataMap = new ConcurrentHashMap<String, T>();
	
	private Map<String, Boolean> updatedDataMap = new ConcurrentHashMap<String, Boolean>();
	
	//无用的标记符，为了使用map
	final private Boolean DUMMY_FLAG = true;

	public DataKVCacheDao() {
		Class<T> clazz = ClassHelper.getEntityClass(this.getClass());
		dataKVDao = new DataKVDao<T>(clazz);
	}
	
	public boolean add(T t) {
		boolean success = false;
		String id = dataKVDao.getId(t);
		if(StringUtils.isNotBlank(id)){
			success = dataKVDao.update(t);
			if(success){
				dataMap.put(id, t);
			}
		}
		
		return success;
	}

	public boolean update(T t) {
		String id = dataKVDao.getId(t);
		if(dataMap.containsKey(id)){
			updatedDataMap.put(id, DUMMY_FLAG);
		}
		return true;
	}


	public boolean delete(String id) {
		if (StringUtils.isBlank(id)) {
			return false;
		}
		boolean success = dataKVDao.delete(id);
		if(success){
			dataMap.remove(id);
			updatedDataMap.remove(id);
		}

		return success;
	}

	public T get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		T t = dataMap.get(id);
		if(t == null){
			t = dataKVDao.get(id);
			if( t!=null ){
				dataMap.put(id, t);
			}
		}
		return t;

	}
	

	//慎用 联系allen
	public List<T> getAll() {
		return new ArrayList<T>();
	}
	
	/**
	 * 刷新所有需要更新的数据
	 */
	public void flush(){
		Map<String, Boolean> updateMapTmp = updatedDataMap;
		updatedDataMap = new ConcurrentHashMap<String, Boolean>();
		Set<String> updatedIdSet = updateMapTmp.keySet();
		for (String idTmp : updatedIdSet) {
			if(dataMap.containsKey(idTmp)){
				final T updateTmp = dataMap.get(idTmp);
				CommonUpdateMgr.getInstance().addTask(new CommonUpdateTask() {
					
					@Override
					public void doTask() {						
						dataKVDao.update(updateTmp);
					}
				});
			}
		}
	}


}
