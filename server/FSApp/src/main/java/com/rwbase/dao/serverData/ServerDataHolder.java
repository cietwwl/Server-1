package com.rwbase.dao.serverData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.log.GameLog;
import com.log.LogModule;

public class ServerDataHolder{
	
	private ServerDataDao serverDataDao = ServerDataDao.getInstance();

	private final String serverId = "1";
	private ReadLock readLock;
	private WriteLock writeLock;	

	
	public ServerDataHolder() {
		
		ReentrantReadWriteLock treeRwLock = new ReentrantReadWriteLock();
		this.readLock = treeRwLock.readLock();
		this.writeLock = treeRwLock.writeLock();
	}
	
	private ServerData getServerData(){
		ServerData serverData = serverDataDao.get(serverId);
		if(serverData == null){
			serverData = new ServerData();
			serverData.setServerId(serverId);
			serverDataDao.update(serverData);
		}
		return serverData;
	}

	private void update(ServerData serverData){
		serverDataDao.update(serverData);
	}

	public int getOnlineLimit(){
		ServerData serverData = getServerData();
		return serverData.getOnlineLimit();
	}
	
	public void setOnlineLimit(int limit){
		ServerData serverData = getServerData();
		serverData.setOnlineLimit(limit);
		update(serverData);
	}
	
	public void setChargeOn(boolean chargeOn){
		ServerData serverData = getServerData();
		serverData.setChargeOn(chargeOn);
		GameLog.error(LogModule.GM, "", "后台开设充值开关"+ chargeOn, null);
		update(serverData);
	}
	
	public boolean isChargeOn(){
		ServerData serverData = getServerData();
		return serverData.isChargeOn();
	}
	
	public void setLastBIStatLogTime(long lastBIStatLogTime){
		ServerData serverData = getServerData();
		serverData.setLastBIStatLogTime(lastBIStatLogTime);
		update(serverData);
	}
	
	public long getLastBIStatLogTime(){
		ServerData serverData = getServerData();
		return serverData.getLastBIStatLogTime();
	}
	
	
	public  List<String> getWhiteList(){
		
		List<String> whiteList = new ArrayList<String>();
		ServerData serverData = getServerData();
		try {
			List<String> whiteListTmp = serverData.getWhiteList();
			if(whiteListTmp!=null){
				whiteList = Collections.unmodifiableList(whiteListTmp);
			}
		} finally {
			
		}
		return whiteList;
	}
	
	public void addWhite(String userId){
		writeLock.lock();
		ServerData serverData = getServerData();
		try {
			List<String> whiteList = serverData.getWhiteList();
			if(whiteList == null){
				whiteList = new ArrayList<String>();
				serverData.setWhiteList(whiteList);
			}
			if(!whiteList.contains(userId)){
				whiteList.add(userId);
				update(serverData);
			}
		} finally {
			writeLock.unlock();
		}
	}
	public void removeWhite(String userId){
		writeLock.lock();
		ServerData serverData = getServerData();
		try {
			List<String> whiteList = serverData.getWhiteList();
			if(whiteList.contains(userId)){
				whiteList.remove(userId);
				update(serverData);
			}
		} finally {
			writeLock.unlock();
		}
	}

	public long getTaskId(){
		ServerData serverData = getServerData();
		return serverData.getTaskId();
	}
	
	public void setTaskId(long taskId){
		ServerData serverData = getServerData();
		serverData.setTaskId(taskId);
		update(serverData);
	}
}
