package com.rwbase.dao.tower;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class TowerAwardCfgDAO extends CfgCsvDao<TowerAwardCfg> {
	private static TowerAwardCfgDAO instance  =  new TowerAwardCfgDAO();
	private TowerAwardCfgDAO(){};
	public static TowerAwardCfgDAO getInstance(){
		return instance;
	}
	@Override
	public Map<String, TowerAwardCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("tower/TowerAward.csv",TowerAwardCfg.class);
		return cfgCacheMap;
	}
	
	 private List<TowerAwardCfg> GetTowerCfgListByLevel(int level) {//35/5 = 7
	        int cigLevel = (level / 5) * 5;
	        List<TowerAwardCfg> cfgList = new ArrayList<TowerAwardCfg>();
	    	for (TowerAwardCfg cfg : super.getAllCfg()) {
	    		 if (cfg.level == cigLevel) {
	    			 cfgList.add(cfg);
		         }
			}
	        return cfgList;
	    }
	    public TowerAwardCfg GetLevelTowerCfgByTowerID(int level,int towerId) {//35 +5
	        List<TowerAwardCfg> cfgList = GetTowerCfgListByLevel(level);
	        if (cfgList != null) {
	            for (int index = 0; index < cfgList.size(); index++) {
	                if (cfgList.get(index).towerId == towerId) {
	                    return cfgList.get(index);
	                }
	            }
	        }
	        return null;
	    }

}
