package com.rwbase.dao.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.arena.pojo.ArenaPrizeCfg;

public class ArenaPrizeCfgDAO extends CfgCsvDao<ArenaPrizeCfg> {

	private static ArenaPrizeCfgDAO instance;
	private ArenaPrizeCfgDAO(){}
	
	public static ArenaPrizeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ArenaPrizeCfgDAO.class);
	}
	
	@Override
	public Map<String, ArenaPrizeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arena/arenaPrize.csv", ArenaPrizeCfg.class);
		return cfgCacheMap;
	}
	
	public List<String[]> getArenaPrizeByPlace(int place)
	{
		List<String[]> list = new ArrayList<String[]>();
		List<ArenaPrizeCfg> listCfg = getAllCfg();
		String[] arrPlace;
		String[] arrPrize;
		int i;
		for(ArenaPrizeCfg cfg : listCfg){
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
	
	public String getArenaPrizeCfgByPlace(int place)
	{
		List<ArenaPrizeCfg> listCfg = getAllCfg();
		String[] arrPlace;
		for(ArenaPrizeCfg cfg : listCfg){
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
