package com.rw.service.PeakArena.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class PeakArenaPrizeCfgHelper extends CfgCsvDao<PeakArenaPrizeCfg> {

	
	public static PeakArenaPrizeCfgHelper getInstance() {
		return SpringContextUtil.getBean(PeakArenaPrizeCfgHelper.class);
	}

	
	@Override
	public Map<String, PeakArenaPrizeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PeakArena/peakArenaPrize.csv", PeakArenaPrizeCfg.class);
		return cfgCacheMap;
	}
	
	public List<String[]> getPeakArenaPrizeByPlace(int place)
	{
		List<String[]> list = new ArrayList<String[]>();
		List<PeakArenaPrizeCfg> listCfg = getAllCfg();
		String[] arrPlace;
		String[] arrPrize;
		int i;
		for(PeakArenaPrizeCfg cfg : listCfg){
			arrPlace = cfg.getRange().split(",");
			if(place >= Integer.parseInt(arrPlace[0]) && place <= Integer.parseInt(arrPlace[1])){
				arrPrize = cfg.getPrize().split(",");
				for(i = 0;i < arrPrize.length;i++){
					list.add(arrPrize[i].split("~"));
				}
			}
		}
		
		return list;
	}
	
	public String getPeakArenaPrizeCfgByPlace(int place)
	{
		List<PeakArenaPrizeCfg> listCfg = getAllCfg();
		String[] arrPlace;
		for(PeakArenaPrizeCfg cfg : listCfg){
			arrPlace = cfg.getRange().split(",");
			if(arrPlace.length == 1){
				if(place == Integer.parseInt(arrPlace[0])){
					return cfg.getPrize();
				}
			}
			else if(arrPlace.length > 1){
				if(place >= Integer.parseInt(arrPlace[0]) && place <= Integer.parseInt(arrPlace[1])){
					return cfg.getPrize();
				}
			}
		}
		
		return null;
	}
	
}
