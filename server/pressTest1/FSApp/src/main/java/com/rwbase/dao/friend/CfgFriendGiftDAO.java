package com.rwbase.dao.friend;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.friend.vo.CfgFriendGift;

public class CfgFriendGiftDAO extends CfgCsvDao<CfgFriendGift> {
	private static CfgFriendGiftDAO instance = new CfgFriendGiftDAO();
	private CfgFriendGiftDAO() {
		
	}
	
	public static CfgFriendGiftDAO getInstance(){
		return instance;
	}
	
	public Map<String, CfgFriendGift> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("friend/friendGift.csv", CfgFriendGift.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public CfgFriendGift getFriendGiftCfg(String level){
		return (CfgFriendGift)getCfgById(level + "");
	}
}