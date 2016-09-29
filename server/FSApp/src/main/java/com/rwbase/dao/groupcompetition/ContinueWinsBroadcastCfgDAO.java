package com.rwbase.dao.groupcompetition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupcompetition.pojo.ContinueWinsBroadcastCfg;

public class ContinueWinsBroadcastCfgDAO extends CfgCsvDao<ContinueWinsBroadcastCfg> {

	public static ContinueWinsBroadcastCfgDAO getInstance() {
		return SpringContextUtil.getBean(ContinueWinsBroadcastCfgDAO.class);
	}
	
	private final Map<Integer, Integer> mapByContinueWins = new HashMap<Integer, Integer>();
	private int maxContinueWins;
	
	@Override
	protected Map<String, ContinueWinsBroadcastCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(GroupCompetitionConfigDir.DIR.getFullPath("ContinueWinsBroadcastCfg.csv"), ContinueWinsBroadcastCfg.class);
		for (Iterator<String> keyItr = this.cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			ContinueWinsBroadcastCfg cfg = this.cfgCacheMap.get(keyItr.next());
			mapByContinueWins.put(cfg.getContinueWinTimes(), cfg.getPmdId());
			if (maxContinueWins < cfg.getContinueWinTimes()) {
				maxContinueWins = cfg.getContinueWinTimes();
			}
		}
		return cfgCacheMap;
	}

	public Integer getBroadcastId(int continueWins) {
		if(maxContinueWins < continueWins) {
			return mapByContinueWins.get(maxContinueWins);
		}
		return mapByContinueWins.get(continueWins);
	}
}
