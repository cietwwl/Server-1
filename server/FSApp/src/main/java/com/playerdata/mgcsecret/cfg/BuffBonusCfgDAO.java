package com.playerdata.mgcsecret.cfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class BuffBonusCfgDAO extends CfgCsvDao<BuffBonusCfg> {
	private HashMap<Integer, ArrayList<BuffBonusCfg>> buffBonusMap = new HashMap<Integer, ArrayList<BuffBonusCfg>>();
	
	public static BuffBonusCfgDAO getInstance(){
		return SpringContextUtil.getBean(BuffBonusCfgDAO.class);
	}
	
	@Override
	public Map<String, BuffBonusCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("magicSecret/buffBonus.csv", BuffBonusCfg.class);
		Collection<BuffBonusCfg> vals = cfgCacheMap.values();
		for (BuffBonusCfg cfg : vals) {
			addMemIntoMap(cfg);
		}
		return cfgCacheMap;
	}
	
	private void addMemIntoMap(BuffBonusCfg cfg){
		ArrayList<BuffBonusCfg> cfgList = buffBonusMap.get(cfg.getLayerID());
		if(cfgList == null) {
			cfgList = new ArrayList<BuffBonusCfg>();
			buffBonusMap.put(cfg.getLayerID(), cfgList);
		}
		cfgList.add(cfg);
	}
	
	/**
	 * 通过buff的方案类型，随机一个方案
	 * @param layerID
	 * @return 
	 */
	public BuffBonusCfg getRandomBuffByLayerID(int layerID){
		if(!buffBonusMap.containsKey(layerID)) return null;
		ArrayList<BuffBonusCfg> cfgList = buffBonusMap.get(layerID);
		int totalRate  = 0;
		for(BuffBonusCfg cfg : cfgList){
			totalRate += cfg.getRate();
		}
		int rankNum = (int)(Math.random() * totalRate);
		for(BuffBonusCfg cfg : cfgList){
			if(rankNum < cfg.getRate()) return cfg;
			rankNum -= cfg.getRate();
		}
		return cfgList.get(0);
	}
}
