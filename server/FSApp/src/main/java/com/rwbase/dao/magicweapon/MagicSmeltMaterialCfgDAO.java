package com.rwbase.dao.magicweapon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.magicweapon.pojo.MagicSmeltMaterialCfg;

public class MagicSmeltMaterialCfgDAO  extends CfgCsvDao<MagicSmeltMaterialCfg>{

	public static MagicSmeltMaterialCfgDAO getInstance() {
		return SpringContextUtil.getBean(MagicSmeltMaterialCfgDAO.class);
	}
	
	private int maxAptiude;
	private Map<Integer, MagicSmeltMaterialCfg> MagicSmeltMaterialCfgMap;
	
	public int getMaxAptiude() {
		return maxAptiude;
	}

	@Override
	protected Map<String, MagicSmeltMaterialCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("MagicWeapon/MagicSmeltMaterial.csv", MagicSmeltMaterialCfg.class);
		MagicSmeltMaterialCfgMap = new HashMap<Integer, MagicSmeltMaterialCfg>();
		int maxValue = -1;
		for (Iterator<Entry<String, MagicSmeltMaterialCfg> > iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, MagicSmeltMaterialCfg> entry = iterator.next();
			MagicSmeltMaterialCfg cfg = entry.getValue();
			String needMaterials = cfg.getGoods();
			String[] split1 = needMaterials.split(";");
			HashMap<Integer, Integer> needMaterialMap = new HashMap<Integer, Integer>();
			for (String value : split1) {
				String[] split2 = value.split("_");
				if(split2.length != 2){
					GameLog.error("MagicSmeltMaterialCfgDAO", "MagicSmeltMaterialCfgDAO", "MagicSmeltMaterialCfg 所需材料填写错误!Aptitude:" + cfg.getAptitude());
					continue;
				}
				int modelId = Integer.parseInt(split2[0]);
				int count = Integer.parseInt(split2[1]);
				needMaterialMap.put(modelId, count);
				
			}
			cfg.setNeedMaterialMap(needMaterialMap);
			if(cfg.getAptitude() > maxValue){
				maxValue = cfg.getAptitude();
			}
			MagicSmeltMaterialCfgMap.put(cfg.getAptitude(), cfg);
		}
		maxAptiude = maxValue;
		
		return cfgCacheMap;
	}

	public MagicSmeltMaterialCfg getMagicSmeltMaterialCfgByAptitude(int aptitude){
		return MagicSmeltMaterialCfgMap.get(aptitude);
	}
}
