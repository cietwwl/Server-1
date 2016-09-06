package com.rwbase.dao.groupcompetition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupcompetition.pojo.GCompScoreCfg;

public abstract class GCompScoreCfgBaseDAO extends CfgCsvDao<GCompScoreCfg> {

	protected abstract String getFileName();
	
	private int maxContinueWins; // 配置中最大的连胜次数
	private Map<Integer, GCompScoreCfg> _mapByContinueWins = new HashMap<Integer, GCompScoreCfg>();
	
	@Override
	protected Map<String, GCompScoreCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(GroupCompetitionConfigDir.DIR.getFullPath(getFileName()), GCompScoreCfg.class);
		for (Iterator<String> keyItr = this.cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			GCompScoreCfg cfg = cfgCacheMap.get(keyItr.next());
			_mapByContinueWins.put(cfg.getContinueWin(), cfg);
			if (maxContinueWins < cfg.getContinueWin()) {
				maxContinueWins = cfg.getContinueWin();
			}
		}
		return cfgCacheMap;
	}

	/**
	 * 
	 * 根据连胜获取积分奖励配置
	 * 
	 * @param continueWins
	 * @return
	 */
	public GCompScoreCfg getByContinueWins(int continueWins) {
		if (continueWins > maxContinueWins) {
			return _mapByContinueWins.get(maxContinueWins);
		} else {
			return _mapByContinueWins.get(continueWins);
		}
	}
}
