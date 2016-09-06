package com.rwbase.dao.fresherActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityFinalRewardCfg;

public class FresherActivityFinalRewardCfgDao  extends CfgCsvDao<FresherActivityFinalRewardCfg>{
	
	private final static List<FresherActivityFinalRewardCfg> SortFinalRewardList = new ArrayList<FresherActivityFinalRewardCfg>();
	
	public static FresherActivityFinalRewardCfgDao getInstance() {
		return SpringContextUtil.getBean(FresherActivityFinalRewardCfgDao.class);
	}
	
	@Override
	public Map<String, FresherActivityFinalRewardCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fresherActivity/FresherActivityFinalRewardCfg.csv", FresherActivityFinalRewardCfg.class);
		return cfgCacheMap;
	}

	public FresherActivityFinalRewardCfg getFresherActivityCfg(int cfgId){
		FresherActivityFinalRewardCfg cfg = (FresherActivityFinalRewardCfg)cfgCacheMap.get(String.valueOf(cfgId));
		return cfg;
	}
	
	public List<FresherActivityFinalRewardCfg> getSortFinalRewardList() {
		if (SortFinalRewardList.size() <= 0) {
			List<FresherActivityFinalRewardCfg> allCfg = getAllCfg();
			Collections.sort(allCfg,
					new Comparator<FresherActivityFinalRewardCfg>() {

						@Override
						public int compare(FresherActivityFinalRewardCfg o1,
								FresherActivityFinalRewardCfg o2) {
							// TODO Auto-generated method stub
							return o1.getId() > o2.getId() ? 1 : -1;
						}
					});
			SortFinalRewardList.addAll(allCfg);
		}
		return SortFinalRewardList;
	}
}
