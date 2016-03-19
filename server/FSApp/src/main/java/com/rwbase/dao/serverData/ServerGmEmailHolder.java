package com.rwbase.dao.serverData;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.bm.serverStatus.ServerStatusMgr;
import com.rwbase.dao.email.EmailData;

public class ServerGmEmailHolder {

	private ServerGmEmailDao serverGmEmailDao = ServerGmEmailDao.getInstance();

	private ReadLock readLock;
	private WriteLock writeLock;	

	
	public ServerGmEmailHolder() {
		
		ReentrantReadWriteLock treeRwLock = new ReentrantReadWriteLock();
		this.readLock = treeRwLock.readLock();
		this.writeLock = treeRwLock.writeLock();
	}

	public List<ServerGmEmail> getGmMailList(){
		
		List<ServerGmEmail> gmMailList = serverGmEmailDao.getAllMails();
		return gmMailList;
	}
	
	public void addGmMail(ServerGmEmail mail){
		writeLock.lock();
		try {
			long taskId = ServerStatusMgr.getTaskId();
			long nextTaskId = taskId + 1;
			EmailData sendToAllEmailData = mail.getSendToAllEmailData();
			sendToAllEmailData.setTaskId(nextTaskId);
			serverGmEmailDao.save(mail, true);
			ServerStatusMgr.setTaskId(nextTaskId);
		}finally{
			writeLock.unlock();
		}
	}
	
	public void updateGmMail(ServerGmEmail mail){
		serverGmEmailDao.save(mail, false);
	}
	
	public ServerGmEmail getGmMailByTaskId(long taskId){
		List<ServerGmEmail> gmMailList = getGmMailList();
		for (ServerGmEmail serverGmEmail : gmMailList) {
			if(serverGmEmail.getEmailTaskId() == taskId){
				return serverGmEmail;
			}
		}
		return null;
	}
}
