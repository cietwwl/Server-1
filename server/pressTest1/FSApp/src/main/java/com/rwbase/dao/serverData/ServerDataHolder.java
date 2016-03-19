package com.rwbase.dao.serverData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class ServerDataHolder{//战斗数据
	
	private ServerDataDao serverDataDao = ServerDataDao.getInstance();
	private ServerData serverData;

	private final String serverId = "1";
	private ReadLock readLock;
	private WriteLock writeLock;	

	
	public ServerDataHolder() {
		ServerData userDataTmp =  serverDataDao.get(serverId);
		if(userDataTmp == null){
			userDataTmp = new ServerData();
			userDataTmp.setServerId(serverId);
			
			boolean success = serverDataDao.update(userDataTmp);
			if(success){
				serverData = userDataTmp;
			}
		}else{
			serverData = userDataTmp;
		}
		

		ReentrantReadWriteLock treeRwLock = new ReentrantReadWriteLock();
		this.readLock = treeRwLock.readLock();
		this.writeLock = treeRwLock.writeLock();
	}

	private void update(){
		serverDataDao.update(serverData);
	}

	public int getOnlineLimit(){
		return serverData.getOnlineLimit();
	}
	
	public void setOnlineLimit(int limit){
		serverData.setOnlineLimit(limit);
		update();
	}
	
	public void setChargeOn(boolean chargeOn){
		serverData.setChargeOn(chargeOn);
		update();
	}
	
	public boolean isChargeOn(){
		return serverData.isChargeOn();
	}
	
	public void setLastBIStatLogTime(long lastBIStatLogTime){
		serverData.setLastBIStatLogTime(lastBIStatLogTime);
		update();
	}
	
	public long getLastBIStatLogTime(){
		return serverData.getLastBIStatLogTime();
	}
	
	
	public  List<String> getWhiteList(){
		readLock.lock();
		List<String> whiteList = new ArrayList<String>();
		try {
			List<String> whiteListTmp = serverData.getWhiteList();
			if(whiteListTmp!=null){
				whiteList = Collections.unmodifiableList(whiteListTmp);
			}
		} finally {
			readLock.unlock();
		}
		return whiteList;
	}
	
	public void addWhite(String userId){
		writeLock.lock();
		try {
			List<String> whiteList = serverData.getWhiteList();
			if(whiteList == null){
				whiteList = new ArrayList<String>();
				serverData.setWhiteList(whiteList);
			}
			if(!whiteList.contains(userId)){
				whiteList.add(userId);
				update();
			}
		} finally {
			writeLock.unlock();
		}
	}
	public void removeWhite(String userId){
		writeLock.lock();
		try {
			List<String> whiteList = serverData.getWhiteList();
			if(whiteList.contains(userId)){
				whiteList.remove(userId);
				update();
			}
		} finally {
			writeLock.unlock();
		}
	}

}
