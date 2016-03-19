package com.rw.service.FresherActivity;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.readonly.CopyLevelRecordIF;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;

public class FrshActCheckCopyLevel implements IFrshActCheckTask {

	@Override
	public FresherActivityCheckerResult doCheck(Player player, eActivityType activityType) {
		// TODO Auto-generated method stub
		List<Integer> result = new ArrayList<Integer>();
		List<FresherActivityItemIF> fresherActivityItems = player
				.getFresherActivityMgrIF()
				.getFresherActivityItems(activityType);
		
		FresherActivityCheckerResult checkResult = new FresherActivityCheckerResult();
		Map<Integer, String> map = new HashMap<Integer, String>();

		for (FresherActivityItemIF freActivityItem : fresherActivityItems) {
			if(!FresherActivityChecker.checkFresherActivity(freActivityItem)){
				continue;
			}

			int cfgId = freActivityItem.getCfgId();
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao
					.getInstance().getFresherActivityCfg(cfgId);
			String condition = fresherActivityCfg.getCondition();

			int levelId = Integer.parseInt(condition);

			CopyLevelRecordIF levelRecord = player.getCopyRecordMgr()
					.getLevelRecord(levelId);
			
			if (levelRecord != null && levelRecord.getPassStar() > 0) {
				result.add(cfgId);
			}
		}
		checkResult.setCompleteList(result);
		return checkResult;
	}

}
