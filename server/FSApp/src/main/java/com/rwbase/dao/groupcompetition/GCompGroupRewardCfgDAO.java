package com.rwbase.dao.groupcompetition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.groupcompetition.pojo.GCompCommonRankRewardCfg;

public class GCompGroupRewardCfgDAO extends GCompCommonRankRewardCfgBaseDAO {
	
	public static GCompGroupRewardCfgDAO getInstance() {
		return SpringContextUtil.getBean(GCompGroupRewardCfgDAO.class);
	}
	
	private final List<GCompCommonRankRewardCfg> list = new ArrayList<GCompCommonRankRewardCfg>();
	
	public GCompCommonRankRewardCfg getByRank(int rank) {
		for (GCompCommonRankRewardCfg cfg : list) {
			if (cfg.getBeginRank() <= rank && cfg.getEndRank() >= rank) {
				return cfg;
			}
		}
		return null;
	}
	
	protected Map<String, GCompCommonRankRewardCfg> initJsonCfg() {
		super.initJsonCfg();
		for (Iterator<String> keyItr = this.cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			GCompCommonRankRewardCfg cfg = this.cfgCacheMap.get(keyItr.next());
			list.add(cfg);
		}
		Collections.sort(list, new CommonRankRewardCfgRankComparator());
		return this.cfgCacheMap;
	}

	@Override
	protected String getFileName() {
		return "GCompGroupRewardCfg.csv";
	}
	
}
