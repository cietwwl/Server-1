package com.rwbase.dao.gameworld;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rwbase.gameworld.GameWorldKey;


public class GameWorldObjDAO {

	private static GameWorldObjDAO instance;
	
	private static Map<String,Object> cacheMap = new ConcurrentHashMap<String, Object>();
	
	private static Map<Class<?>,ClassInfo> classInfoMap = new ConcurrentHashMap<Class<?>, ClassInfo>();

	private ReadLock readLock;

	private WriteLock writeLock;	

	public GameWorldObjDAO(){
		
		ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
		this.readLock = rwLock.readLock();
		this.writeLock = rwLock.writeLock();
	}
	
	public static GameWorldObjDAO getInstance() {
		if (instance == null) {
			instance = new GameWorldObjDAO();
		}
		return instance;
	}
	
	public boolean update(GameWorldKey gwKey, Object value){
		
		String key = gwKey.getName();
		ClassInfo classInfo = classInfoMap.get(value.getClass());
		boolean success = false;
		writeLock.lock();
		try {
			String json = classInfo.toJson(value);
			GameWorldAttributeData data = new GameWorldAttributeData();
			data.setKey(key);
			data.setValue(json);
			success = GameWorldDAO.getInstance().update(data);
			if(success){
				cacheMap.put(key, value);
			}
		} catch (Exception e) {
			success = false;
			GameLog.error(LogModule.GameWorld, "GameWorldObjDAO[update]", "update error", e);
		}finally{
			writeLock.unlock();	
		}
		
		return success;
		
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(GameWorldKey gwKey, Class<T> clazz ){
		String key = gwKey.getName();
		Object target = null;
		readLock.lock();
		try {			
			if(!cacheMap.containsKey(key)){
				T fromDB = getFromDB(key, clazz);
				cacheMap.put(key, fromDB);
			}
			
			target = cacheMap.get(key);
		}finally {
			readLock.unlock();
		}
		
		if(target!=null){
			return (T)target;
		}
		return null;
	}

	private <T> T getFromDB(String key, Class<T> clazz) {
		
		GameWorldAttributeData target = GameWorldDAO.getInstance().get(key);
		String value = null;
		if(target!=null){
			value = target.getValue();
		}
		
		if(StringUtils.isNotBlank(value)){
			
			ClassInfo classInfo = classInfoMap.get(clazz);
			try {
				@SuppressWarnings("unchecked")
				T valueObject = (T)classInfo.fromJson(value);
				return valueObject;
			} catch (Exception e) {
				GameLog.error(LogModule.GameWorld, "GameWorldDAO[get]", "classInfo.fromJson(value) value:" + value, e);
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private ClassInfo getClassInfo(Class<?> clazz){
		
		if(!classInfoMap.containsKey(clazz)){
			ClassInfo  classInfoTmp = new ClassInfo(clazz);
			classInfoMap.put(clazz, classInfoTmp);
		}
		
		return classInfoMap.get(clazz);
		
	}
	
}
