package com.rw.service.FresherActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.arena.ArenaBM;
import com.playerdata.Player;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;

/**
 * 检测竞技场挑战次数的活动
 * @author lida
 *
 */
public class FrshActCheckArenaChallengeTime implements IFrshActCheckTask{

	@Override
	public FresherActivityCheckerResult doCheck(Player player,
			eActivityType activityType) {
		List<Integer> result = new ArrayList<Integer>();
		// TODO Auto-generated method stub
		List<FresherActivityItemIF> fresherActivityItems = player.getFresherActivityMgrIF().getFresherActivityItems(activityType);
		String userId = player.getUserId();
		TableArenaData arenaData = ArenaBM.getInstance().getArenaData(userId);
		int challengeTime = arenaData.getChallengeTime();
		FresherActivityCheckerResult checkResult = new FresherActivityCheckerResult();
		Map<Integer, String> map = new HashMap<Integer, String>();
		for (FresherActivityItemIF freActivityItem : fresherActivityItems) {
			if(!FresherActivityChecker.checkFresherActivity(freActivityItem)){
				continue;
			}
			int cfgId = freActivityItem.getCfgId();
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
			String condition = fresherActivityCfg.getCondition();
			int conditionValue = Integer.parseInt(condition);
			map.put(cfgId, String.valueOf(challengeTime));
			if(challengeTime >= conditionValue){
				result.add(cfgId);
			}
		}
		checkResult.setCompleteList(result);
		checkResult.setCurrentProgress(map);
		return checkResult;
	}

}