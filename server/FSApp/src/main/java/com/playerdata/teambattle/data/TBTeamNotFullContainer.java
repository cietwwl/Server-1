package com.playerdata.teambattle.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.common.HPCUtil;

public class TBTeamNotFullContainer {

	private ArrayList<String> list;
	private Lock readLock;
	private Lock writeLock;

	public TBTeamNotFullContainer(List<String> list) {
		ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
		readLock = rwLock.readLock();
		writeLock = rwLock.writeLock();
		writeLock.lock();
		try {
			if(null == list || list.isEmpty()){
				this.list = new ArrayList<String>();
			}else{
				this.list = new ArrayList<String>(list);
			}
		} finally {
			writeLock.unlock();
		}
	}

	public String getTeam(int index, String hardId) {
		readLock.lock();
		try {
			int size = list.size();
			if (index >= size) {
				return null;
			}
			return list.get(index);
		} finally {
			readLock.unlock();
		}
	}

	public boolean notifyTeamAdd(String teamId) {
		writeLock.lock();
		try {
			if (list.contains(teamId)) {
				return false;
			}
			return list.add(teamId);
		} finally {
			writeLock.unlock();
		}
	}

	public boolean notifyTeamRemove(String teamId) {
		int index;
		readLock.lock();
		try {
			index = list.indexOf(teamId);
		} finally {
			readLock.unlock();
		}
		if (index == -1) {
			return false;
		}
		writeLock.lock();
		try {
			String current = list.get(index);
			if (current.equals(teamId)) {
				list.remove(index);
				return true;
			} else {
				return list.remove(teamId);
			}
		} finally {
			writeLock.unlock();
		}
	}

	public String getRandomTeam() {
		Random random = HPCUtil.getRandom();
		readLock.lock();
		try {
			int size = list.size();
			if(size <= 0) {
				return null;
			}
			int randomIndex = random.nextInt(size);
			return list.get(randomIndex);
		} finally {
			readLock.unlock();
		}
	}
	
	public List<String> getRandomTeam(int count) {
		Random random = HPCUtil.getRandom();
		List<String> result = new ArrayList<String>();
		readLock.lock();
		try {
			int size = list.size();
			if(size <= 0) {
				return result;
			}
			if(size < count){
				Collections.copy(result, list);
				return result;
			}
			int randomIndex = random.nextInt(size);
			for(int i = 0; i < count; i++){
				result.add(list.get((randomIndex + i)%size));
			}
			return result;
		} finally {
			readLock.unlock();
		}
	}
	
	public void clearRecord(){
		writeLock.lock();
		try {
			this.list.clear();
		} finally {
			writeLock.unlock();
		}
	}
}
