package com.rw.service.FresherActivity;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;

public class FrshActCheckTower implements IFrshActCheckTask{

	@Override
	public FresherActivityCheckerResult doCheck(Player player, eActivityType activityType) {
		// TODO Auto-generated method stub
		List<Integer> result = new ArrayList<Integer>();
		
		FresherActivityCheckerResult checkResult = new FresherActivityCheckerResult();
		Map<Integer, String> map = new HashMap<Integer, String>();
		
		List<FresherActivityItemIF> fresherActivityItems = player.getFresherActivityMgrIF().getFresherActivityItems(activityType);
		
		int highestFloor = player.getBattleTowerMgr().getTableBattleTower().getHighestFloor();
		
		for (FresherActivityItemIF freActivityItem : fresherActivityItems) {
			if(!FresherActivityChecker.checkFresherActivity(freActivityItem)){
				continue;
			}
			int cfgId = freActivityItem.getCfgId();
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
			String condition = fresherActivityCfg.getCondition();
			
			int floorCondition = Integer.parseInt(condition);
			
			if(highestFloor >= floorCondition){
				result.add(cfgId);
			}else{
				map.put(cfgId, String.valueOf(highestFloor));
			}
		}
		checkResult.setCompleteList(result);
		checkResult.setCurrentProgress(map);
		return checkResult;
	}

}
