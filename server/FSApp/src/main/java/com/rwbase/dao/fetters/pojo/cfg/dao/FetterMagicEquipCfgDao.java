package com.rwbase.dao.fetters.pojo.cfg.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;

public class FetterMagicEquipCfgDao extends CfgCsvDao<MagicEquipConditionCfg>{

	
	
	public static int TYPE_MAGICWEAPON = 1;
	
	public static int TYPE_FIXEQUIP = 2;
	
	/**key=modelID, 优化检索速度*/
	private Map<Integer,List<MagicEquipConditionCfg>> modelData = new HashMap<Integer, List<MagicEquipConditionCfg>>();
	
	/**key = type*/
	private Map<Integer,List<MagicEquipConditionCfg>> typeData = new HashMap<Integer, List<MagicEquipConditionCfg>>();
	
	
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
			
			List<Integer> modelIDList = cfg.getModelIDList();
			for (Integer modelID : modelIDList) {
				if(modelData.containsKey(modelID)){
					List<MagicEquipConditionCfg> modelList = modelData.get(modelID);
					modelList.add(cfg);
				}else{
					ArrayList<MagicEquipConditionCfg> temp = new ArrayList<MagicEquipConditionCfg>();
					temp.add(cfg);
					modelData.put(modelID, temp);
				}
			}
			
			
			if(typeData.containsKey(cfg.getType())){
				List<MagicEquipConditionCfg> typeList = typeData.get(cfg.getType());
				typeList.add(cfg);
			}else{
				List<MagicEquipConditionCfg> typeList = new ArrayList<MagicEquipConditionCfg>();
				typeList.add(cfg);
				typeData.put(cfg.getType(), typeList);
			}
		}
	}
	
	/**
	 * 找出目标类型的配置
	 * @param type 法宝或神器
	 * @return
	 */
	public List<MagicEquipConditionCfg> getCfgByType(int type){
		if(typeData.containsKey(type)){
			return Collections.unmodifiableList(typeData.get(type));
		}
		return Collections.emptyList();
	}

	
	/**
	 * 根据modelID获取配置列表
	 * @param modelId
	 * @return
	 */
	public List<MagicEquipConditionCfg> getCfgListByModelID(int modelId) {
		if(modelData.containsKey(modelId)){
			return Collections.unmodifiableList(modelData.get(modelId));
		}
		return Collections.emptyList();
		
	}
	
	
	
	
	
}
