package com.rounter.controller.ucParam.cfg;

import java.util.Map;

import com.rounter.csv.CfgCsvDao;
import com.rounter.csv.CfgCsvHelper;

public class UCGiftCfgDAO extends CfgCsvDao<UCGiftCfg>{

	private static UCGiftCfgDAO instance = new UCGiftCfgDAO();
	
	public static UCGiftCfgDAO getInstance(){
		if(instance == null){
			instance = new UCGiftCfgDAO();
		}
		return instance;
	}
	
	
	private UCGiftCfgDAO() {
		initJsonCfg();
	}



	@Override
	protected Map<String, UCGiftCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("ucCfg/UCGiftCfg.csv", UCGiftCfg.class);
		return cfgCacheMap;
	}

	
	
}
