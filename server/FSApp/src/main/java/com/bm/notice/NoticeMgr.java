package com.bm.notice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.tools.ant.taskdefs.condition.HasMethod;

import com.bm.serverStatus.ServerStatusMgr;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;
import com.rwbase.dao.gameNotice.TableGameNotice;
import com.rwbase.dao.gameNotice.pojo.GameNoticeDataHolder;
import com.rwbase.dao.notice.AnnouncementCfgDAO;
import com.rwbase.dao.notice.pojo.AnnouncementCfg;
import com.rwbase.dao.serverData.ServerGmNotice;

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
	
	private HashMap<Integer, Notice> GmNotices = new HashMap<Integer, Notice>();
	
	protected NoticeMgr() {
		
	}
	
	public void initNotice(){
		AnnouncementCfgDAO.getInstance().parse();
		
		NoticeList.clear();
		long secondLevelMillis = DateUtils.getSecondLevelMillis();
		//加载配置表的公告
		List<AnnouncementCfg> allCfg = AnnouncementCfgDAO.getInstance().getAllCfg();
		if (allCfg != null) {
			for (AnnouncementCfg announcementCfg : allCfg) {
				if (announcementCfg.getEndTime() < secondLevelMillis || announcementCfg.getPushType() == PUSHTYPE_PLATFORM) {
					continue;
				}
				Notice notice = new Notice();
				notice.SetNotice(announcementCfg);
				NoticeList.add(notice);
			}
		}
		
		
		//加载数据库的公告
		refreshGameNotice();
	}
	
	public void refreshGameNotice() {
		long secondLevelMillis = DateUtils.getSecondLevelMillis();

		HashMap<Integer, TableGameNotice> map = GameManager.getGameNotice().getGameNotices();
		for (Iterator<Entry<Integer, TableGameNotice>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, TableGameNotice> entry = iterator.next();
			TableGameNotice value = entry.getValue();
			if (value.getEndTime() * 1000 < secondLevelMillis) {
				continue;
			}
			if (GmNotices.containsKey(value.getNoticeId())) {
				Notice notice = GmNotices.get(value.getNoticeId());
				if (notice.getId() != 0 && notice.getId() == value.getNoticeId()) {
					UpdateNotice(value, notice);
				}
			} else {
				Notice notice = new Notice();
				notice.SetNotice(value);
				GmNotices.put(value.getNoticeId(), notice);
			}
		}
	}
	
	public void UpdateNotice(TableGameNotice tableGameNotice, Notice notice){
		notice.SetNotice(tableGameNotice);
	}
	
	public void RemoveNotice(TableGameNotice tableGameNotice) {
		GmNotices.remove(tableGameNotice.getNoticeId());
	}
	
	public List<Notice> getNoticeList(){
		List<Notice> result = new ArrayList<Notice>();
		result.addAll(NoticeList);
		result.addAll(GmNotices.values());
		return result;
		
	}
}
