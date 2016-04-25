package com.rw.service.FresherActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;

/*
 * @author HC
 * @date 2016年4月14日 上午11:36:28
 * @Description 
 */
public class FrshActCheckTowerUseBox implements IFrshActCheckTask {

	@Override
	public FresherActivityCheckerResult doCheck(Player player, eActivityType activityType) {
		List<Integer> result = new ArrayList<Integer>();

		FresherActivityCheckerResult checkResult = new FresherActivityCheckerResult();
		Map<Integer, String> map = new HashMap<Integer, String>();

		List<FresherActivityItemIF> fresherActivityItems = player.getFresherActivityMgrIF().getFresherActivityItems(activityType);

		int useBoxCount = player.getBattleTowerMgr().getTableBattleTower().getHasUsedKeyCount();

		for (FresherActivityItemIF freActivityItem : fresherActivityItems) {
			if (!FresherActivityChecker.checkFresherActivity(freActivityItem)) {
				continue;
			}
			int cfgId = freActivityItem.getCfgId();
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
			String condition = fresherActivityCfg.getCondition();

			int boxCountCondition = Integer.parseInt(condition);

			if (useBoxCount >= boxCountCondition) {
				result.add(cfgId);
			} else {
				map.put(cfgId, String.valueOf(useBoxCount));
			}
		}
		checkResult.setCompleteList(result);
		checkResult.setCurrentProgress(map);
		return checkResult;
	}
}