package com.playerdata;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.rank.RankType;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.common.Weight;
import com.log.GameLog;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.CurAttrData;
import com.playerdata.common.PlayerEventListener;
import com.playerdata.team.TeamInfo;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.tower.TowerHandler.FloorState;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.anglearray.AngelArrayConst;
import com.rwbase.dao.anglearray.AngelArrayUtils;
import com.rwbase.dao.anglearray.pojo.AngleArrayMatchHelper;
import com.rwbase.dao.anglearray.pojo.cfg.AngleArrayMatchCfg;
import com.rwbase.dao.anglearray.pojo.cfg.dao.AngleArrayMatchCfgCsvDao;
import com.rwbase.dao.anglearray.pojo.db.AngelArrayEnemyInfoData;
import com.rwbase.dao.anglearray.pojo.db.AngelArrayFloorData;
import com.rwbase.dao.anglearray.pojo.db.AngelArrayTeamInfoData;
import com.rwbase.dao.anglearray.pojo.db.TableAngleArrayData;
import com.rwbase.dao.anglearray.pojo.db.dao.AngelArrayEnemyInfoDataHolder;
import com.rwbase.dao.anglearray.pojo.db.dao.AngelArrayFloorDataHolder;
import com.rwbase.dao.anglearray.pojo.db.dao.AngelArrayTeamInfoDataHolder;
import com.rwbase.dao.anglearray.pojo.db.dao.AngleArrayDataDao;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.tower.TowerAwardCfg;
import com.rwbase.dao.tower.TowerAwardCfgDAO;
import com.rwbase.dao.tower.TowerFirstAwardCfgDAO;
import com.rwbase.dao.tower.TowerGoodsCfg;
import com.rwbase.dao.tower.TowerGoodsCfgDAO;
import com.rwbase.dao.tower.pojo.TowerHeroChange;
import com.rwproto.ItemBagProtos.EItemTypeDef;

public class TowerMgr implements TowerMgrIF, PlayerEventListener {

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
	private AngelArrayFloorDataHolder angelArrayFloorDataHolder;// 万仙阵层数信息的Holder
	private AngelArrayEnemyInfoDataHolder angelArrayEnemyInfoDataHolder;// 万仙阵敌人血量变化信息记录
	private String userId;

	public void init(Player player) {
		userId = player.getUserId();
		angelArrayFloorDataHolder = new AngelArrayFloorDataHolder(userId);
		angelArrayEnemyInfoDataHolder = new AngelArrayEnemyInfoDataHolder(userId);
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
		if (!DateUtils.isResetTime(AngelArrayConst.RESET_TIME, 0, 0, angleArrayData.getResetTime())) {// 不需要重置
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
			int maxSize = AngelArrayConst.MAX_HERO_FIGHTING_SIZE + 1;// 包括主要角色在内的佣兵数据
			maxSize = maxSize > allHeros.size() ? allHeros.size() : maxSize;

			// 获取到佣兵要用于匹配的总战力
			for (int i = 0; i < maxSize; i++) {
				totalFighting += allHeros.get(i).getFighting();
			}
		}

		angleArrayData.setResetFighting(totalFighting);
	}

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
		if (needClearEnemy) {
			angelArrayFloorDataHolder.resetAllAngelArrayFloorData();
			angelArrayEnemyInfoDataHolder.resetAllAngelArrayEnemyInfoData();
		}

		List<String> allEnemyIdList = angelArrayFloorDataHolder.getEnemyUserIdList();
		AngleArrayMatchCfgCsvDao cfgDAO = AngleArrayMatchCfgCsvDao.getCfgDAO();
		int size = floor + AngelArrayConst.TOWER_UPDATE_NUM;

		AngelArrayTeamInfoDataHolder holder = AngelArrayTeamInfoDataHolder.getHolder();
		List<String> hasUserIdList = holder.getAllUserIdList();

