package com.rwbase.dao.tower;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class TowerGoodsCfgDAO extends CfgCsvDao<TowerGoodsCfg> {
	private static TowerGoodsCfgDAO instance  =  new TowerGoodsCfgDAO();
	private TowerGoodsCfgDAO(){};
	public static TowerGoodsCfgDAO getInstance(){
		return instance;
	}
	@Override
	public Map<String, TowerGoodsCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("tower/TowerGoods.csv",TowerGoodsCfg.class);
		return cfgCacheMap;
	}
	 public List<TowerGoodsCfg> GetCfgsByFormatId(int id){
		 List<TowerGoodsCfg> allCfg = super.getAllCfg();
		 List<TowerGoodsCfg> formatCfg = new ArrayList<TowerGoodsCfg>();
		 for(int i=0;i<allCfg.size();i++){
			 if(allCfg.get(i).formatId ==id){
				 formatCfg.add(allCfg.get(i));
			 }
		 }
		 return formatCfg;
	 }
}
