package com.playerdata.charge.cfg;

import java.util.HashMap;
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
//		for (FirstChargeCfg cfgTmp : cfgCacheMap.values()) {
//			parseFirstChargeList(cfgTmp);
//		}
		return cfgCacheMap;
	}
	
//private void parseFirstChargeList(FirstChargeCfg cfgTmp) {
////	String giftStr = cfgTmp.getReward();
//	String giftStr = "1:1";
//	Map<String,Integer> giftCountMap = new HashMap<String, Integer>();
//	String[] giftSplit = giftStr.split(";");
//	for (String giftTmp : giftSplit) {
//		String giftId = giftTmp.split(":")[0];
//		Integer giftCount = Integer.parseInt(giftTmp.split(":")[1]);
//		giftCountMap.put(giftId, giftCount);
//	}
//	cfgTmp.setGiftMap(giftCountMap);
//		
//	}

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
