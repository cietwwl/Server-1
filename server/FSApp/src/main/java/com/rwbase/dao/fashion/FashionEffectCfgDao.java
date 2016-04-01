package com.rwbase.dao.fashion;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.common.enu.ECareer;

public class FashionEffectCfgDao extends CfgCsvDao<FashionEffectCfg> {
	public static FashionEffectCfgDao getInstance() {
		return SpringContextUtil.getBean(FashionEffectCfgDao.class);
	}

	private Map<IReadOnlyPair<Integer,ECareer>,FashionEffectCfg> effectMapping;
	@Override
	public Map<String, FashionEffectCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fashion/FashionEffectCfg.csv", FashionEffectCfg.class);
		Collection<Entry<String,FashionEffectCfg>> values = cfgCacheMap.entrySet();
		effectMapping = new HashMap<IReadOnlyPair<Integer,ECareer>, FashionEffectCfg>();
		for (Entry<String,FashionEffectCfg> entry : values) {
			FashionEffectCfg cfg = entry.getValue(); 
			cfg.ExtraInit(entry.getKey());
			IReadOnlyPair<Integer,ECareer> pair = Pair.CreateReadonly(cfg.getFashionId(), cfg.getCareerTypeField());
			if (effectMapping.put(pair, cfg)!=null){
				GameLog.info("时装", "FashionEffectCfg.csv配置警告", "重复的<时装关键字，职业>:"+cfg.getFashionId()+","+cfg.getCareerTypeField(), null);
			}
		}
		return cfgCacheMap;
	}

	public FashionEffectCfg getConfig(String id) {
		FashionEffectCfg cfg = (FashionEffectCfg) getCfgById(id);
		return cfg;
	}

	public FashionEffectCfg getConfig(int fashionId, int careerType) {
		ECareer career = ECareer.valueOf(careerType);
		if (career == null) return null;
		IReadOnlyPair<Integer,ECareer> pair = Pair.CreateReadonly(fashionId,career);
		return effectMapping.get(pair);
	}
}
