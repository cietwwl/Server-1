package com.playerdata.activity.retrieve.userFeatures;

import com.playerdata.Player;
import com.playerdata.activity.retrieve.cfg.NormalRewardsCfg;
import com.playerdata.activity.retrieve.cfg.PerfectRewardsCfg;
import com.playerdata.activity.retrieve.cfg.RewardBackCfg;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;


public interface IUserFeatruesHandler {
	
	/**
	 * 
	 * @param userId
	 * @return 负责创建角色或隔天刷新时，将当天的功能对应可完成次数填入todaysubitem;创建角色时不能传入player等，都写系统默认值
	 */
	public RewardBackTodaySubItem doEvent();
	
	/**
	 * 
	 * @param todaySubItem
	 * @param userId
	 * @param dao
	 * @return 负责隔天刷新时，将旧的当天功能数据生成对应的新的活动找回数据；
	 * 首先要对其功能进行核实，是否激活； 
	 * 其次因为部分奖励和消耗是根据用户等级+vip变化的，需要此时根据自身条件对doevent生成的数据再次调整；
	 */
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem,Player player,CfgOpenLevelLimitDAO dao);

	/**
	 * 
	 * @param todaySubItem
	 * @param userId
	 * @param dao
	 * @return 
	 * 生成数据时，如果找回奖励-1是辅表的，需要用vip,level得到对应的辅表cfg，再用enum去匹配到对应的普通或完美奖励
	 * 生成数据时，如果找回奖励-2是不定量*单价的，理论需要再加表，目前只有体力如此，写死，使用subitem
	 * 生成数据时，如果再传入enum，则get时可以抽出，不过意义不大- -！
	 */
	public String getNorReward(NormalRewardsCfg cfg,RewardBackSubItem subItem);

	/**
	 * 
	 * @param todaySubItem
	 * @param userId
	 * @param dao
	 * @return 
	 * 
	 */
	public String getPerReward(PerfectRewardsCfg cfg,RewardBackSubItem subItem);
	
	/**
	 * 
	 * @param rewardCfg
	 * @param subItem
	 * @param mainCfg
	 * @return
	 * 生成消耗时，如果是-1的，则去子表根据等级和vip匹配，使用第一个参数；如果是-2的，则根据subitem的count匹配cfg的范围浮动生成
	 * 最好用两个方法区别处理0.0这里偷懒了放一起，实例的地方在分开处理
	 */
	public int getNorCost(NormalRewardsCfg rewardCfg,RewardBackSubItem subItem,RewardBackCfg mainCfg);
	
	public int getPerCost(PerfectRewardsCfg rewardCfg,RewardBackSubItem subItem,RewardBackCfg mainCfg);
	
	
}
