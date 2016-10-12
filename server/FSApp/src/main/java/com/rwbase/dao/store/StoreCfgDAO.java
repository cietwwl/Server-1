package com.rwbase.dao.store;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.store.pojo.StoreCfg;
import com.rwbase.dao.store.pojo.WakenLotteryDrawCfg;

public class StoreCfgDAO extends CfgCsvDao<StoreCfg> {
	public static StoreCfgDAO getInstance() {
		return SpringContextUtil.getBean(StoreCfgDAO.class);
	}
	@Override
	public Map<String, StoreCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("store/StoreCfg.csv",StoreCfg.class);
		parse();
		return cfgCacheMap;
	}
	
	public StoreCfg getStoreCfg(int type){
		List<StoreCfg> allcfg = super.getAllCfg();
		for (StoreCfg cfg : allcfg) {
			if(cfg.getType() == type){
				return cfg;
			}
		}
		return null;
	}
	public StoreCfg getStoreCfgByID(int id){
		List<StoreCfg> allcfg = super.getAllCfg();
		for (StoreCfg cfg : allcfg) {
			if(cfg.getId() == id){
				return cfg;
			}
		}
		return null;
	}

	private void parse(){
		for (Iterator<Entry<String, StoreCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, StoreCfg> entry = iterator.next();
			StoreCfg cfg = entry.getValue();
			String refreshDay = cfg.getRefreshDay();
			if (!refreshDay.equals("0")) {
				String[] split = refreshDay.split(",");
				ArrayList<Integer> refreshDayList = new ArrayList<Integer>();
				for (String value : split) {
					refreshDayList.add(Integer.parseInt(value));
				}
				cfg.setRefreshDayList(refreshDayList);
			}
		}
	}
}
