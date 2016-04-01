package com.rwbase.dao.fashion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

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
		Collection<Entry<String, FashionQuantityEffectCfg>> values =  cfgCacheMap.entrySet();
		ArrayList<FashionQuantityEffectCfg> lst = new ArrayList<FashionQuantityEffectCfg>();
		for (Entry<String, FashionQuantityEffectCfg> entry : values) {
			FashionQuantityEffectCfg cfg = entry.getValue();
			cfg.ExtraInit(entry.getKey());
			lst.add(cfg);
		}
		Collections.sort(lst, FashionQuantityEffectCfg.getComparator());
		effLst = lst.toArray(effLst);
		initZeroEff = FashionQuantityEffectCfg.ZeroEffect();
		if (initZeroEff == null){
			GameLog.error("时装", "初始化失败", "无法构造ZeroEffect");
		}
		return cfgCacheMap;
	}
	
	/**
	 * 搜索合适的增益配置，返回配置必须是刚好比需要的数量小或者相等，并且是所有满足这个条件的最大配置
	 * @param quantity
	 * @return
	 */
	public FashionQuantityEffectCfg searchOption(int quantity){
		FashionQuantityEffectCfg result = initZeroEff;
		for (int i = effLst.length -1; i >= 0 ; i--) {
			FashionQuantityEffectCfg cfg = effLst[i];
			if (quantity >= cfg.getQuantity()){
				result = cfg;
				break;
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
