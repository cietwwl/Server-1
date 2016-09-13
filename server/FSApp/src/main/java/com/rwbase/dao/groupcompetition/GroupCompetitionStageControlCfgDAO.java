package com.rwbase.dao.groupcompetition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageControlCfg;

public class GroupCompetitionStageControlCfgDAO extends CfgCsvDao<GroupCompetitionStageControlCfg> {
	
	public static GroupCompetitionStageControlCfgDAO getInstance() {
		return SpringContextUtil.getBean(GroupCompetitionStageControlCfgDAO.class);
	}

	@Override
	protected Map<String, GroupCompetitionStageControlCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(GroupCompetitionConfigDir.DIR.getFullPath("CompetitionStage.csv"), GroupCompetitionStageControlCfg.class);
		for (Iterator<String> keyItr = this.cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			GroupCompetitionStageControlCfg cfg = this.cfgCacheMap.get(keyItr.next());
			String[] startTimeInfo = cfg.getStartTime().split(":");
			cfg.setStartTimeInfo(Pair.CreateReadonly(Integer.parseInt(startTimeInfo[0]), Integer.parseInt(startTimeInfo[1])));
			String[] stageDetail = cfg.getStageDetail().split(",");
			List<Integer> list = new ArrayList<Integer>();
			for(int i = 0; i < stageDetail.length; i++) {
				list.add(Integer.parseInt(stageDetail[i]));
			}
			cfg.setStageDetailList(list);
		}
		return this.cfgCacheMap;
	}
	
	public GroupCompetitionStageControlCfg getByType(int type) {
		GroupCompetitionStageControlCfg tempCfg;
		for(Iterator<String> keyItr = this.cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			tempCfg = this.cfgCacheMap.get(keyItr.next());
			if(tempCfg.getStartType() == type) {
				return tempCfg;
			}
		}
		return null;
	}

}
