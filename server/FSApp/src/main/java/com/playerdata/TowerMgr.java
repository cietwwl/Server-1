package com.playerdata;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.rank.RankType;
import com.common.Weight;
import com.log.GameLog;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.CurAttrData;
import com.playerdata.common.PlayerEventListener;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.tower.TowerHandler.FloorState;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.anglearray.pojo.AngleArrayMatchHelper;
import com.rwbase.dao.anglearray.pojo.cfg.AngleArrayMatchCfg;
import com.rwbase.dao.anglearray.pojo.cfg.dao.AngleArrayMatchCfgCsvDao;
import com.rwbase.dao.anglearray.pojo.db.TableAngleArrayData;
import com.rwbase.dao.anglearray.pojo.db.TableAngleArrayFloorData;
import com.rwbase.dao.anglearray.pojo.db.dao.AngleArrayDataDao;
import com.rwbase.dao.anglearray.pojo.db.dao.AngleArrayFloorDataDao;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.tower.TowerAwardCfg;
import com.rwbase.dao.tower.TowerAwardCfgDAO;
import com.rwbase.dao.tower.TowerFirstAwardCfgDAO;
import com.rwbase.dao.tower.TowerGoodsCfg;
import com.rwbase.dao.tower.TowerGoodsCfgDAO;
import com.rwbase.dao.tower.pojo.TowerHeroChange;
import com.rwproto.ItemBagProtos.EItemTypeDef;

public class TowerMgr implements TowerMgrIF, PlayerEventListener {
	private static final int MAX_HERO_FIGHTING_SIZE = 4;// 最多查找4个佣兵的战力
	public static final int towerUpdateNum = 3;// 每次开放层
	public static final int totalTowerNum = 15;// 总塔层
	public static final int RESET_TIME = 21;// 晚上21点重置

	/**
	 * 获取英雄的排序方法
	 */
	private static final Comparator<Hero> comparator = new Comparator<Hero>() {

		@Override
		public int compare(Hero h1, Hero h2) {
			// 主角始终是排在最前边的
			int rType1 = h1.getRoleType().ordinal();
			int rType2 = h2.getRoleType().ordinal();
			if (rType1 > rType2) {
				return -1;
			} else if (rType1 < rType2) {
				return 1;
			}
			// 佣兵的个人战力，谁大谁在前
			int f1 = h1.getFighting();
			int f2 = h2.getFighting();
			return f2 - f1;
		}
	};

	private AngleArrayDataDao angleArrayDao = AngleArrayDataDao.getDao();
	private AngleArrayFloorDataDao angleArrayFloorDao = AngleArrayFloorDataDao.getDao();
	private String userId;

	public void init(Player player) {
		userId = player.getUserId();
		// System.err.println("万仙阵Init-----" + userId);
		// // 检测万仙阵数据
		// TableAngleArrayData angleData = angleArrayDao.getAngleArrayDataByKey(userId);
		// if (angleData == null) {
		// angleData = new TableAngleArrayData(userId);
		// angleArrayDao.addOrUpdateAngleArrayData(angleData);
		// }
		//
		// // 检测万仙阵层数据
		// TableAngleArrayFloorData floorData = angleArrayFloorDao.get(userId);
		// if (floorData == null) {
		// floorData = new TableAngleArrayFloorData(userId);
		// angleArrayFloorDao.update(floorData);
		// }
	}

	/**
	 * 获取万仙阵数据
	 * 
	 * @return
	 */
	public TableAngleArrayData getAngleArrayData() {
		return angleArrayDao.getAngleArrayDataByKey(userId);
	}

	/**
	 * 获取万仙阵的层数据
	 * 
	 * @return
	 */
	public TableAngleArrayFloorData getAngleArrayFloorData() {
		return angleArrayFloorDao.get(userId);
	}

	/**
	 * 重置个人万仙阵匹配的数据
	 * 
	 * @param player
	 * @param now
	 */
	public void checkAndResetMatchData(Player player) {
		// TODO HC 临时放这里检测一下数据是否创建成功
		notifyPlayerLogin(player);
		TableAngleArrayData angleArrayData = getAngleArrayData();
		if (angleArrayData == null) {
			return;
		}

		if (angleArrayData.getCurFloorState() == -1) {// 还不能打开界面
			return;
		}

		checkAndResetMatchData(player, angleArrayData);
		saveAngleArrayData();
	}

