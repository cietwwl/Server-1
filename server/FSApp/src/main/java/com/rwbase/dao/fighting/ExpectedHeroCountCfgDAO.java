package com.rwbase.dao.fighting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fighting.pojo.ExpectedHeroCountCfg;

public class ExpectedHeroCountCfgDAO extends CfgCsvDao<ExpectedHeroCountCfg> {
	
	public static ExpectedHeroCountCfgDAO getInstance() {
		return SpringContextUtil.getBean(ExpectedHeroCountCfgDAO.class);
	}
	
	private Map<Integer, Integer> _expectedCountMap;

	@Override
	protected Map<String, ExpectedHeroCountCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map("SystemFightingExpected/ExpectedHeroCount.csv", ExpectedHeroCountCfg.class);
		this._expectedCountMap = new HashMap<Integer, Integer>(cfgCacheMap.size() + 1, 1.0f);
		for(Iterator<ExpectedHeroCountCfg> itr = this.cfgCacheMap.values().iterator(); itr.hasNext();) {
			ExpectedHeroCountCfg cfg = itr.next();
			this._expectedCountMap.put(cfg.getLevel(), cfg.getExpectedHeroCount());
		}
		return cfgCacheMap;
	}
	
	public int getExpectedHeroCount(int level) {
		return this._expectedCountMap.get(level);
	}
}
