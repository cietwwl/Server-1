package com.rwbase.dao.groupcompetition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupcompetition.pojo.GCompChampionRewardCfg;

public class GCompChampionRewardCfgDAO extends CfgCsvDao<GCompChampionRewardCfg> {
	
	public static GCompChampionRewardCfgDAO getInstance() {
		return SpringContextUtil.getBean(GCompChampionRewardCfgDAO.class);
	}
	
	private Map<Integer, GCompChampionRewardCfg> rewardByPos = new HashMap<Integer, GCompChampionRewardCfg>();

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
			rewardByPos.put(cfg.getPosition(), cfg);
		}
		return this.cfgCacheMap;
	}
	
	public GCompChampionRewardCfg getByPos(Integer pos) {
		return rewardByPos.get(pos);
	}

}