	/**
	 * 检查数据
	 * 
	 * @param player
	 * @param angleArrayData
	 */
	private void checkAndResetMatchData(Player player, TableAngleArrayData angleArrayData) {
		if (!DateUtils.isResetTime(RESET_TIME, 0, 0, angleArrayData.getResetTime())) {// 不需要重置
			return;
		}

		angleArrayData.setResetTime(System.currentTimeMillis());// 设置重置个人数据的时间
		// 从昨日竞技排行榜拿数据
		angleArrayData.setResetLevel(player.getLevel());// 竞技阵容出现的最高等级
		angleArrayData.setResetRankIndex(0);// 竞技排名
		// 从昨日榜中获取自己的战力
		Ranking rank = RankingFactory.getRanking(RankType.FIGHTING_ALL_DAILY);
		// 获取到佣兵要用于匹配的总战力
		int totalFighting = -1;
		if (rank != null) {
			RankingEntry rankingEntry = rank.getRankingEntry(player.getUserId());
			if (rankingEntry != null) {
				RankingLevelData att = (RankingLevelData) rankingEntry.getExtendedAttribute();
				if (att != null) {
					totalFighting = att.getFightingTeam();
				}
			}
		}

		if (totalFighting == -1) {
			List<Hero> allHeros = player.getHeroMgr().getAllHeros(comparator);

			// 要看一下总共要获取多少个佣兵的战力
			int maxSize = MAX_HERO_FIGHTING_SIZE + 1;// 包括主要角色在内的佣兵数据
			maxSize = maxSize > allHeros.size() ? allHeros.size() : maxSize;

			// 获取到佣兵要用于匹配的总战力
			for (int i = 0; i < maxSize; i++) {
				totalFighting += allHeros.get(i).getFighting();
			}
		}

		angleArrayData.setResetFighting(totalFighting);
	}

	// /**
	// * 重置个人的万仙阵数据
	// *
	// * @param isInit 是否是初始化
	// */
	// public void resetAngleArrayData(boolean isInit) {
	// TableAngleArrayData angleData = getAngleArrayData();
	// if (angleData == null) {
	// return;
	// }
	//
	// TableAngleArrayFloorData floorData = getAngleArrayFloorData();
	// if (floorData == null) {
	// return;
	// }
	// // ////// 重置个人数据
	// if (!isInit) {
	// angleData.setResetTimes(angleData.getResetTimes() + 1);// 设置已经使用的重置次数
	// }
	//
	// angleData.resetHeroChange();// 重置角色血量记录
	// angleData.setCurFloor(0);
	// angleData.setCurFloorState(FloorState.UN_PASS.ordinal());// 设置为未攻打状态
	// angleArrayDao.addOrUpdateAngleArrayData(angleData);
	//
	// // ////// 重置前三关的关卡数据
	// // 清空万仙阵缓存的关卡数据
	// floorData.clearAllEnemyInfo();
	// updateOpenFloorInfo(floorData, angleData.getUserId(), angleData.getResetLevel(), angleData.getResetFighting(), angleData.getCurFloor());
	// }

	/**
	 * 重置数据
	 * 
	 * @param angleData
	 * @param isInit
	 */
	public void resetAngleArrayData(Player player, boolean isInit) {
		TableAngleArrayData angleData = getAngleArrayData();
		if (angleData == null) {
			return;
		}

		if (angleData.getCurFloorState() == -1) {
			checkAndResetMatchData(player, angleData);
		}

		// ////// 重置个人数据
		if (!isInit) {
			angleData.setResetTimes(angleData.getResetTimes() + 1);// 设置已经使用的重置次数
		}

		angleData.resetHeroChange();// 重置角色血量记录
		angleData.setCurFloor(0);
		angleData.setCurFloorState(FloorState.UN_PASS.ordinal());// 设置为未攻打状态
		saveAngleArrayData();

		// 更新万仙阵的层信息
		updateAngleArrayFloorData(userId, angleData.getResetLevel(), angleData.getResetFighting(), angleData.getCurFloor(), true);
	}

