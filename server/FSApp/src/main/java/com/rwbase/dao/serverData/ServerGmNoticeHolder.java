package com.rwbase.dao.serverData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ServerGmNoticeHolder {
	private ServerGmNoticeDao serverGmNoticeDao = ServerGmNoticeDao.getInstance();
	private HashMap<Long, ServerGmNotice> GmNoticeMap = new HashMap<Long, ServerGmNotice>();
	
	public ServerGmNoticeHolder(){
		
	}
	
	public void initGmNotices(){
		List<ServerGmNotice> allNotices = serverGmNoticeDao.getAllNotices();
		long current = System.currentTimeMillis();
		for (ServerGmNotice serverGmNotice : allNotices) {
			GmNoticeInfo noticeInfo = serverGmNotice.getNoticeInfo();
			if(current >= noticeInfo.getEndTime()){
				removeGmNotice(serverGmNotice.getId());
			}else{
				GmNoticeMap.put(serverGmNotice.getId(), serverGmNotice);
			}
		}
	}
	
	public List<ServerGmNotice> GetGmNotices(){
		Collection<ServerGmNotice> values = GmNoticeMap.values();
		return new ArrayList<ServerGmNotice>(values);
	}
	
	public void editGmNotice(ServerGmNotice notice, boolean insert){
		GmNoticeMap.put(notice.getId(), notice);
		serverGmNoticeDao.save(notice, insert);
	}
	
	public void removeGmNotice(long noticeId){
		GmNoticeMap.remove(noticeId);
		serverGmNoticeDao.remove(String.valueOf(noticeId));
	}
	
	public ServerGmNotice getGmNoticeById(long noticeId){
		return GmNoticeMap.get(noticeId);
	}
}
