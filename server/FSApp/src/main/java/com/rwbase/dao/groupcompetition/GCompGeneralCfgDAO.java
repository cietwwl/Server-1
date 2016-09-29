package com.rwbase.dao.groupcompetition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.playerdata.groupcompetition.quiz.GCQuizEventItem;
import com.playerdata.groupcompetition.quiz.GCompQuizMgr;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupcompetition.pojo.GCompGeneralCfg;


public class GCompGeneralCfgDAO extends CfgCsvDao<GCompGeneralCfg> {
	public static GCompGeneralCfgDAO getInstance() {
		return SpringContextUtil.getBean(GCompGeneralCfgDAO.class);
	}

	@Override
	public Map<String, GCompGeneralCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupCompetition/GCompGeneralCfg.csv",GCompGeneralCfg.class);
		Collection<GCompGeneralCfg> vals = cfgCacheMap.values();
		for (GCompGeneralCfg cfg : vals) {
			setGeneralPara(cfg);
		}
		return cfgCacheMap;
	}
	
	private void setGeneralPara(GCompGeneralCfg cfg){
		GCQuizEventItem.DEFAULT_MIN_RATE = cfg.getMinBetRate();
		GCompQuizMgr.INIT_BASE_COIN = cfg.getBetOriginal();
		GCompQuizMgr.QUIZ_INIT_RATE = cfg.getInitBetRate();
		Collections.addAll(GCompQuizMgr.QUIZAMOUNT, cfg.getBetAmount().split(","));
	}
}
