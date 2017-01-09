package com.playerdata.activity.notice.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activity.notice.cfg.ActivityNoticeCfgDAO"  init-method="init" />

public class ActivityNoticeCfgDAO extends CfgCsvDao<ActivityNoticeCfg> {
	public static ActivityNoticeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityNoticeCfgDAO.class);
	}

	@Override
	public Map<String, ActivityNoticeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("mainmsg/NoticeCfg.csv", ActivityNoticeCfg.class);
		return cfgCacheMap;
	}
}
