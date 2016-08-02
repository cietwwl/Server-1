package com.rw.service.FresherActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.readonly.FresherActivityMgrIF;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;

/**
 * 开服活动：伙伴数量
 * @author lida
 *
 */
public class FrshActCheckHeroNum implements IFrshActCheckTask{

	@Override
	public FresherActivityCheckerResult doCheck(Player player, eActivityType activityType) {
		// TODO Auto-generated method stub
		List<Integer> result = new ArrayList<Integer>();
		// TODO Auto-generated method stub
		FresherActivityMgrIF fresherActivityMgrIF = player.getFresherActivityMgrIF();
		FresherActivityCheckerResult checkResult = new FresherActivityCheckerResult();
		if(fresherActivityMgrIF == null){
			return checkResult;
		}
		List<FresherActivityItemIF> fresherActivityItems = fresherActivityMgrIF.getFresherActivityItems(activityType);
		
		
		Map<Integer, String> map = new HashMap<Integer, String>();
		
//		int herosSize = player.getHeroMgr().getHerosSize();
		int herosSize = player.getHeroMgr().getHerosSize(player);
		
		for (FresherActivityItemIF freActivityItem : fresherActivityItems) {
			if(!FresherActivityChecker.checkFresherActivity(freActivityItem)){
				continue;
			}
			int cfgId = freActivityItem.getCfgId();
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
			String condition = fresherActivityCfg.getCondition();
			
			int numCondition = Integer.parseInt(condition);
			
			if(herosSize >= numCondition){
				result.add(cfgId);
			}else{
				map.put(cfgId, String.valueOf(herosSize));
			}
		}
		checkResult.setCompleteList(result);
		checkResult.setCurrentProgress(map);
		return checkResult;
	}

}
