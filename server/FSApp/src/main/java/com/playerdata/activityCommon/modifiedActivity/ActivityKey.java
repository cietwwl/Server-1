package com.playerdata.activityCommon.modifiedActivity;

import com.playerdata.activityCommon.activityType.ActivityType;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rwbase.gameworld.GameWorldKey;

/**
 * 用于存储修改过的活动数据
 * 
 * <note>
 * 由于数据存储在game_world表，所有需要一个GameWorldKey
 * 但是为了方便管理活动类的存储，所以创建这个枚举管理活动类的key
 * </note>
 * @author aken
 *
 */
public enum ActivityKey {
	
	/**--	充值排行榜	--*/
	ACTIVITY_CHARGE_RANK(1, GameWorldKey.ACTIVITY_CHARGE_RANK, ActivityTypeFactory.ChargeRank),
	/**--	消费排行榜	--*/
	ACTIVITY_CONSUME_RANK(2, GameWorldKey.ACTIVITY_CONSUME_RANK, ActivityTypeFactory.ConsumeRank),
	/**--	每日充值	--*/
	ACTIVITY_DAILY_RECHARGE(3, GameWorldKey.ACTIVITY_DAILY_RECHARGE, ActivityTypeFactory.DailyRecharge),
	/**--	成长基金	--*/
	ACTIVITY_GROWTHFUND(4, GameWorldKey.ACTIVITY_GROWTHFUND, ActivityTypeFactory.GrowthFund),
	/**--	登陆奖励等基础活动	--*/
	ACTIVITY_COUNTTYPE(5, GameWorldKey.ACTIVITY_COUNTTYPE, ActivityTypeFactory.CountType),
	/**--	申公豹驾到	--*/
	ACTIVITY_EVILBAOARRIVE(6, GameWorldKey.ACTIVITY_EVILBAOARRIVE, ActivityTypeFactory.EvilBaoArrive),
	/**--	申公豹驾到	--*/
	ACTIVITY_RANK_TYPE(7, GameWorldKey.ACTIVITY_RANK_TYPE, ActivityTypeFactory.ActRankType),
	/**--	超值欢乐购	--*/
	ACTIVITY_DISCOUNT(8, GameWorldKey.ACTIVITY_DISCOUNT, ActivityTypeFactory.DailyDiscount),
	/**--	招财猫	--*/
	ACTIVITY_FORTUNECAT(9, GameWorldKey.ACTIVITY_FORTUNECAT, ActivityTypeFactory.FortuneCat),
	;
	
	@SuppressWarnings("rawtypes")
	ActivityKey(int key, GameWorldKey worldKey, ActivityType activityType) {
		this.key = key;
		this.worldKey = worldKey;
		this.activityType = activityType;
	}

	private int key;
	private GameWorldKey worldKey;
	@SuppressWarnings("rawtypes")
	private ActivityType activityType;

	public GameWorldKey getGameWorldKey() {
		return worldKey;
	}

	@SuppressWarnings("rawtypes")
	public ActivityType getActivityType() {
		return activityType;
	}
	
	public static ActivityKey getByKey(int key){
		for(ActivityKey act : ActivityKey.values()){
			if(act.key == key){
				return act;
			}
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static ActivityKey getByActType(ActivityType type){
		for(ActivityKey act : ActivityKey.values()){
			if(act.activityType.equals(type)){
				return act;
			}
		}
		return null;
	}
	
	/**
	 * 通过配置id找到属于的类型
	 * @param cfgId
	 * @return
	 */
	public static ActivityKey getByCfgId(int cfgId){
		for(ActivityKey act : ActivityKey.values()){
			if(act.getActivityType().getActivityMgr().isThisActivityIndex(cfgId)){
				return act;
			}
		}
		return null;
	}
}
