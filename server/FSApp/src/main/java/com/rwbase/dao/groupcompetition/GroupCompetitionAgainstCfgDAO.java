package com.rwbase.dao.groupcompetition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionAgainstCfg;

public class GroupCompetitionAgainstCfgDAO extends CfgCsvDao<GroupCompetitionAgainstCfg> {

	@Override
	protected Map<String, GroupCompetitionAgainstCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(GroupCompetitionConfigDir.DIR.getFullPath("CompetitionAgainst.csv"), GroupCompetitionAgainstCfg.class);
		String key;
		for(Iterator<String> keyItr = this.cfgCacheMap.keySet().iterator(); keyItr.hasNext(); ) {
			key = keyItr.next();
			GroupCompetitionAgainstCfg cfg = this.cfgCacheMap.get(key);
			String[] againstInfoStr = cfg.getAgainstInfo().split(";");
			IReadOnlyPair<Integer, Integer> tempPair;
			List<IReadOnlyPair<Integer, Integer>> list = new ArrayList<IReadOnlyPair<Integer,Integer>>(againstInfoStr.length);
			String[] singleAgainstInfo;
			for (int i = 0, length = againstInfoStr.length; i < length; i++) {
				singleAgainstInfo = againstInfoStr[i].split(",");
				tempPair = Pair.CreateReadonly(Integer.parseInt(singleAgainstInfo[0]), Integer.parseInt(singleAgainstInfo[1]));
				list.add(tempPair);
			}
			cfg.setAgainstInfoList(list);
		}
		return cfgCacheMap;
	}

}
