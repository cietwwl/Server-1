package com.rw.service.FresherActivity;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.enu.EHeroQuality;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.enu.eHeroStar;
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;

/**
 * 开服活动:伙伴升星
 * @author lida
 *
 */
public class FrshActCheckHeroStar implements IFrshActCheckTask{

	@Override
	public FresherActivityCheckerResult doCheck(Player player, eActivityType activityType) {
		// TODO Auto-generated method stub
		List<Integer> result = new ArrayList<Integer>();
		List<FresherActivityItemIF> fresherActivityItems = player.getFresherActivityMgrIF().getFresherActivityItems(activityType);
		
		FresherActivityCheckerResult checkResult = new FresherActivityCheckerResult();
		Map<Integer, String> map = new HashMap<Integer, String>();
		
		Map<Integer, Integer> StarMap = getHeroStarMap(player);
		
		for (FresherActivityItemIF freActivityItem : fresherActivityItems) {
			if(!FresherActivityChecker.checkFresherActivity(freActivityItem)){
				continue;
			}
			
			int cfgId = freActivityItem.getCfgId();
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
			String condition = fresherActivityCfg.getCondition();
			
			String[] conditions = condition.split(":");
			int num = Integer.parseInt(conditions[0]);
			int star = Integer.parseInt(conditions[1]);
			
			if(StarMap.containsKey(star)){
				int current = StarMap.get(star);
				if(current >= num){
					result.add(cfgId);
				}else{
					map.put(cfgId, String.valueOf(current));
				}
			}
		}
		
		checkResult.setCompleteList(result);
		checkResult.setCurrentProgress(map);
		return checkResult;
	}

	private Map<Integer, Integer> getHeroStarMap(Player player){
		Enumeration<Hero> herosEnumeration = player.getHeroMgr().getHerosEnumeration();
		Map<Integer, Integer> starMap = new HashMap<Integer, Integer>();
		eHeroStar[] starValues = eHeroStar.getAllValue();
		//统计当前英雄各品质的个数
		for(Enumeration e = herosEnumeration; herosEnumeration.hasMoreElements();){
			Hero hero = (Hero)e.nextElement();
			int starLevel = hero.getStarLevel();
			
			for (eHeroStar star : starValues) {
				if(starLevel >= star.getStar()){
					if(starMap.containsKey(star.getStar())){
						starMap.put(star.getStar(), starMap.get(star.getStar())+1);
					}else{
						starMap.put(star.getStar(), 1);
					}
					
				}
			}
			
		}
		return starMap;
	}

}
