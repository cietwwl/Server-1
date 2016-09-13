package com.rwbase.dao.fighting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fighting.pojo.ExpectedHeroStatusCfg;

public class ExpectedHeroStatusCfgDAO extends CfgCsvDao<ExpectedHeroStatusCfg> {
	
	public static ExpectedHeroStatusCfgDAO getInstance() {
		return SpringContextUtil.getBean(ExpectedHeroStatusCfgDAO.class);
	}
	
	private Map<Integer, Integer> _expectedCountMap;

	@Override
	protected Map<String, ExpectedHeroStatusCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map("SystemFightingExpected/ExpectedHeroStatus.csv", ExpectedHeroStatusCfg.class);
		this._expectedCountMap = new HashMap<Integer, Integer>(cfgCacheMap.size() + 1, 1.0f);
		for(Iterator<String> itr = this.cfgCacheMap.keySet().iterator(); itr.hasNext();) {
			ExpectedHeroStatusCfg cfg = this.cfgCacheMap.get(itr.next());
			try {
				cfg.afterInit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			this._expectedCountMap.put(cfg.getLevel(), cfg.getExpectedHeroCount());
		}
		return cfgCacheMap;
	}
	
	public int getExpectedHeroCount(int level) {
		return this._expectedCountMap.get(level);
	}
}
