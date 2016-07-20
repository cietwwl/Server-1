package com.rwbase.dao.fetters.pojo.cfg.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;

public class FetterMagicEquipCfgDao extends CfgCsvDao<MagicEquipConditionCfg>{

	private Map<Integer, List<MagicEquipConditionCfg>> configMap = new HashMap<Integer, List<MagicEquipConditionCfg>>();
	
	public static FetterMagicEquipCfgDao getInstance(){
		return SpringContextUtil.getBean(MagicEquipConditionCfg.class);
	}
	
	@Override
	protected Map<String, MagicEquipConditionCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fetters/FetterMagicEquipCfg.csv", MagicEquipConditionCfg.class);
		return cfgCacheMap;
	}

	
	private MagicEquipConditionCfg getCfg(int cfgID){
		return getCfg(fetterID);
	}

	@Override
	public void CheckConfig() {
		List<MagicEquipConditionCfg> list = getAllCfg();
		for (MagicEquipConditionCfg cfg : list) {
			
			cfg.formateData();
			
			List<MagicEquipConditionCfg> dataList = configMap.get(cfg.getItemModelId());
			if(dataList == null){
				dataList = new ArrayList<MagicEquipConditionCfg>();
				configMap.put(cfg.getItemModelId(), dataList);
			}
			dataList.add(cfg);
		}
	}
	
	/**
	 * 检查配置内对应的羁绊配置
	 * @param cfgID  羁绊条件ID
	 * @param lv 等级
	 * @param star 星级
	 * @return
	 */
	public MagicEquipConditionCfg getCfgByLvStar(int cfgID, int lv, int star){
		MagicEquipConditionCfg cfg = null;
		
		List<MagicEquipConditionCfg> list = configMap.get(cfgID);
		if(list != null && !list.isEmpty()){
			for (MagicEquipConditionCfg i : list) {
				if(i.get)
			}
		}
		
		
		return cfg;
	}
	
}
