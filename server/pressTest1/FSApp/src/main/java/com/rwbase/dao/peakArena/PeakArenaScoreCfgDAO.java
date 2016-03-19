package com.rwbase.dao.peakArena;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.peakArena.pojo.PeakArenaScoreCfg;

public class PeakArenaScoreCfgDAO extends CfgCsvDao<PeakArenaScoreCfg> {

	private static PeakArenaScoreCfgDAO instance;
	private PeakArenaScoreCfgDAO(){}
	
	public static PeakArenaScoreCfgDAO getInstance()
	{
		if(instance == null){
			instance = new PeakArenaScoreCfgDAO();
		}
		return instance;
	}
	
	@Override
	public Map<String, PeakArenaScoreCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arena/peakArenaScore.csv", PeakArenaScoreCfg.class);
		return cfgCacheMap;
	}
	
	public PeakArenaScoreCfg getPeakArenaScoreCfgByScore(int score)
	{
		PeakArenaScoreCfg result = null;
		List<PeakArenaScoreCfg> listCfg = getAllCfg();
		String[] arrRange;
		for( PeakArenaScoreCfg cfg : listCfg){
			arrRange = cfg.getRange().split(",");
			if(score >= Integer.parseInt(arrRange[0]) && score <= Integer.parseInt(arrRange[1])){
				return cfg;
			}
			result = cfg;
		}
		
		return result;
	}
	
}