		for (; floor < size; floor++) {
			AngleArrayMatchCfg matchCfg = cfgDAO.getMatchCfg(level, floor);
			if (matchCfg == null) {
				continue;
			}

			int minFighting = (int) (fighting * matchCfg.getMinFightingRatio());
			int maxFighting = (int) (fighting * matchCfg.getMaxFightingRatio());

			AngelArrayTeamInfoData angelArrayTeamInfo = holder.getAngelArrayTeamInfo(minFighting, maxFighting, allEnemyIdList);
			if (angelArrayTeamInfo == null || allEnemyIdList.contains(angelArrayTeamInfo.getId())) {
				angelArrayTeamInfo = AngleArrayMatchHelper.getMatchAngelArrayTeamInfo(userId, matchCfg.getLevel(), matchCfg.getMaxLevel(), minFighting, maxFighting, allEnemyIdList, hasUserIdList,
						matchCfg.getRobotId());
				holder.addAngelArrayTeamInfo(angelArrayTeamInfo);
			}

			TeamInfo teamInfo = angelArrayTeamInfo.getTeamInfo();
			if (teamInfo != null) {
				AngelArrayFloorData floorData = new AngelArrayFloorData();
				floorData.setUserId(userId);
				floorData.setFloor(floor);
				floorData.setId(AngelArrayUtils.getAngelArrayFloorDataId(userId, floor));
				floorData.setTeamInfo(teamInfo);

				angelArrayFloorDataHolder.addAngelArrayFloorData(floorData);

				GameLog.info("万仙阵匹配玩家", userId,
						String.format("万仙阵第[%s]层，己方匹配战力区间战力是[%s,%s]，匹配到的玩家Id是[%s]，匹配阵容战力是[%s]", floor, minFighting, maxFighting, teamInfo.getUuid(), teamInfo.getTeamFighting()), null);
			} else {
				GameLog.error("万仙阵匹配玩家", userId, String.format("万仙阵第[%s]层，匹配不到玩家阵容", floor));
			}
		}
	}

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

		boolean isInsert = false;// 是否是要插入数据库
		String userId = player.getUserId();
		String angelArrayFloorDataId = AngelArrayUtils.getAngelArrayFloorDataId(userId, floor);
		AngelArrayEnemyInfoData angelArrayEnemyInfoData = angelArrayEnemyInfoDataHolder.getAngelArrayEnemyInfoData(angelArrayFloorDataId);
		if (angelArrayEnemyInfoData == null) {
			angelArrayEnemyInfoData = new AngelArrayEnemyInfoData();
			angelArrayEnemyInfoData.setUserId(userId);
			angelArrayEnemyInfoData.setId(angelArrayFloorDataId);
			angelArrayEnemyInfoData.setFloor(floor);
			isInsert = true;
		}

		for (int i = 0, size = heroChangeList.size(); i < size; i++) {
			TowerHeroChange towerHeroChange = heroChangeList.get(i);
			if (towerHeroChange == null) {
				continue;
			}

			String heroId = towerHeroChange.getRoleId();
			CurAttrData heroAttrData = angelArrayEnemyInfoData.getHeroAttrData(heroId);
			if (heroAttrData == null) {
				heroAttrData = new CurAttrData();
			}

			heroAttrData.setId(heroId);
			heroAttrData.setCurLife(towerHeroChange.getReduceLife());
			heroAttrData.setCurEnergy(towerHeroChange.getReduceEnegy());
			angelArrayEnemyInfoData.updateHeroAttrData(heroId, heroAttrData);
		}

		if (isInsert) {
			angelArrayEnemyInfoDataHolder.addAngelArrayEnemyInfoData(angelArrayEnemyInfoData);
		} else {
			angelArrayEnemyInfoDataHolder.flush();
		}
	}

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

	/**
	 * 获取只读的层Id列表
	 * 
	 * @return
	 */
	public List<String> getEnemyInfoIdList() {
		return angelArrayFloorDataHolder.getReadOnlyKeyList();
	}

	/**
	 * 通过敌人信息的Id获取到floor
	 * 
	 * @param id
	 * @return
	 */
	public int getKey4FloorId(String id) {
		AngelArrayFloorData angelArrayFloorData = angelArrayFloorDataHolder.getAngelArrayFloorData(id);
		if (angelArrayFloorData == null) {
			return -1;
		}

		return angelArrayFloorData.getFloor();
	}

	/**
	 * 获取某层的敌人信息
	 * 
	 * @param id
	 * @return
	 */
	public ArmyInfo getEnemyArmyInfo(String id) {
		AngelArrayFloorData angelArrayFloorData = angelArrayFloorDataHolder.getAngelArrayFloorData(id);
		if (angelArrayFloorData == null) {
			return new ArmyInfo();
		}

		TeamInfo teamInfo = angelArrayFloorData.getTeamInfo();
		if (teamInfo == null) {
			return new ArmyInfo();
		}

		ArmyInfo armyInfo = AngelArrayTeamInfoHelper.parseTeamInfo2ArmyInfo(teamInfo);

		Map<String, CurAttrData> attrMap = null;
		AngelArrayEnemyInfoData angelArrayEnemyInfoData = angelArrayEnemyInfoDataHolder.getAngelArrayEnemyInfoData(id);
		if (angelArrayEnemyInfoData != null) {
			attrMap = angelArrayEnemyInfoData.getEnemyChangeMap();
		}

		// 按照客户端的旧规则，主角存的是RoleBaseInfo的Id字段
		ArmyHero player = armyInfo.getPlayer();
		String mainRoleId = player.getRoleBaseInfo().getId();

		// 按照客户端的旧规则，其他佣兵存的都是modelId
		List<ArmyHero> heroList = armyInfo.getHeroList();
		player.setCurAttrData(fillArmyHeroCurAttrData(mainRoleId, player.getAttrData(), attrMap));
		for (int i = 0, size = heroList.size(); i < size; i++) {
			ArmyHero armyHero = heroList.get(i);
			String heroId = String.valueOf(armyHero.getRoleBaseInfo().getModeId());
			armyHero.setCurAttrData(fillArmyHeroCurAttrData(heroId, armyHero.getAttrData(), attrMap));
		}

		return armyInfo;
	}

	/**
	 * 填充当前的剩余血量和能量
	 * 
	 * @param heroId
	 * @param attrData
	 * @param map
	 * @return
	 */
	private CurAttrData fillArmyHeroCurAttrData(String heroId, AttrData attrData, Map<String, CurAttrData> map) {
		CurAttrData curAttrData = map == null ? null : map.get(heroId);

		if (curAttrData == null) {
			curAttrData = new CurAttrData();
			curAttrData.setId(heroId);
			curAttrData.setCurLife(attrData.getLife());
			curAttrData.setCurEnergy(attrData.getEnergy());
		}

		return curAttrData;
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		// 创建万仙阵数据
		String userId = player.getUserId();
		TableAngleArrayData angleData = new TableAngleArrayData(userId);
		angleArrayDao.addOrUpdateAngleArrayData(angleData);
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
	}

	/**
	 * 更新万仙阵的数据
	 */
	public void saveAngleArrayData() {
		angleArrayDao.update(userId);
	}
}