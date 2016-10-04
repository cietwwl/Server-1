package com.playerdata.activity.retrieve.userFeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.retrieve.cfg.NormalRewardsCfg;
import com.playerdata.activity.retrieve.cfg.NormalRewardsCfgDAO;
import com.playerdata.activity.retrieve.cfg.PerfectRewardsCfg;
import com.playerdata.activity.retrieve.cfg.PerfectRewardsCfgDAO;
import com.playerdata.activity.retrieve.cfg.RewardBackCfg;
import com.playerdata.activity.retrieve.cfg.RewardBackCfgDAO;
import com.playerdata.activity.retrieve.data.ActivityRetrieveTypeHolder;
import com.playerdata.activity.retrieve.data.RewardBackItem;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.data.TeamBattleRecord;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesBattleTower;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesBreakfast;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesBuyPowerFive;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesBuyPowerFour;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesBuyPowerOne;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesBuyPowerThree;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesBuyPowerTwo;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesCeletriKunlun;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesCeletriPenglai;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesDinner;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesJbzd;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesLunch;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesLxsg;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesMagicSecret;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesPower;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesSupper;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesTeamBattle;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesTower;
import com.playerdata.activity.retrieve.userFeatures.userFeaturesType.UserFeatruesWorship;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copypve.pojo.CopyInfoCfg;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class UserFeatruesMgr {
	// 日常任务的早午晚编号
	private static final int dailyTaskBreakfast = 10002;
	private static final int dailyTasklunch = 10001;
	private static final int dailyTaskdinner = 10003;
	private static final int dailyTasksupper = 10004;
	// 逻辑得到copy,copylevel，最后得到copyinfo才能判断时哪种幻境
	public static final int jbzd = 1;
	public static final int lxsg = 2;
	public static final int celestial_kunlun = 3;
	public static final int celestial_penglai = 4;
	//活动表里的体力购买字段
	public static final int buyPowerOne = 1;
	public static final int buyPowerTwo = 4;
	public static final int buyPowerThree = 7;
	public static final int buyPowerFour = 10;
	public static final int buyPowerFive = 13;
	public static final int buyPowerLength = 3;
	
	

	private static UserFeatruesMgr instance = new UserFeatruesMgr();

	public static UserFeatruesMgr getInstance() {
		return instance;
	}

	private Map<UserFeaturesEnum, IUserFeatruesHandler> featruesHandlerMap = new HashMap<UserFeaturesEnum, IUserFeatruesHandler>();

	private UserFeatruesMgr() {
		featruesHandlerMap.put(UserFeaturesEnum.breakfast, new UserFeatruesBreakfast());
		featruesHandlerMap.put(UserFeaturesEnum.lunch, new UserFeatruesLunch());
		featruesHandlerMap.put(UserFeaturesEnum.dinner, new UserFeatruesDinner());
		featruesHandlerMap.put(UserFeaturesEnum.supper, new UserFeatruesSupper());
		featruesHandlerMap.put(UserFeaturesEnum.power, new UserFeatruesPower());
		featruesHandlerMap.put(UserFeaturesEnum.worship, new UserFeatruesWorship());
		featruesHandlerMap.put(UserFeaturesEnum.buyPowerOne, new UserFeatruesBuyPowerOne());
		featruesHandlerMap.put(UserFeaturesEnum.buyPowerTwo, new UserFeatruesBuyPowerTwo());
		featruesHandlerMap.put(UserFeaturesEnum.buyPowerThree, new UserFeatruesBuyPowerThree());
		featruesHandlerMap.put(UserFeaturesEnum.buyPowerFour, new UserFeatruesBuyPowerFour());
		featruesHandlerMap.put(UserFeaturesEnum.buyPowerFive, new UserFeatruesBuyPowerFive());
		featruesHandlerMap.put(UserFeaturesEnum.jbzd, new UserFeatruesJbzd());
		featruesHandlerMap.put(UserFeaturesEnum.lxsg, new UserFeatruesLxsg());
		featruesHandlerMap.put(UserFeaturesEnum.celestial_KunLunWonderLand, new UserFeatruesCeletriKunlun());
		featruesHandlerMap.put(UserFeaturesEnum.celestial_PengLaiIsland, new UserFeatruesCeletriPenglai());
		featruesHandlerMap.put(UserFeaturesEnum.teamBattle, new UserFeatruesTeamBattle());
		featruesHandlerMap.put(UserFeaturesEnum.tower, new UserFeatruesTower());
		featruesHandlerMap.put(UserFeaturesEnum.battleTower, new UserFeatruesBattleTower());
		featruesHandlerMap.put(UserFeaturesEnum.magicSecert, new UserFeatruesMagicSecret());
	}

	public List<RewardBackTodaySubItem> doCreat() {
		List<RewardBackTodaySubItem> subItemList = new ArrayList<RewardBackTodaySubItem>();
		for (Map.Entry<UserFeaturesEnum, IUserFeatruesHandler> entry : featruesHandlerMap.entrySet()) {
			RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
			subItem = creatSubItem( entry.getKey(), entry.getValue());
			subItemList.add(subItem);
		}
		return subItemList;
	}

	private RewardBackTodaySubItem creatSubItem(UserFeaturesEnum iEnum, IUserFeatruesHandler iUserFeatruesHandler) {
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		if (iUserFeatruesHandler != null) {
			subItem = iUserFeatruesHandler.doEvent();
		}
		return subItem;
	}

	// 玩家触发功能计数
	public void doFinish(Player player, UserFeaturesEnum iEnum) {
		doFinishFinally(player, iEnum, 1,0);
	}

	// 带参数的计数
	public void doFinishOfCount(Player player, UserFeaturesEnum iEnum, int count) {
		doFinishFinally(player, iEnum, count,0);
	}
	
	// 玩家触发功能计数
	public void doFinishOfHardId(Player player, UserFeaturesEnum iEnum, int hardId) {
		doFinishFinally(player, iEnum, 1, hardId);
	}
	
	/**
	 * 
	 * @param player
	 * @param iEnum
	 * @param count 普通的传入1；体力溢出传入count；心魔录传入id
	 * @param hardId
	 */
	private void doFinishFinally(Player player, UserFeaturesEnum iEnum, int count,int hardId) {
		ActivityRetrieveTypeHolder dataholder = ActivityRetrieveTypeHolder.getInstance();
		String userId = player.getUserId();
		RewardBackTodaySubItem subItem = null;
		RewardBackItem item = dataholder.getItem(userId);
		List<RewardBackTodaySubItem> todaySubitemList = item.getTodaySubitemList();

		for (RewardBackTodaySubItem temp : todaySubitemList) {
			if (StringUtils.equals(temp.getId(), iEnum.getId())) {
				subItem = temp;
				break;
			}
		}
		if (subItem == null) {
			// 当天没生成活动数据，但功能又跑进来了
			GameLog.error(LogModule.ComActivityRetrieve, userId, "当天没生成活动数据，但功能又跑进来了", null);
			return;
		}		
		int tmp = subItem.getCount()+ count;
		subItem.setCount(tmp);
		addTeamBattleMap(subItem,hardId);
		dataholder.updateItem(player, item);
	}
	
	/**唯独心魔录不能像其他功能直接增加次数，而是要将hardid和增加次数一并存入；心魔录可以看做是-1用等级vip匹配和-2分段计算的综合*/
	private void addTeamBattleMap(RewardBackTodaySubItem subItem, int hardId) {
		if(!StringUtils.equals(subItem.getId(), UserFeaturesEnum.teamBattle.getId())||hardId == 0){
			return;
		}
		HashMap<Integer, TeamBattleRecord> map = subItem.getTeambattleCountMap();
		map.get(hardId).setCount(map.get(hardId).getCount() + 1);	
	}

	/** 完成日常任务时判断下是否为早午晚餐等 */
	public void checkDailyTask(Player player, int id) {
		if (id == dailyTaskBreakfast) {
			doFinish(player, UserFeaturesEnum.breakfast);
		} else if (id == dailyTasklunch) {
			doFinish(player, UserFeaturesEnum.lunch);
		} else if (id == dailyTaskdinner) {
			doFinish(player, UserFeaturesEnum.dinner);
		} else if (id == dailyTasksupper) {
			doFinish(player, UserFeaturesEnum.supper);
		}
	}

	/* 完成生存幻境是判断是哪一种，蛋疼的一笔 */
	public void checkCelestial(Player player, CopyCfg copyCfg) {
		CopyInfoCfg infoCfg = player.getCopyDataMgr().getCopyInfoCfgByLevelID(String.valueOf(copyCfg.getLevelID()));
		if (infoCfg == null) {
			return;
		}
		if (infoCfg.getId() == celestial_kunlun) {
			doFinish(player, UserFeaturesEnum.celestial_KunLunWonderLand);
		} else if (infoCfg.getId() == celestial_penglai) {
			doFinish(player, UserFeaturesEnum.celestial_PengLaiIsland);
		}
	}

	/** 五档买体,传入买体成功后的当日已买次数;策划要改就加字段 */
	public void buyPower(Player player, int buyPowerCount) {
		if (buyPowerCount >= buyPowerOne && buyPowerCount < buyPowerTwo) {
			doFinish(player, UserFeaturesEnum.buyPowerOne);
		} else if (buyPowerCount >= buyPowerTwo && buyPowerCount < buyPowerThree) {
			doFinish(player, UserFeaturesEnum.buyPowerTwo);
		} else if (buyPowerCount >= buyPowerThree && buyPowerCount < buyPowerFour) {
			doFinish(player, UserFeaturesEnum.buyPowerThree);
		} else if (buyPowerCount >= buyPowerFour && buyPowerCount < buyPowerFive) {
			doFinish(player, UserFeaturesEnum.buyPowerFour);
		} else if (buyPowerCount >= buyPowerFive && buyPowerCount < buyPowerFive + buyPowerLength) {
			doFinish(player, UserFeaturesEnum.buyPowerFive);
		}
	}

	/**
	 * 
	 * @param userId
	 * @param subTodayItemList
	 * @return 负责隔天刷新时，将旧的当天功能数据生成对应的新的活动找回数据；
	 */
	public List<RewardBackSubItem> doFresh(Player player, List<RewardBackTodaySubItem> subTodayItemList) {
		RewardBackCfgDAO Dao = RewardBackCfgDAO.getInstance();
		NormalRewardsCfgDAO norDAO = NormalRewardsCfgDAO.getInstance();
		PerfectRewardsCfgDAO perDAO = PerfectRewardsCfgDAO.getInstance();
		List<RewardBackSubItem> subItemList = new ArrayList<RewardBackSubItem>();
		CfgOpenLevelLimitDAO limitDao = CfgOpenLevelLimitDAO.getInstance();
		int level = player.getLevel();
		int vip = player.getVip();
		for (RewardBackTodaySubItem todaySubItem : subTodayItemList) {
			UserFeaturesEnum iEnum = UserFeaturesEnum.getById(todaySubItem.getId());
			if (iEnum == null) {
				continue;
			}
			featruesHandlerMap.get(iEnum).doFresh(todaySubItem, player,limitDao);
			RewardBackSubItem subItem = new RewardBackSubItem();
			subItem = doFresh(iEnum, todaySubItem, level, vip, Dao, norDAO, perDAO);
			subItemList.add(subItem);
		}
		return subItemList;
	}

	/**
	 * @param todaySubItem
	 * @param userId
	 * @param dao
	 * @return 将一个子功能的昨日完成度数据转化为今日每日找回活动数据
	 */
	private RewardBackSubItem doFresh(UserFeaturesEnum ienum, RewardBackTodaySubItem todaySubItem, int level, int vip, RewardBackCfgDAO dao, NormalRewardsCfgDAO normalDao, PerfectRewardsCfgDAO perfectDao) {
		RewardBackCfg cfg = dao.getCfgById(todaySubItem.getId() + "");
		RewardBackSubItem subItem = new RewardBackSubItem();
		subItem.setId(Integer.parseInt(todaySubItem.getId()));
		subItem.setMaxCount(todaySubItem.getMaxCount());
		subItem.setCount(todaySubItem.getCount());
		subItem.setTeambattleCountMap(todaySubItem.getTeambattleCountMap());
		if(cfg == null){
			//创建时遍历枚举,没使用cfg；此处没有cfg
			return subItem;
		}
		subItem.setNormalType(cfg.getNormalCostType());
		subItem.setPerfectType(cfg.getPerfectCostType());

		setReward(ienum, subItem, cfg, level, vip, normalDao, perfectDao);
		setCost(ienum, subItem, cfg, level, vip, normalDao, perfectDao);
		subItem.setIstaken(false);
		return subItem;
	}

	
	/**
	 * 因为部分奖励和消耗是根据用户等级+vip变化的，需要此时根据自身获得对应的normal-perfect的cfg再获得对应的功能的相关数据；
	 * 设置子功能的奖励和消耗，但有些是主表没有的
	 * 
	 * @param subItem
	 * @param cfg
	 * @param level
	 * @param vip
	 * @param norDao
	 * @param perDao
	 */
	private void setReward(UserFeaturesEnum ienum, RewardBackSubItem subItem, RewardBackCfg cfg, int level, int vip, NormalRewardsCfgDAO norDao, PerfectRewardsCfgDAO perDao) {
		if (StringUtils.equals("-1", cfg.getNormalRewards())||StringUtils.equals("-2", cfg.getNormalRewards())) {
			setNorRewardByLevelAndVip(ienum,subItem, level, vip, norDao);
		} else {
			subItem.setNormalReward(cfg.getNormalRewards());
		}
		if (StringUtils.equals("-1", cfg.getPerfectRewards())||StringUtils.equals("-2", cfg.getPerfectRewards())) {
			setPerRewardByLevelAndVip(ienum,subItem, level, vip, perDao);
		} else {
			subItem.setPerfectReward(cfg.getPerfectRewards());
		}
	}

	/**
	 * 根据level vip 从perDao里拿完美找回的奖励
	 * 
	 * @param subItem
	 * @param level
	 * @param vip
	 * @param perDao
	 */
	private void setPerRewardByLevelAndVip(UserFeaturesEnum ienum, RewardBackSubItem subItem, int level, int vip, PerfectRewardsCfgDAO perDao) {
		HashMap<Integer, PerfectRewardsCfg> map = perDao.get_levelCfgMapping().get(level);
		if(map == null){
			return;
		}		
		PerfectRewardsCfg cfg = map.get(vip);
		if(cfg == null){
			return;
		}
		String reward = featruesHandlerMap.get(ienum).getPerReward(cfg,subItem);
		subItem.setPerfectReward(reward);	
	}	

	/**
	 * 根据level vip 从norDao里拿普通找回的奖励
	 * 
	 * @param subItem
	 * @param level
	 * @param vip
	 * @param norDao
	 */
	private void setNorRewardByLevelAndVip(UserFeaturesEnum ienum, RewardBackSubItem subItem, int level, int vip, NormalRewardsCfgDAO norDao) {
		HashMap<Integer, NormalRewardsCfg> map = norDao.get_levelCfgMapping().get(level);
		if(map == null){
			return;
		}		
		NormalRewardsCfg cfg = map.get(vip);
		if(cfg == null){
			return;
		}
		String reward = featruesHandlerMap.get(ienum).getNorReward(cfg,subItem);
		subItem.setNormalReward(reward);		
	}	
	
	private void setCost(UserFeaturesEnum ienum, RewardBackSubItem subItem, RewardBackCfg cfg, int level, int vip, NormalRewardsCfgDAO normalDao, PerfectRewardsCfgDAO perfectDao) {
		if (-1==cfg.getNormalCost()||cfg.getNormalCost() == -2) {
			setNorCostByLevelAndVip(ienum,subItem, level, vip, normalDao,cfg);
		} else {
			subItem.setNormalCost(cfg.getNormalCost());
		}
		if (-1==cfg.getPerfectCost()||cfg.getPerfectCost() == -2) {
			setPerCostByLevelAndVip(ienum,subItem, level, vip, perfectDao,cfg);
		} else {
			subItem.setPerfectCost(cfg.getPerfectCost());
		}
		
	}

	private void setPerCostByLevelAndVip(UserFeaturesEnum ienum, RewardBackSubItem subItem, int level, int vip, PerfectRewardsCfgDAO perfectDao,RewardBackCfg mainCfg) {
		HashMap<Integer, PerfectRewardsCfg> map = perfectDao.get_levelCfgMapping().get(level);
		if(map == null){
			return;
		}		
		PerfectRewardsCfg cfg = map.get(vip);
		if(cfg == null){
			return;
		}
		int cost = featruesHandlerMap.get(ienum).getPerCost(cfg,subItem,mainCfg);
		subItem.setPerfectCost(cost);
		
	}

	private void setNorCostByLevelAndVip(UserFeaturesEnum ienum, RewardBackSubItem subItem, int level, int vip, NormalRewardsCfgDAO normalDao,RewardBackCfg mainCfg) {
		HashMap<Integer, NormalRewardsCfg> map = normalDao.get_levelCfgMapping().get(level);
		if(map == null){
			return;
		}		
		NormalRewardsCfg cfg = map.get(vip);
		if(cfg == null){
			return;
		}
		int cost = featruesHandlerMap.get(ienum).getNorCost(cfg,subItem,mainCfg);
		subItem.setNormalCost(cost);
		
	}



}
