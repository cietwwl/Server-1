package com.rwbase.dao.fightgrowth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fightgrowth.pojo.FSUserFightingGrowthTitleCfg;

public class FSUserFightingGrowthTitleCfgDAO extends CfgCsvDao<FSUserFightingGrowthTitleCfg> {
	
	private String _firstTitleKey;

	public static FSUserFightingGrowthTitleCfgDAO getInstance() {
		return SpringContextUtil.getBean(FSUserFightingGrowthTitleCfgDAO.class);
	}
	
	private Map<String, Integer> parseItemString(String str) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		String[] array = str.split(";");
		for(int i = 0; i < array.length; i++) {
			String[] single = array[i].split(",");
			map.put(single[0], Integer.parseInt(single[1]));
		}
		return map;
	}
	
	public FSUserFightingGrowthTitleCfg getFirstTitleCfg() {
		return this.getCfgById(_firstTitleKey);
	}
	
	
	@Override
	protected Map<String, FSUserFightingGrowthTitleCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map("fightingGrowth/FightingGrowthTitle.csv", FSUserFightingGrowthTitleCfg.class);
		for (Iterator<FSUserFightingGrowthTitleCfg> itr = this.cfgCacheMap.values().iterator(); itr.hasNext();) {
			FSUserFightingGrowthTitleCfg temp = itr.next();
			temp.setItemRequiredMap(this.parseItemString(temp.getItemRequired()));
			temp.setRewardItemsMap(this.parseItemString(temp.getRewards()));
			if (temp.isFirst()) {
				if (this._firstTitleKey == null) {
					this._firstTitleKey = temp.getKey();
				} else {
					throw new RuntimeException("fightingGrowth/FightingGrowthTitle.csv，多于一个isFirst为true！");
				}
			}
		}
		return this.cfgCacheMap;
	}

}
