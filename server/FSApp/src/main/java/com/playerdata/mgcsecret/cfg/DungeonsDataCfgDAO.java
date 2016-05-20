package com.playerdata.mgcsecret.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.playerdata.mgcsecret.data.MSStageInfo;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class DungeonsDataCfgDAO extends CfgCsvDao<DungeonsDataCfg> {
	public static DungeonsDataCfgDAO getInstance(){
		return SpringContextUtil.getBean(DungeonsDataCfgDAO.class);
	}
	
	@Override
	public Map<String, DungeonsDataCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("magicSecret/dungeonsData.csv", DungeonsDataCfg.class);
		Set<Entry<String, DungeonsDataCfg>> entrySet = cfgCacheMap.entrySet();
		for (Entry<String, DungeonsDataCfg> entry : entrySet) {
			if(entry != null){
				DungeonsDataCfg cfg = entry.getValue();
				if (cfg != null) {
					//cfg.ExtraInit();
				}else{
					//GameLog.error("法宝", "CriticalEnhance.csv", "invalid cfg");
				}
			}
		}
		return cfgCacheMap;
	}
	
	public List<MSStageInfo> getNextStages(String stageID){
		List<MSStageInfo> listStages = new ArrayList<MSStageInfo>();
		DungeonsDataCfg stageInfo = getCfgById(stageID);
		for(int i = 1; i <= 3; i++){
			String stgID = (stageInfo.getId() + 1) + "_" + i;
			DungeonsDataCfg stgInfo = getCfgById(stgID);
			if(stgInfo != null){
				MSStageInfo msStgInfo = new MSStageInfo(stgID, String.valueOf(stgInfo.getFabaoBuff()), stgInfo.getEnimy(), stgInfo.getDrop());
				listStages.add(msStgInfo);
			}
		}
		return listStages;
	}
}
