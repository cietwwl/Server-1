package com.rwbase.dao.fashion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class FashionQuantityEffectCfgDao extends CfgCsvDao<FashionQuantityEffectCfg> {
	public static FashionQuantityEffectCfgDao getInstance() {
		return SpringContextUtil.getBean(FashionQuantityEffectCfgDao.class);
	}
	
	private FashionQuantityEffectCfg initZeroEff;
	private FashionQuantityEffectCfg[] effLst=new FashionQuantityEffectCfg[0];
	
	@Override
	public Map<String, FashionQuantityEffectCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fashion/FashionQuantityEffectCfg.csv", FashionQuantityEffectCfg.class);
		Collection<FashionQuantityEffectCfg> values =  cfgCacheMap.values();
		ArrayList<FashionQuantityEffectCfg> lst = new ArrayList<FashionQuantityEffectCfg>();
		for (FashionQuantityEffectCfg cfg : values) {
			cfg.ExtraInit();
			lst.add(cfg);
		}
		Collections.sort(lst, FashionQuantityEffectCfg.getComparator());
		effLst = lst.toArray(effLst);
		initZeroEff = FashionQuantityEffectCfg.ZeroEffect();
		if (initZeroEff == null){
			GameLog.info("时装", "初始化失败", "无法构造ZeroEffect", null);
		}
		return cfgCacheMap;
	}
	
	public FashionQuantityEffectCfg searchOption(int quantity){
		FashionQuantityEffectCfg result = initZeroEff;
		for (int i = 0; i < effLst.length; i++) {
			FashionQuantityEffectCfg cfg = effLst[i];
			if (quantity <= cfg.getQuantity()){
				result = cfg;
				break;
			}
			if (i + 1 == effLst.length){
				//Exhausted,use the largest
				result = cfg;
			}
		}
		return result;
	}
	
	public FashionQuantityEffectCfg getConfig(String quntity){
		FashionQuantityEffectCfg cfg = (FashionQuantityEffectCfg)getCfgById(quntity);
		return cfg;
	}
	
	public FashionQuantityEffectCfg getConfig(int quntity){
		FashionQuantityEffectCfg cfg = (FashionQuantityEffectCfg)getCfgById(String.valueOf(quntity));
		return cfg;
	}
}
