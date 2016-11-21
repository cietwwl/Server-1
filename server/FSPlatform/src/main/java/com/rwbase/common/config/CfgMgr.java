package com.rwbase.common.config;

import com.rwbase.dao.notice.AnnouncementCfgDAO;

public class CfgMgr {
	public static CfgMgr instance = new CfgMgr();
	
	public static CfgMgr getInstance(){
		return instance;
	}
	
	public void init(){
		AnnouncementCfgDAO.getInstance().init();
	}
}
