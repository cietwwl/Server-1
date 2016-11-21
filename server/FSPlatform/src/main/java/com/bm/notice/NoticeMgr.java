package com.bm.notice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.rw.fsutil.util.DateUtils;
import com.rw.platform.PlatformFactory;
import com.rwbase.dao.notice.AnnouncementCfgDAO;
import com.rwbase.dao.notice.pojo.AnnouncementCfg;
import com.rwbase.dao.platformNotice.TablePlatformNotice;

public class NoticeMgr {
	
	public final static int PUSHTYPE_ALL = 0;          		//全部推送
	public final static int PUSHTYPE_PLATFORM = 1;		   	//平台推送
	public final static int PUSHTYPE_GAME = 2;				//游戏推送
	
	public final static int AnnonceType_Word = 0;
	public final static int AnnonceType_Pic = 1;
	
	private static NoticeMgr instance = new NoticeMgr();
	
	public static NoticeMgr getInstance(){
		return instance;
	}
	
	private List<Notice> NoticeList = new ArrayList<Notice>();
	
	protected NoticeMgr() {
		
	}
	
	public void initNotice(){
		NoticeList.clear();
		long secondLevelMillis = DateUtils.getSecondLevelMillis();
		//加载配置表的公告
		List<AnnouncementCfg> allCfg = AnnouncementCfgDAO.getInstance().getAllCfg();
		for (AnnouncementCfg announcementCfg : allCfg) {
			if(announcementCfg.getEndTime() < secondLevelMillis || announcementCfg.getPushType() == PUSHTYPE_PLATFORM){
				continue;
			}
			Notice notice =new Notice();
			notice.SetNotice(announcementCfg);
			NoticeList.add(notice);
		}
		
		//加载数据库的公告
		TablePlatformNotice platformNotice = PlatformFactory.getPlatformService().getPlatformNotice();
		if (platformNotice != null) {
			Notice notice = new Notice();
			notice.SetNotice(platformNotice);
			NoticeList.add(notice);
		}
	}
	
	public void AddNotice(TablePlatformNotice tableGameNotice){
		for (Notice notice : NoticeList) {
			if(notice.isConfigNotice()){
				continue;
			}
			if(notice.getId() == tableGameNotice.getId()){
				UpdateNotice(tableGameNotice, notice);
				return;
			}
		}
		Notice notice = new Notice();
		notice.SetNotice(tableGameNotice);
		NoticeList.add(notice);
	}
	
	public void UpdateNotice(TablePlatformNotice tableGameNotice, Notice notice){
		notice.SetNotice(tableGameNotice);
	}
	
	public void RemoveNotice(TablePlatformNotice tableGameNotice){
		for(int i = NoticeList.size(); --i>= 0;){
			Notice notice = NoticeList.get(i);
			if(notice.isConfigNotice()){
				continue;
			}
			if(notice.getId() == tableGameNotice.getId()){
				NoticeList.remove(i);
				break;
			}
		}
	}
	
	public List<Notice> getNoticeList(){
		
		return NoticeList;
		
	}
}
