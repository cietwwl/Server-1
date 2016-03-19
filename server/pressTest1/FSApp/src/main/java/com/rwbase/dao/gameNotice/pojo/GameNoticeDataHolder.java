package com.rwbase.dao.gameNotice.pojo;

import java.util.HashMap;
import java.util.List;

import com.rw.service.gamenotice.GameNoticeService;
import com.rwbase.dao.gameNotice.TableGameNotice;
import com.rwbase.dao.gameNotice.TableGameNoticeDAO;

public class GameNoticeDataHolder {
	private TableGameNotice tableGameNotice;
	
	public GameNoticeDataHolder(){
		if(!blnInit){
			initNotice();
		}
	}
	
	
	private final static HashMap<Integer, TableGameNotice> NoticeMap = new HashMap<Integer, TableGameNotice>();
	private Object _lock = new Object();
	private boolean blnInit = false;
	
	private static GameNoticeService instance = new GameNoticeService();
	
	public static GameNoticeService getInstance(){
		if(instance == null){
			instance = new GameNoticeService();
		}
		return instance;
	}
	
	private void initNotice(){
		
		List<TableGameNotice> NoticeList = TableGameNoticeDAO.getInstance().getAllGameNotice();
		for (TableGameNotice tableGameNotice : NoticeList) {
			NoticeMap.put(tableGameNotice.getNoticeId(), tableGameNotice);
		}
		blnInit = true;
	}
	
	public TableGameNotice getNoticeByNoticeId(int noticeId){
		return NoticeMap.get(noticeId);
	}
	
	public boolean saveOrUpdate(TableGameNotice notice){
		return TableGameNoticeDAO.getInstance().saveOrUpdate(notice);
	}
	
	public void addGameNotice(TableGameNotice notice){
		synchronized (_lock) {
			saveOrUpdate(notice);
			initNotice();
		}
	}
	
	public boolean removeGameNotice(int noticeId){
		NoticeMap.remove(noticeId);
		return TableGameNoticeDAO.getInstance().deleteByNoticeId(noticeId);
	}
	
	public HashMap<Integer, TableGameNotice> getGameNotices(){
		return NoticeMap;
	}
}
