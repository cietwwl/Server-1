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
import com.rwbase.dao.fresherActivity.FresherActivityCfgDao;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;
import com.rwproto.HeroServiceProtos.eHeroResultType;

/**
 * 开服活动 伙伴进阶
 * @author lida
 *
 */
public class FrshActCheckHeroGrade implements IFrshActCheckTask{

	@Override
	public FresherActivityCheckerResult doCheck(Player player, eActivityType activityType) {
		// TODO Auto-generated method stub
		List<Integer> result = new ArrayList<Integer>();
		List<FresherActivityItemIF> fresherActivityItems = player.getFresherActivityMgrIF().getFresherActivityItems(activityType);
		
		FresherActivityCheckerResult checkResult = new FresherActivityCheckerResult();
		Map<Integer, String> map = new HashMap<Integer, String>();
		
		Map<Integer, Integer> QualityMap = getHeroQualityMap(player);
		
		for (FresherActivityItemIF freActivityItem : fresherActivityItems) {
			if(!FresherActivityChecker.checkFresherActivity(freActivityItem)){
				continue;
			}
			
			int cfgId = freActivityItem.getCfgId();
			
			FresherActivityCfg fresherActivityCfg = FresherActivityCfgDao.getInstance().getFresherActivityCfg(cfgId);
			String condition = fresherActivityCfg.getCondition();
			
			String[] conditions = condition.split(":");
			int num = Integer.parseInt(conditions[0]);
			int quality = Integer.parseInt(conditions[1]);
			
			if(QualityMap.containsKey(quality)){
				int current = QualityMap.get(quality);
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
	
	private Map<Integer, Integer> getHeroQualityMap(Player player){
		Enumeration<Hero> herosEnumeration = player.getHeroMgr().getHerosEnumeration();
		Map<Integer, Integer> qualityMap = new HashMap<Integer, Integer>();
		EHeroQuality[] qualityValue = EHeroQuality.getAllValue();
		//统计当前英雄各品质的个数
		for (Enumeration e = herosEnumeration; herosEnumeration
				.hasMoreElements();) {
			Hero hero = (Hero) e.nextElement();
			int heroQuality = hero.GetHeroQuality();
			
			for (EHeroQuality eHeroQuality : qualityValue) {
				if(heroQuality >= eHeroQuality.ordinal()){
					if(qualityMap.containsKey(eHeroQuality.ordinal())){
						qualityMap.put(eHeroQuality.ordinal(), qualityMap.get(eHeroQuality.ordinal())+1);
					}else{
						qualityMap.put(eHeroQuality.ordinal(), 1);
					}
				}
			}
			
		}
		return qualityMap;
	}

}
