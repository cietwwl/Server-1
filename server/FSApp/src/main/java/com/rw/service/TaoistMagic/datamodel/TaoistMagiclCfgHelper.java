package com.rw.service.TaoistMagic.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.TaoistMagic.datamodel.TaoistMagiclCfgHelper"  init-method="init" />

public class TaoistMagiclCfgHelper extends CfgCsvDao<TaoistMagicCfg> {
	public static TaoistMagiclCfgHelper getInstance() {
		return SpringContextUtil.getBean(TaoistMagiclCfgHelper.class);
	}

	@Override
	public Map<String, TaoistMagicCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("TaoistMagic/TaoistMagicCfg.csv",TaoistMagicCfg.class);
		Collection<TaoistMagicCfg> vals = cfgCacheMap.values();
		for (TaoistMagicCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
			//TODO 检查属性是否存在, 每个分页的开放等级必须一样,序号应该连续且没有重复

		}
		
		return cfgCacheMap;
	}
	
	//@Override
	public void CheckConfig(){
		//跨表检查，consumeId是否在TaoistConsumeCfg有定义
		TaoistConsumeCfgHelper helper = TaoistConsumeCfgHelper.getInstance();
		Collection<TaoistMagicCfg> vals = cfgCacheMap.values();
		for (TaoistMagicCfg cfg : vals) {
			int consumeId = cfg.getConsumeId();
			TaoistConsumeCfg consumeCfg = helper.getCfgById(String.valueOf(consumeId));
			if (consumeCfg == null){
				throw new RuntimeException("无效技能消耗ID="+consumeId);
			}
		}
	}
}
