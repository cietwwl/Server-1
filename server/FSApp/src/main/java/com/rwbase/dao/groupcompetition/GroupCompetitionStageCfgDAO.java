package com.rwbase.dao.groupcompetition;

import java.util.Iterator;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

public class GroupCompetitionStageCfgDAO extends CfgCsvDao<GroupCompetitionStageCfg> {
	
	public static GroupCompetitionStageCfgDAO getInstance() {
		return SpringContextUtil.getBean(GroupCompetitionStageCfgDAO.class);
	}

	@Override
	protected Map<String, GroupCompetitionStageCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(GroupCompetitionConfigDir.DIR.getFullPath("CompetitionStage.csv"), GroupCompetitionStageCfg.class);
		for (Iterator<String> keyItr = cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			GroupCompetitionStageCfg cfg = cfgCacheMap.get(keyItr.next());
			String[] timeInfos = cfg.getStartTime().split(":");
			cfg.setStartTimeInfo(Integer.parseInt(timeInfos[0]), Integer.parseInt(timeInfos[1]));
			timeInfos = cfg.getEndTime().split(":");
			if(timeInfos.length > 0 && timeInfos[0].length() > 0) {
				cfg.setEndTimeInfo(Integer.parseInt(timeInfos[0]), Integer.parseInt(timeInfos[1]));
			}
		}
		return this.cfgCacheMap;
	}

}
