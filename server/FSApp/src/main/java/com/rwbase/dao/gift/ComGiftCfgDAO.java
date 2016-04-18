package com.rwbase.dao.gift;

import java.util.HashMap;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ComGiftCfgDAO extends CfgCsvDao<ComGiftCfg> {


	public static ComGiftCfgDAO getInstance() {
		return SpringContextUtil.getBean(ComGiftCfgDAO.class);
	}

	
	@Override
	public Map<String, ComGiftCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Gift/ComGiftCfg.csv", ComGiftCfg.class);
		for (ComGiftCfg cfgTmp : cfgCacheMap.values()) {
			parseGiftList(cfgTmp);
		}
		
		return cfgCacheMap;
	}
	
	private void parseGiftList(ComGiftCfg cfgTmp) {
		String giftStr = cfgTmp.getGift();
		Map<String,Integer> giftCountMap = new HashMap<String, Integer>();
		String[] giftSplit = giftStr.split(";");
		for (String giftTmp : giftSplit) {
			String giftId = giftTmp.split(":")[0];
			Integer giftCount = Integer.parseInt(giftTmp.split(":")[1]);
			giftCountMap.put(giftId, giftCount);
		}
		cfgTmp.setGiftMap(giftCountMap);
	}


	public ComGiftCfg getConfig(String id){
		ComGiftCfg cfg = getCfgById(id);
		return cfg;
	}
	



}