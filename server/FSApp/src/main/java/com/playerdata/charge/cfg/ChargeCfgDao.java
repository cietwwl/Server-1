package com.playerdata.charge.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ChargeCfgDao extends CfgCsvDao<ChargeCfg> {
	public static ChargeCfgDao getInstance() {
		return SpringContextUtil.getBean(ChargeCfgDao.class);
	}
	
	@Override
	public Map<String, ChargeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Charge/ChargeCfg.csv", ChargeCfg.class);
		ChargeCfg chargeCfg = cfgCacheMap.get("1");
		parseChargeItem(chargeCfg);
		return cfgCacheMap;
	}
	
	private void parseChargeItem(ChargeCfg chargeCfg) {
		List<ChargeItem> itemList = new ArrayList<ChargeItem>();
		String chargeItems = chargeCfg.getChargeItems();
		String[] itemArray = chargeItems.split(";");
		for (String item : itemArray) {
			String id = item.split(":")[0]; 
			int money = Integer.valueOf(item.split(":")[1]); 			
			itemList.add(new ChargeItem(id, money));			
		}
		chargeCfg.setChargeItems(chargeItems);
	}

	public ChargeCfg getConfig(){
		ChargeCfg cfg = getCfgById("1");
		return cfg;
	}
	
	
}