	/**
	 * 更新帮派数据
	 * 
	 * @param userId
	 * @param level
	 * @param fighting
	 * @param floor
	 * @param needClearEnemy
	 */
	public void updateAngleArrayFloorData(String userId, int level, int fighting, int floor, boolean needClearEnemy) {
		TableAngleArrayFloorData floorData = getAngleArrayFloorData();
		if (floorData == null) {
			return;
		}

		if (needClearEnemy) {
			floorData.clearAllEnemyInfo();
		}

		List<String> allEnemyIdList = floorData.getAllEnemyIdList();
		AngleArrayMatchCfgCsvDao cfgDAO = AngleArrayMatchCfgCsvDao.getCfgDAO();
		int size = floor + towerUpdateNum;
		for (; floor < size; floor++) {
			AngleArrayMatchCfg matchCfg = cfgDAO.getMatchCfg(level, floor);
			if (matchCfg == null) {
				continue;
			}

			int minFighting = (int) (fighting * matchCfg.getMinFightingRatio());
			int maxFighting = (int) (fighting * matchCfg.getMaxFightingRatio());

			ArmyInfo armyInfo = AngleArrayMatchHelper.getMatchArmyInfo(userId, matchCfg.getLevel(), matchCfg.getMaxLevel(), minFighting, maxFighting, allEnemyIdList);
			if (armyInfo == null) {
				armyInfo = AngleArrayMatchHelper.getRobotArmyInfo(matchCfg.getRobotId());
			}

			if (armyInfo != null) {
				floorData.putNewEnemyInfo(floor, armyInfo);
			}
		}
		saveAngleArrayFloorData();
	}

	// /**
	// * 刷新开启层的信息
	// *
	// * @param floorData
	// * @param level
	// * @param fighting
	// * @param floor 开始层的索引
	// */
	// public void updateOpenFloorInfo(TableAngleArrayFloorData floorData, String userId, int level, int fighting, int floor) {
	// AngleArrayMatchCfgCsvDao cfgDAO = AngleArrayMatchCfgCsvDao.getCfgDAO();
	// int size = floor + towerUpdateNum;
	// for (; floor < size; floor++) {
	// AngleArrayMatchCfg matchCfg = cfgDAO.getMatchCfg(level, floor + 1);
	// if (matchCfg == null) {
	// continue;
	// }
	//
	// int minFighting = (int) (fighting * matchCfg.getMaxFightingRatio());
	// int maxFighting = (int) (fighting * matchCfg.getMaxFightingRatio());
	//
	// ArmyInfo armyInfo = AngleArrayMatchHelper.getMatchArmyInfo(userId, level, minFighting, maxFighting);
	// if (armyInfo == null) {
	// armyInfo = AngleArrayMatchHelper.getRobotArmyInfo(matchCfg.getRobotId());
	// }
	//
	// floorData.putNewEnemyInfo(floor, armyInfo);
	// }
	// angleArrayFloorDao.update(floorData);
	// }

	public void addTowerNum(int num) {
	}

	/**
	 * 更新角色的属性修改
	 * 
	 * @param heroChangeList
	 */
	public void updateHeroChange(List<TowerHeroChange> heroChangeList) {
		if (heroChangeList == null || heroChangeList.isEmpty()) {
			return;
		}

		TableAngleArrayData angleData = getAngleArrayData();
		if (angleData == null) {
			return;
		}

		for (int i = 0, size = heroChangeList.size(); i < size; i++) {
			TowerHeroChange heroChange = heroChangeList.get(i);
			angleData.updateHeroChange(heroChange.getRoleId(), heroChange);
		}

		saveAngleArrayData();
	}

