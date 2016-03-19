package com.rw.service.FresherActivity;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.arena.ArenaBM;
import com.bm.rank.arena.ArenaExtAttribute;
import com.playerdata.Player;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;

/**
 * 开服活动:玩家在活动时间内达到指定等级即可获得奖励
 * @author lida
 *
 */
public class FrshActCheckPlayerLevel implements IFrshActCheckTask{

	@Override
	public FresherActivityCheckerResult doCheck(Player player, eActivityType activityType) {
		// TODO Auto-generated method stub
		List<Integer> result = new ArrayList<Integer>();
		int level = player.getLevel();
		
		FresherActivityCheckerResult checkResult = new FresherActivityCheckerResult();
		Map<Integer, String> map = new HashMap<Integer, String>();
		
		List<FresherActivityItemIF> fresherActivityItems = player.getFresherActivityMgrIF().getFresherActivityItems(activityType);
		
		for (FresherActivityItemIF freActivityItem : fresherActivityItems) {
			if(!FresherActivityChecker.checkFresherActivity(freActivityItem)){
				continue;
			}
			int cfgId = freActivityItem.getCfgId();
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
			String condition = fresherActivityCfg.getCondition();
			
			int levelCondition = Integer.parseInt(condition);
			
			if(level >= levelCondition){
				result.add(cfgId);
			}else{
				map.put(cfgId, String.valueOf(level));
			}
		}
		checkResult.setCompleteList(result);
		checkResult.setCurrentProgress(map);
		return checkResult;
	}

}
