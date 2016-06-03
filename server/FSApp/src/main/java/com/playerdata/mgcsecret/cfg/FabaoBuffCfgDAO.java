package com.playerdata.mgcsecret.cfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class FabaoBuffCfgDAO extends CfgCsvDao<FabaoBuffCfg> {
	private HashMap<Integer, ArrayList<FabaoBuffCfg>> fabaoBuffMap = new HashMap<Integer, ArrayList<FabaoBuffCfg>>();
	
	public static FabaoBuffCfgDAO getInstance(){
		return SpringContextUtil.getBean(FabaoBuffCfgDAO.class);
	}
	
	@Override
	public Map<String, FabaoBuffCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("magicSecret/fabaoBuff.csv", FabaoBuffCfg.class);
		Collection<FabaoBuffCfg> vals = cfgCacheMap.values();
		for (FabaoBuffCfg cfg : vals) {
			addMemIntoMap(cfg);
		}
		return cfgCacheMap;
	}
	
	private void addMemIntoMap(FabaoBuffCfg cfg){
		ArrayList<FabaoBuffCfg> cfgList = fabaoBuffMap.get(cfg.getLayerID());
		if(cfgList == null) {
			cfgList = new ArrayList<FabaoBuffCfg>();
			fabaoBuffMap.put(cfg.getLayerID(), cfgList);
		}
		cfgList.add(cfg);
	}
	
	/**
	 * 通过buff的方案类型，随机一个方案
	 * @param layerID
	 * @return 
	 */
	public FabaoBuffCfg getRandomBuffByLayerID(int layerID){
		if(!fabaoBuffMap.containsKey(layerID)) return null;
		ArrayList<FabaoBuffCfg> cfgList = fabaoBuffMap.get(layerID);
		int totalRate  = 0;
		for(FabaoBuffCfg cfg : cfgList){
			totalRate += cfg.getRate();
		}
		int rankNum = (int)(Math.random() * totalRate);
		for(FabaoBuffCfg cfg : cfgList){
			if(rankNum < cfg.getRate()) return cfg;
			rankNum -= cfg.getRate();
		}
		return cfgList.get(0);
	}
}
