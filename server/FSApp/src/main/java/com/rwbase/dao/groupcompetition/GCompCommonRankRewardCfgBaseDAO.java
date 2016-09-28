package com.rwbase.dao.groupcompetition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.playerdata.groupcompetition.util.GCEventsType;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupcompetition.pojo.GCompCommonRankRewardCfg;

public abstract class GCompCommonRankRewardCfgBaseDAO extends CfgCsvDao<GCompCommonRankRewardCfg> {

	protected abstract String getFileName();
	
	private final Map<GCEventsType, List<GCompCommonRankRewardCfg>> _rewardsByType = new HashMap<GCEventsType, List<GCompCommonRankRewardCfg>>();
	
	@Override
	protected Map<String, GCompCommonRankRewardCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(GroupCompetitionConfigDir.DIR.getFullPath(this.getFileName()), GCompCommonRankRewardCfg.class);
		for (Iterator<String> keyItr = this.cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			GCompCommonRankRewardCfg cfg = this.cfgCacheMap.get(keyItr.next());
			String[] ranks = cfg.getRank().split("-");
			int beginRank = Integer.parseInt(ranks[0]);
			cfg.setBeginRank(beginRank);
			cfg.setEndRank(ranks.length > 1 ? Integer.parseInt(ranks[1]) : beginRank);
			String[] gifts = cfg.getGiftId().split(";");
			Map<Integer, Integer> map = new HashMap<Integer, Integer>(gifts.length, 1.5f);
			for (String tempGift : gifts) {
				String[] tempGiftSplit = tempGift.split("_");
				map.put(Integer.parseInt(tempGiftSplit[0]), Integer.parseInt(tempGiftSplit[1]));
			}
			cfg.setRewardMap(map);
			GCEventsType type = GCEventsType.getBySign(cfg.getMatchType());
			List<GCompCommonRankRewardCfg> list = _rewardsByType.get(type);
			if (list == null) {
				list = new ArrayList<GCompCommonRankRewardCfg>();
				_rewardsByType.put(type, list);
			}
			list.add(cfg);
		}
		Comparator<GCompCommonRankRewardCfg> comparator = new CommonRankRewardCfgRankComparator();
		for (Iterator<GCEventsType> keyItr = _rewardsByType.keySet().iterator(); keyItr.hasNext();) {
			List<GCompCommonRankRewardCfg> list = _rewardsByType.get(keyItr.next());
			Collections.sort(list, comparator);
		}
		return cfgCacheMap;
	}
	
	public GCompCommonRankRewardCfg getByMatchTypeAndRank(GCEventsType eventsType, int rank) {
		List<GCompCommonRankRewardCfg> list = _rewardsByType.get(eventsType);
		for (GCompCommonRankRewardCfg cfg : list) {
			if (cfg.getBeginRank() <= rank && cfg.getEndRank() >= rank) {
				return cfg;
			}
		}
		return null;
	}

	protected static class CommonRankRewardCfgRankComparator implements Comparator<GCompCommonRankRewardCfg> {

		@Override
		public int compare(GCompCommonRankRewardCfg o1, GCompCommonRankRewardCfg o2) {
			return o1.getBeginRank() < o2.getBeginRank() ? -1 : 1;
		}

	}
	
	public static void main(String[] args) {
		GCompCommonRankRewardCfgBaseDAO dao = new GCompCommonRankRewardCfgBaseDAO() {
			
			@Override
			protected String getFileName() {
				return "GCompScoreRewardCfg.csv";
			}
		};
		dao.initJsonCfg();
		GCompCommonRankRewardCfg cfg = dao.getByMatchTypeAndRank(GCEventsType.FINAL, 5);
		System.out.println(cfg.getBeginRank() + "-" + cfg.getEndRank() + ":" + cfg.getRewardMap());
	}
}
