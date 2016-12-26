package com.rwbase.dao.gameNotice.pojo;

import java.util.HashMap;
import java.util.List;

import com.bm.notice.NoticeMgr;
import com.rw.service.gamenotice.GameNoticeService;
import com.rwbase.dao.gameNotice.TableGameNotice;
import com.rwbase.dao.gameNotice.TableGameNoticeDAO;

public class GameNoticeDataHolder {
	
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
		return TableGameNoticeDAO.getInstance().getByNoticeId(noticeId);
	}
	
	public void saveOrUpdate(TableGameNotice notice, boolean insert){
		TableGameNoticeDAO.getInstance().save(notice, insert);
	}
	
	public void addGameNotice(TableGameNotice notice, boolean insert){
		synchronized (_lock) {
			saveOrUpdate(notice, insert);
			initNotice();
			NoticeMgr.getInstance().refreshGameNotice();
		}
	}
	
	public void removeGameNotice(int noticeId){
		TableGameNotice tableGameNotice = NoticeMap.remove(noticeId);
		TableGameNoticeDAO.getInstance().deleteByNoticeId(noticeId);
		NoticeMgr.getInstance().RemoveNotice(tableGameNotice);
	}
	
	public HashMap<Integer, TableGameNotice> getGameNotices(){
		return NoticeMap;
	}
}
