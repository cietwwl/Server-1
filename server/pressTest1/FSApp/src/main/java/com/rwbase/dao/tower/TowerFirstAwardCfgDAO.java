package com.rwbase.dao.tower;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class TowerFirstAwardCfgDAO extends CfgCsvDao<TowerFirstAwardCfg> {
	private static TowerFirstAwardCfgDAO instance  =  new TowerFirstAwardCfgDAO();
	private TowerFirstAwardCfgDAO(){};
	public static TowerFirstAwardCfgDAO getInstance(){
		return instance;
	}
	@Override
	public Map<String, TowerFirstAwardCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("tower/TowerFirstAward.csv",TowerFirstAwardCfg.class);
		return cfgCacheMap;
	}

//	 public String[] GetGoodsStr(String id) {
//	    	HashMap<Integer, Integer> dic = new HashMap<Integer, Integer>();
//	    	TowerFirstAwardCfg goodCfg = (TowerFirstAwardCfg)super.getCfgById(id);
//	        String goodStr = goodCfg.goods;//600001_3,600002_2,600003_1
//	        String[] goodsList =goodStr.split(",");
//	        return goodsList;
//	  }
	 public String GetGooldListStr(String id){
	    	TowerFirstAwardCfg goodCfg = (TowerFirstAwardCfg)super.getCfgById(id);
	        String goodStr = goodCfg.goods;//600001_3,600002_2,600003_1
	        return goodStr;
	 }
}
