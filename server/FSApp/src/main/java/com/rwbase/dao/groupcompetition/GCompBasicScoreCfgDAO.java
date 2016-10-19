package com.rwbase.dao.groupcompetition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupcompetition.pojo.GCompBasicScoreCfg;

public class GCompBasicScoreCfgDAO extends CfgCsvDao<GCompBasicScoreCfg> {
	
	public static GCompBasicScoreCfgDAO getInstance() {
		return SpringContextUtil.getBean(GCompBasicScoreCfgDAO.class);
	}

	private Map<Integer, GCompBasicScoreCfg> basicScoreByBattleResult = new HashMap<Integer, GCompBasicScoreCfg>();
	
	@Override
	protected Map<String, GCompBasicScoreCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(GroupCompetitionConfigDir.DIR.getFullPath("BasicScoreCfg.csv"), GCompBasicScoreCfg.class);
		for (Iterator<String> keyItr = this.cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			GCompBasicScoreCfg cfg = this.cfgCacheMap.get(keyItr.next());
			basicScoreByBattleResult.put(cfg.getBattleResult(), cfg);
		}
		return cfgCacheMap;
	}
	
	public GCompBasicScoreCfg getByBattleResult(int battleResult) {
		return basicScoreByBattleResult.get(battleResult);
	}

}
