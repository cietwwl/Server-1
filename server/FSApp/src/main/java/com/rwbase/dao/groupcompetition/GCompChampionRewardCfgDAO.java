package com.rwbase.dao.groupcompetition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupcompetition.pojo.GCompChampionRewardCfg;

public class GCompChampionRewardCfgDAO extends CfgCsvDao<GCompChampionRewardCfg> {

	@Override
	protected Map<String, GCompChampionRewardCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(GroupCompetitionConfigDir.DIR.getFullPath("GCompChampionRewardCfg.csv"), GCompChampionRewardCfg.class);
		for (Iterator<String> keyItr = this.cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			GCompChampionRewardCfg cfg = this.cfgCacheMap.get(keyItr.next());
			String[] gifts = cfg.getRewardId().split(";");
			Map<Integer, Integer> map = new HashMap<Integer, Integer>(gifts.length, 1.5f);
			for (String tempGift : gifts) {
				String[] tempGiftSplit = tempGift.split("_");
				map.put(Integer.parseInt(tempGiftSplit[0]), Integer.parseInt(tempGiftSplit[1]));
			}
			cfg.setRewardMap(map);
		}
		return this.cfgCacheMap;
	}

}
