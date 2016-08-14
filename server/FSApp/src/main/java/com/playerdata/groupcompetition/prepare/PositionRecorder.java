package com.playerdata.groupcompetition.prepare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.mina.util.ConcurrentHashSet;

/**
 * 备战区玩家位置信息（一个备战区对应一个Recorder）
 * 按帮派来区分的
 * 一个帮派拥有一个备战区
 * @author aken
 */
public class PositionRecorder<K, T> {
	private HashMap<K, T> map;
	private Lock readLock;
	private Lock writeLock;
	
	public PositionRecorder() {
		ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
		readLock = rwLock.readLock();
		writeLock = rwLock.writeLock();
		map = new HashMap<K, T>();
	}
	
	public boolean notifyAdd(K key, T value) {
		writeLock.lock();
		try {
			if (map.containsKey(key)) {
				return false;
			}
			return map.put(key, value) == null;
		} finally {
			writeLock.unlock();
		}
	}

	public boolean notifyTeamRemove(K key) {
		writeLock.lock();
		try {
			return map.remove(key) == null;
		} finally {
			writeLock.unlock();
		}
	}
	
	public T getRecord(K key){
		readLock.lock();
		try {
			return map.get(key);
		} finally {
			readLock.unlock();
		}
	}
	
	public HashMap<K, T> getRecords(){
		readLock.lock();
		try {
			return new HashMap<K, T>(map);
		} finally {
			readLock.unlock();
		}
	}
	
//	public static void main(String[] args) {
//		int TIMES_1 = 10;
//		int TIMES_2 = 10;
//		
//		WaitingQueue<String> wq = new WaitingQueue<String>();
//		long start = System.nanoTime();
//		for(int i = 0 ; i < TIMES_1; i++){
//			for(int j = 0 ; j < TIMES_2 * 30; j++){
//				wq.addElement(j + "@@中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112");
//			}
//			for(int j = 0 ; j < TIMES_2; j++){
//				wq.pollElement();
//			}
//		}
//		System.out.println("wq time:" + (System.nanoTime() - start));
//		
//		List<String> list = new ArrayList<String>();
//		start = System.nanoTime();
//		for(int i = 0 ; i < TIMES_1; i++){
//			for(int j = 0 ; j < TIMES_2; j++){
//				list.add(j + "@@中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112");
//			}
//			for(int j = 0 ; j < TIMES_2 * 29; j++){
//				list.contains(j + "@@中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112");
//			}
//			for(int j = 0 ; j < TIMES_2; j++){
//				list.remove(0);
//			}
//		}
//		System.out.println("list time:" + (System.nanoTime() - start));
//		
//		HashSet<String> hs = new HashSet<String>();
//		start = System.nanoTime();
//		for(int i = 0 ; i < TIMES_1; i++){
//			for(int j = 0 ; j < TIMES_2 * 30; j++){
//				hs.add(j + "@@中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112");
//			}
//			for(int j = 0 ; j < TIMES_2; j++){
//				hs.remove(0);
//			}
//		}
//		System.out.println("hs time:" + (System.nanoTime() - start));
//		
//		
//		ConcurrentHashSet<String> chs = new ConcurrentHashSet<String>();
//		start = System.nanoTime();
//		for(int i = 0 ; i < TIMES_1; i++){
//			for(int j = 0 ; j < TIMES_2 * 30; j++){
//				chs.add(j + "@@中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112中国人abc112");
//			}
//			for(int j = 0 ; j < TIMES_2; j++){
//				chs.remove(0);
//			}
//		}
//		System.out.println("chs time:" + (System.nanoTime() - start));
//	}
}
