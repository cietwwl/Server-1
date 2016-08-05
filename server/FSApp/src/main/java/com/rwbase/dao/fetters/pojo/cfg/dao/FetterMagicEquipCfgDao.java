package com.rwbase.dao.fetters.pojo.cfg.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;

public class FetterMagicEquipCfgDao extends CfgCsvDao<MagicEquipConditionCfg>{

	
	
	public static int TYPE_MAGICWEAPON = 1;
	
	public static int TYPE_FIXEQUIP = 2;
	
	
	
	public static FetterMagicEquipCfgDao getInstance(){
		return SpringContextUtil.getBean(FetterMagicEquipCfgDao.class);
	}
	
	@Override
	protected Map<String, MagicEquipConditionCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("HeroFetters/magicEquipConditionCfg.csv", MagicEquipConditionCfg.class);
		return cfgCacheMap;
	}

	
	@Override
	public void CheckConfig() {
		List<MagicEquipConditionCfg> list = getAllCfg();
		for (MagicEquipConditionCfg cfg : list) {
			cfg.formateData();
		}
	}
	
	/**
	 * 找出目标类型的配置
	 * @param type 法宝或神器
	 * @return
	 */
	public List<MagicEquipConditionCfg> getCfgByType(int type){
		List<MagicEquipConditionCfg> temp = new ArrayList<MagicEquipConditionCfg>();
		for (MagicEquipConditionCfg cfg : getAllCfg()) {
			if(cfg.getType() == type){
				temp.add(cfg);
			}
		}
		
		return Collections.unmodifiableList(temp);
	}

	
	/**
	 * 根据modelID获取配置列表
	 * @param modelId
	 * @return
	 */
	public List<MagicEquipConditionCfg> getCfgListByModelID(int modelId) {
		List<MagicEquipConditionCfg> temp = new ArrayList<MagicEquipConditionCfg>();
		for (MagicEquipConditionCfg cfg : getAllCfg()) {
			if(cfg.getModelIDList().contains(modelId)){
				temp.add(cfg);
			}
		}
		
		return Collections.unmodifiableList(temp);
		
	}
	
	
	
	
	
}
