package com.rwbase.dao.commonsoul;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.commonsoul.pojo.ExchangeRateCfg;

public class ExchangeRateCfgDAO extends CfgCsvDao<ExchangeRateCfg> {

	private Map<Integer, Integer> _exchangeRates = new HashMap<Integer, Integer>(); // 兑换的比例，key=英雄魂石id，value=多少个万能魂石兑换一个英雄魂石
	
	public static ExchangeRateCfgDAO getInstace() {
		return SpringContextUtil.getBean(ExchangeRateCfgDAO.class);
	}
	@Override
	protected Map<String, ExchangeRateCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map("CommonSoul/ExchangeRate.csv", ExchangeRateCfg.class);
		for (Iterator<String> keyItr = this.cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			ExchangeRateCfg cfg = this.cfgCacheMap.get(keyItr.next());
			_exchangeRates.put(cfg.getTargetSoulItemId(), cfg.getExchangeRate());
		}
		return cfgCacheMap;
	}
	
	public int getExchangeRate(int soulItemId) {
		Integer rate = _exchangeRates.get(soulItemId);
		if (rate == null) {
			return 0;
		}
		return rate.intValue();
	}

}
