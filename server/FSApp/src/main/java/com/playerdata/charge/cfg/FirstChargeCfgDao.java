package com.playerdata.charge.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class FirstChargeCfgDao extends CfgCsvDao<FirstChargeCfg> {
	public static FirstChargeCfgDao getInstance() {
		return SpringContextUtil.getBean(FirstChargeCfgDao.class);
	}
	
	@Override
	public Map<String, FirstChargeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Charge/FirstChargeCfg.csv", FirstChargeCfg.class);
//		FirstChargeCfg FirstChargeCfg = cfgCacheMap.get("1");
//		parseChargeItem(FirstChargeCfg);
		return cfgCacheMap;
	}
	
//	private void parseChargeItem(FirstChargeCfg FirstChargeCfg) {
//		List<ChargeItem> itemList = new ArrayList<ChargeItem>();
//		String chargeItems = FirstChargeCfg.getChargeItems();
//		String[] itemArray = chargeItems.split(";");
//		for (String item : itemArray) {
//			String id = item.split(":")[0]; 
//			int money = Integer.valueOf(item.split(":")[1]); 			
//			int gold = Integer.valueOf(item.split(":")[2]); 			
//			itemList.add(new ChargeItem(id, money, gold));			
//		}
//		FirstChargeCfg.setChargeItems(chargeItems);
//	}
//
	
	public FirstChargeCfg getConfig(){
		FirstChargeCfg cfg = getCfgById("1");
		return cfg;
	}
	
	
}
