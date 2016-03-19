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
 * 
 * @author lida
 *
 */
public class FrshActCheckArena implements IFrshActCheckTask{

	@Override
	public FresherActivityCheckerResult doCheck(Player player, eActivityType activityType) {
		List<Integer> result = new ArrayList<Integer>();
		// TODO Auto-generated method stub
		List<FresherActivityItemIF> fresherActivityItems = player.getFresherActivityMgrIF().getFresherActivityItems(activityType);
		String userId = player.getUserId();
		int career = player.getCareer();
		ListRankingEntry<String, ArenaExtAttribute> entry = ArenaBM.getInstance().getEntry(userId, career);
		int ranking = entry.getRanking();
		FresherActivityCheckerResult checkResult = new FresherActivityCheckerResult();
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		
		for (FresherActivityItemIF freActivityItem : fresherActivityItems) {
			if(!FresherActivityChecker.checkFresherActivity(freActivityItem)){
				continue;
			}
			int cfgId = freActivityItem.getCfgId();
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
			String condition = fresherActivityCfg.getCondition();
			map.put(cfgId, ranking);
			int rankCondition = Integer.parseInt(condition);
			
			if(ranking <= rankCondition){
				result.add(cfgId);
			}
		}
		checkResult.setCompleteList(result);
		return checkResult;
	}

}