	/**
	 * 保存敌人的血量信息
	 * 
	 * @param floor
	 * @param heroChangeList
	 */
	public void updateEnemyChange(Player player, int floor, List<TowerHeroChange> heroChangeList) {
		if (heroChangeList == null || heroChangeList.isEmpty()) {
			return;
		}

		TableAngleArrayFloorData floorData = getAngleArrayFloorData();
		if (floorData == null) {
			return;
		}

		ArmyInfo enemyinfo = floorData.getEnemyInfo(floor);// 获取当前层敌人数据
		if (enemyinfo == null) {
			return;
		}

		CurAttrData curAttrData = enemyinfo.getPlayer().getCurAttrData();
		TowerHeroChange heroChange = heroChangeList.get(0);
		if (curAttrData == null) {
			curAttrData = new CurAttrData();
			enemyinfo.getPlayer().setCurAttrData(curAttrData);
		}

		curAttrData.setId(enemyinfo.getPlayer().getRoleBaseInfo().getId());
		curAttrData.setCurLife(heroChange.getReduceLife());
		curAttrData.setCurEnergy(heroChange.getReduceEnegy());
		heroChangeList.remove(0);

		// 敌人佣兵的信息修改
		for (int i = 0, size = heroChangeList.size(); i < size; i++) {// 0 主角 1~n佣兵
			heroChange = heroChangeList.get(i);
			ArmyHero hero = getHeroTableById(enemyinfo, heroChange.getRoleId());
			if (hero == null) {
				GameLog.error("万仙阵", player.getUserId(), "没有找到改变的敌人数据 id=" + heroChange.getRoleId());
				continue;
			}

			CurAttrData heroAttrData = hero.getCurAttrData();
			if (heroAttrData == null) {
				heroAttrData = new CurAttrData();
				hero.setCurAttrData(heroAttrData);
			}

			heroAttrData.setId(hero.getRoleBaseInfo().getId());
			heroAttrData.setCurLife((int) heroChange.getReduceLife());
			heroAttrData.setCurEnergy((int) heroChange.getReduceEnegy());
		}

		saveAngleArrayFloorData();
	}

	/**
	 * 获取佣兵的信息
	 * 
	 * @param towerEnemyInfo
	 * @param heroId
	 * @return
	 */
	private ArmyHero getHeroTableById(ArmyInfo towerEnemyInfo, String heroId) {
		List<ArmyHero> heroList = towerEnemyInfo.getHeroList();
		for (ArmyHero data : heroList) {
			String heroModerId = String.valueOf(data.getRoleBaseInfo().getModeId());
			if (heroModerId.equals(heroId)) {
				return data;
			}
		}
		return null;
	}

	// /**
	// * 获取所有的敌人信息
	// *
	// * @return
	// */
	// public Enumeration<ArmyInfo> getEnemyEnumeration() {
	// return getAngleArrayFloorData().getEnemyEnumeration();
	// }
	//
	// /**
	// * 获取敌人信息
	// *
	// * @param floor
	// * @return
	// */
	// public ArmyInfo getEnemy(int floor) {
	// return getAngleArrayFloorData().getEnemyInfo(floor);
	// }

	/**
	 * 5点重置数据
	 */
	public void resetDataInNewDay() {
		TableAngleArrayData angleArrayData = getAngleArrayData();
		if (angleArrayData == null) {
			return;
		}

		angleArrayData.setResetTimes(0);
		angleArrayDao.update(userId);
	}

	/**
	 * 获取奖励的信息
	 * 
	 * @param floor
	 * @return
	 */
	public String getAwardByFloor(Player player, int floor) {
		TableAngleArrayData angleArrayData = getAngleArrayData();
		if (angleArrayData == null) {
			return "";
		}

		int maxFloor = angleArrayData.getMaxFloor();
		StringBuilder firstReward = new StringBuilder();// 首次
		StringBuilder dropReward = new StringBuilder();// 掉落
		if (floor > maxFloor) {
			firstReward.append(TowerFirstAwardCfgDAO.getInstance().GetGooldListStr(String.valueOf(floor + 1)));
		}

		TowerAwardCfg awardCfg = TowerAwardCfgDAO.getLevelTowerCfgByFloor(angleArrayData.getResetLevel(), floor);
		if (awardCfg != null) {
			// 过关奖励
			int gold = awardCfg.gold;
			int towerCoin = awardCfg.towerCoin;
			if (gold > 0) {
				dropReward.append(eSpecialItemId.Coin.getValue()).append("_").append(gold).append(",");
			}
			if (towerCoin > 0) {
				dropReward.append(eSpecialItemId.BraveCoin.getValue()).append("_").append(towerCoin).append(",");
			}

			List<TowerGoodsCfg> formatList = TowerGoodsCfgDAO.getInstance().getCfgsByFormatId(awardCfg.formatId);
			if (formatList != null && !formatList.isEmpty()) {
				int size = formatList.size();
				Map<Integer, Integer> proMap = new HashMap<Integer, Integer>(size);
				for (int i = 0; i < size; i++) {
					proMap.put(i, formatList.get(i).getWeight());
				}

				Weight<Integer> weight = new Weight<Integer>(proMap);
				Integer index = weight.getRanResult();
				if (index != null) {
					TowerGoodsCfg goodCfg = formatList.get(index);
					int leastNum = goodCfg.getLeastNum();// 最小数量
					int maxNum = goodCfg.getMaxNum();// 最大数量
					int num = leastNum + (int) Math.random() * (maxNum - leastNum + 1);
					if (num > 0) {
						dropReward.append(goodCfg.getItemId()).append("_").append(num).append(",");
					}
				}
			}
		}

		if (firstReward.length() > 0) {// 有第一次奖励
			dropReward.append(firstReward.toString());
		} else {// 没有首次掉落
			int dropLen = dropReward.length();
			dropReward.replace(dropLen - 1, dropLen, "");
		}

		if (dropReward.length() > 0) {
			addGoods(player, dropReward.toString().split(","));// 发送奖励
		}

		return dropReward.toString();
	}

