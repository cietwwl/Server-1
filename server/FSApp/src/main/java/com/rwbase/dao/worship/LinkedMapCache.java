package com.rwbase.dao.worship;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;



/**
 * <P>lru cache</P>
 * 一个固定容量的map,线程安全，如果达到容量上限，会将停留时间最长的元素删除
 * @author Alex
 * 2016年8月22日 下午8:39:23
 */
public class LinkedMapCache<K, V> {
	
	private LinkedHashMap<K, V> cacheMap;
	
	private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	
	public LinkedMapCache(final int value) {
		
		
		cacheMap = new LinkedHashMap<K, V>(value){
			
		
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Entry<K, V> eldest) {
				if(size() > value){
					return true;
				}
				return false;
			}

		};
	}
	

	


	public V put(K k, V v){
		V value = null;
		boolean lock = false;
		try {
			lock = rwLock.writeLock().tryLock(2, TimeUnit.SECONDS);
			if(lock){
				value = cacheMap.put(k, v);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(lock){
				rwLock.writeLock().unlock();
			}
		}
		return value;
	}
	
	
	public void clear(){
		boolean lock = false;
		try {
			lock = rwLock.writeLock().tryLock(2, TimeUnit.SECONDS);
			if(lock){
				cacheMap.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(lock){
				rwLock.writeLock().unlock();
			}
		}
	}
	
	public List<V> getValues(){
		List<V> newList = new ArrayList<V>();
		boolean lock = false;
		try {
			lock = rwLock.readLock().tryLock(2, TimeUnit.SECONDS);
			if(lock){
				newList.addAll(cacheMap.values());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(lock){
				rwLock.readLock().unlock();
			}
		}
		return newList;
	}
	
	
	public boolean remove(K k){
		V value = null;
		boolean lock = false;
		try {
			lock = rwLock.writeLock().tryLock(2, TimeUnit.SECONDS);
			if(lock){
				value = cacheMap.remove(k);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(lock){
				rwLock.writeLock().unlock();
			}
		}
		return value != null;
	}

	
	
	public V get(K k){
		V value = null;
		boolean lock = false;
		try {
			lock = rwLock.readLock().tryLock(2, TimeUnit.SECONDS);
			if(lock){
				value = cacheMap.get(k);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(lock){
				rwLock.readLock().unlock();
			}
		}
		return value;
	}
	
	
	public int size(){
		int size = 0;
		boolean lock = false;
		try {
			lock = rwLock.readLock().tryLock(2, TimeUnit.SECONDS);
			if(lock){
				size = cacheMap.size();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(lock){
				rwLock.readLock().unlock();
			}
		}
		return size;
	}
}
