package com.rwbase.dao.gamble.pojo.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.friend.CfgFriendGiftDAO;
import com.rwproto.GambleServiceProtos.EGambleType;

public class GambleCfgDAO extends CfgCsvDao<GambleCfg>
{
	public static GambleCfgDAO getInstance() {
		return SpringContextUtil.getBean(GambleCfgDAO.class);
	}
	
	
	public GambleCfg getGambleCfg(EGambleType gambleType){
		return (GambleCfg)getCfgById(String.valueOf(gambleType.getNumber()));
	}

	@Override
	public Map<String, GambleCfg> initJsonCfg() 
	{
		cfgCacheMap = CfgCsvHelper.readCsv2Map("gamble/gamble.csv",GambleCfg.class);
		return cfgCacheMap;
	}
}