	/**
	 * 发送奖励
	 * 
	 * @param goodsStr
	 */
	private void addGoods(Player player, String[] goodsStr) {
		for (int i = 0; i < goodsStr.length; i++) {
			String[] goodList = goodsStr[i].split("_");
			int templateId = Integer.valueOf(goodList[0]);
			int num = Integer.valueOf(goodList[1]);
			EItemTypeDef eItemType = ItemCfgHelper.getItemType(templateId);
			if (eItemType == EItemTypeDef.HeroItem) {// 是添加英雄物品
				player.getHeroMgr().addHero(String.valueOf(templateId));
				player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "得到英雄id=" + templateId);
			} else {
				player.getItemBagMgr().addItem(templateId, num);
			}
		}
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		// 创建万仙阵数据
		String userId = player.getUserId();
		System.err.println("万仙阵----" + userId);
		TableAngleArrayData angleData = new TableAngleArrayData(userId);
		angleArrayDao.addOrUpdateAngleArrayData(angleData);

		// 创建万仙阵的层数据
		TableAngleArrayFloorData floorData = new TableAngleArrayFloorData(userId);
		angleArrayFloorDao.update(floorData);
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		// 检测万仙阵数据
		String userId = player.getUserId();
		TableAngleArrayData angleData = angleArrayDao.getAngleArrayDataByKey(userId);
		if (angleData == null) {
			angleData = new TableAngleArrayData(userId);
			angleArrayDao.addOrUpdateAngleArrayData(angleData);
		}

		// 检测万仙阵层数据
		TableAngleArrayFloorData floorData = angleArrayFloorDao.get(userId);
		if (floorData == null) {
			floorData = new TableAngleArrayFloorData(userId);
			angleArrayFloorDao.update(floorData);
		}
	}

	/**
	 * 更新万仙阵的数据
	 */
	public void saveAngleArrayData() {
		angleArrayDao.update(userId);
	}

	/**
	 * 更新万仙阵层信息
	 */
	public void saveAngleArrayFloorData() {
		angleArrayFloorDao.update(userId);
	}
	// static class Test {
	// private int id;
	// private String name;
	//
	// public Test(int id, String name) {
	// this.id = id;
	// this.name = name;
	// }
	// }
	//
	// static class Cache {
	// Map<Integer, Test> testCache;
	//
	// public Cache() {
	// testCache = new HashMap<Integer, Test>();
	// for (int i = 0; i < 10; i++) {
	// testCache.put(i, new Test(i, "HC" + i));
	// }
	// }
	//
	// public Test get(int id) {
	// return testCache.get(id);
	// }
	// }
	//
	// static class TestMgr {
	// final int id;
	//
	// public TestMgr(int id) {
	// this.id = id;
	// }
	//
	// public Test getTest() {
	// return TowerMgr.CahceFactory.getCache().get(id);
	// }
	//
	// public void tUpdate() {
	// Test test = getTest();
	// test.name = "zzzzzz";
	// }
	// }
	//
	// static class CahceFactory {
	// static Cache cache = new Cache();
	//
	// public static Cache getCache() {
	// return cache;
	// }
	// }
	//
	// public static void main(String[] args) {
	// TestMgr TM = new TestMgr(1);
	// Test test = TM.getTest();
	// System.err.println(test.name);
	// TM.tUpdate();
	// System.err.println(test.name);
	// System.err.println(TM.getTest().name);
	// }
}