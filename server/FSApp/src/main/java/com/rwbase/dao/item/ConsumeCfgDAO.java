package com.rwbase.dao.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.common.enu.eConsumeTypeDef;
import com.rwbase.dao.item.pojo.ConsumeCfg;

public class ConsumeCfgDAO extends CfgCsvDao<ConsumeCfg>{
	public static ConsumeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ConsumeCfgDAO.class);
	}

		@Override
		public Map<String, ConsumeCfg> initJsonCfg() {
			cfgCacheMap = CfgCsvHelper.readCsv2Map("item/Consume.csv",ConsumeCfg.class);
			return cfgCacheMap;
		}
		
		public List<ConsumeCfg> getEnhanceList(){
			List<ConsumeCfg> allcfg = super.getAllCfg();
			List<ConsumeCfg> list = new ArrayList<ConsumeCfg>();
			for (ConsumeCfg cfg : allcfg) {
				if(cfg.getConsumeType() == eConsumeTypeDef.EquipStrengthConsume.getOrder()){
					list.add(cfg);
				}
			}
			return list;
		}
		public HashMap<Integer,ConsumeCfg> getEnhanceMap(){
			List<ConsumeCfg> allcfg = super.getAllCfg();
			HashMap<Integer,ConsumeCfg> list = new HashMap<Integer, ConsumeCfg>();
			for (ConsumeCfg cfg : allcfg) {
				if(cfg.getConsumeType() == eConsumeTypeDef.EquipStrengthConsume.getOrder()){
					list.put(cfg.getId(),cfg);
				}
			}
			return list;
		}
}
