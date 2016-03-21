package com.rw.service.FresherActivity;

import java.util.HashMap;

import com.playerdata.Player;
import com.rw.service.FresherActivity.Achieve.FrshActAchieveFinalReward;
import com.rw.service.FresherActivity.Achieve.FrshActAchieveNormalReward;
import com.rw.service.FresherActivity.Achieve.IFrshActAchieveRewardHandler;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityFinalRewardCfg;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemHolder;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;

public class FresherActivityChecker {
	
	private final static HashMap<eActivityType, IFrshActCheckTask> FresherActivityMap = new HashMap<eActivityType, IFrshActCheckTask>();
	private final static HashMap<eActivityType, IFrshActAchieveRewardHandler> FrshActRewardMap = new HashMap<eActivityType, IFrshActAchieveRewardHandler>();
	
	public static void init(){
		FresherActivityMap.put(eActivityType.A_PlayerLv,new FrshActCheckPlayerLevel());
		FresherActivityMap.put(eActivityType.A_NormalCopyLv, new FrshActCheckCopyLevel());
		FresherActivityMap.put(eActivityType.A_EliteCopyLv, new FrshActCheckCopyLevel());
		FresherActivityMap.put(eActivityType.A_HeroGrade, new FrshActCheckHeroGrade());
		FresherActivityMap.put(eActivityType.A_HeroStar, new FrshActCheckHeroStar());
		FresherActivityMap.put(eActivityType.A_HeroNum, new FrshActCheckHeroNum());
		FresherActivityMap.put(eActivityType.A_ArenaRank, new FrshActCheckArena());
		FresherActivityMap.put(eActivityType.A_MagicLv, new FrshActCheckMagic());
		//暂时屏蔽
//		FresherActivityMap.put(eActivityType.A_CollectionType, new FrshActCheckCollection());
//		FresherActivityMap.put(eActivityType.A_CollectionLevel, new FrshActCheckCollection());
		FresherActivityMap.put(eActivityType.A_Tower, new FrshActCheckTower());
		
		FrshActAchieveNormalReward achieveNormalReward = new FrshActAchieveNormalReward();
		FrshActRewardMap.put(eActivityType.A_PlayerLv,achieveNormalReward);
		FrshActRewardMap.put(eActivityType.A_NormalCopyLv, achieveNormalReward);
		FrshActRewardMap.put(eActivityType.A_EliteCopyLv, achieveNormalReward);
		FrshActRewardMap.put(eActivityType.A_HeroGrade, achieveNormalReward);
		FrshActRewardMap.put(eActivityType.A_HeroStar, achieveNormalReward);
		FrshActRewardMap.put(eActivityType.A_HeroNum, achieveNormalReward);
		FrshActRewardMap.put(eActivityType.A_ArenaRank, achieveNormalReward);
		FrshActRewardMap.put(eActivityType.A_MagicLv, achieveNormalReward);
		//暂时屏蔽
//		FrshActRewardMap.put(eActivityType.A_CollectionType, achieveNormalReward);
//		FrshActRewardMap.put(eActivityType.A_CollectionLevel, achieveNormalReward);
		FrshActRewardMap.put(eActivityType.A_Tower, achieveNormalReward);
		//TODO HC @Notify 终极奖励的领取处理和正常的是不一个处理，正常用的是FrshActAchieveNormalReward，终级是：FrshActAchieveFinalReward
		FrshActRewardMap.put(eActivityType.A_Final, new FrshActAchieveFinalReward());
	}
	
	/**
	 * 检查
	 * @param player
	 * @param activityType
	 */
	public FresherActivityCheckerResult checkActivityCondition(Player player, eActivityType activityType){
		IFrshActCheckTask checkTask = FresherActivityMap.get(activityType);
		if(checkTask == null){
			return null;
		}
		return checkTask.doCheck(player, activityType);
	}
	
	public String achieveActivityReward(Player player, int cfgId, eActivityType activityType, FresherActivityItemHolder holder){
		IFrshActAchieveRewardHandler handler = FrshActRewardMap.get(activityType);
		return handler.achieveFresherActivityReward(player, cfgId, holder);
	} 
	
	/**
	 * 判断开服活动是否需要检测
	 * @param fresherActivityItem
	 * @return
	 */
	public static boolean checkFresherActivity(FresherActivityItemIF fresherActivityItem){
		if(fresherActivityItem.isFinish() || fresherActivityItem.isClosed() 
				|| fresherActivityItem.getEndTime() < System.currentTimeMillis()
				|| fresherActivityItem.getStartTime() > System.currentTimeMillis()){
			return false;
		}else{
			return true;
		}
	}
}
