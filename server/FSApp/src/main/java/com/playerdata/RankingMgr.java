package com.playerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.bm.arena.ArenaBM;
import com.bm.rank.ListRankingType;
import com.bm.rank.RankType;
import com.bm.rank.RankingEntityCopyer;
import com.bm.rank.arena.ArenaExtAttribute;
import com.bm.rank.arena.ArenaRankingComparable;
import com.bm.rank.arena.ArenaSettleComparable;
import com.bm.rank.arena.ArenaSettlement;
import com.bm.rank.fightingAll.FightingComparable;
import com.bm.rank.level.LevelComparable;
import com.log.GameLog;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntityOfRank;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.util.DateUtils;
import com.rw.netty.UserChannelMgr;
import com.rw.service.ranking.ERankingType;
import com.rwbase.common.enu.ECareer;
import com.rwbase.dao.ranking.CfgRankingDAO;
import com.rwbase.dao.ranking.TableRankingMgr;
import com.rwbase.dao.ranking.pojo.CfgRanking;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.ranking.pojo.RankingTeamData;
import com.rwbase.gameworld.GameWorld;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

/**
 * 排行榜数据管理类。
 * 
 * @author henry
 * @Modified-by henry
 * @version
 * 
 */
public class RankingMgr {

	private static RankingMgr m_instance = new RankingMgr();

	public static RankingMgr getInstance() {
		if (m_instance == null) {
			m_instance = new RankingMgr();
		}
		return m_instance;
	}

	public void onInitRankData() {
		resetUpdateState();
		arenaCalculate();
	}

	/**
	 * 对外提供重置每日排行榜的通知
	 */
	@SuppressWarnings("unchecked")
	public void resetUpdateState() {
		GameWorld world = GameWorldFactory.getGameWorld();
		String lastResetText = world.getAttribute(GameWorldKey.DAILY_RANKING_RESET);
		if (lastResetText != null && !lastResetText.isEmpty()) {
			long lastResetTime = Long.parseLong(lastResetText);
			if (!DateUtils.isResetTime(5, 0, 0, lastResetTime)) {
				return;
			}
		}

		// 第一次初始化的时候调用
		if (lastResetText == null || lastResetText.isEmpty()) {
			ArrayList<? extends ListRankingEntry<String, ArenaExtAttribute>> list = new ArrayList<ListRankingEntry<String, ArenaExtAttribute>>();
			list.addAll(RankingFactory.getSRanking(ListRankingType.WARRIOR_ARENA).getEntrysCopy());
			list.addAll(RankingFactory.getSRanking(ListRankingType.SWORDMAN_ARENA).getEntrysCopy());
			list.addAll(RankingFactory.getSRanking(ListRankingType.MAGICAN_ARENA).getEntrysCopy());
			list.addAll(RankingFactory.getSRanking(ListRankingType.PRIEST_ARENA).getEntrysCopy());

			Ranking<FightingComparable, RankingLevelData> fightingRanking = RankingFactory.getRanking(RankType.FIGHTING_ALL);
			Ranking<FightingComparable, RankingLevelData> fightingTeamRanking = RankingFactory.getRanking(RankType.TEAM_FIGHTING);
			Ranking<LevelComparable, RankingLevelData> levelRanking = RankingFactory.getRanking(RankType.LEVEL_ALL);
			for (ListRankingEntry<String, ArenaExtAttribute> m : list) {
				RankingLevelData levelData = createRankingLevelData(m);
				ArenaExtAttribute areanExt = m.getExtension();
				String key = m.getKey();
				FightingComparable fightingComparable = new FightingComparable();
				fightingComparable.setFighting(areanExt.getFighting());
				fightingRanking.addOrUpdateRankingEntry(key, fightingComparable, levelData);

				fightingComparable = new FightingComparable();
				fightingComparable.setFighting(areanExt.getFightingTeam());
				fightingTeamRanking.addOrUpdateRankingEntry(key, fightingComparable, levelData);
				LevelComparable lc = new LevelComparable();
				lc.setLevel(areanExt.getLevel());
				lc.setExp(0);
				levelRanking.addOrUpdateRankingEntry(key, lc, levelData);
			}
		}

		changeDailyData(RankType.FIGHTING_ALL, RankType.FIGHTING_ALL_DAILY);
		changeDailyData(RankType.TEAM_FIGHTING, RankType.TEAM_FIGHTING_DAILY);
		changeDailyData(RankType.LEVEL_ALL, RankType.LEVEL_ALL_DAILY);
		world.updateAttribute(GameWorldKey.DAILY_RANKING_RESET, String.valueOf(System.currentTimeMillis()));
	}

	public void arenaCalculate() {
		GameWorld world = GameWorldFactory.getGameWorld();
		String lastResetText = world.getAttribute(GameWorldKey.DAILY_RANKING_RESET);
		if (lastResetText != null && !lastResetText.isEmpty()) {
			long lastResetTime = Long.parseLong(lastResetText);
			if (!DateUtils.isResetTime(21, 0, 0, lastResetTime)) {
				return;
			}
		}
		long currentTime = System.currentTimeMillis();
		try {
			ArrayList<RankingEntityOfRank<ArenaSettleComparable, ArenaSettlement>> settletList = new ArrayList<RankingEntityOfRank<ArenaSettleComparable, ArenaSettlement>>();
			changeDailyData(ListRankingType.WARRIOR_ARENA, RankType.WARRIOR_ARENA_DAILY, settletList, currentTime);
			changeDailyData(ListRankingType.SWORDMAN_ARENA, RankType.SWORDMAN_ARENA_DAILY, settletList, currentTime);
			changeDailyData(ListRankingType.MAGICAN_ARENA, RankType.MAGICAN_ARENA_DAILY, settletList, currentTime);
			changeDailyData(ListRankingType.PRIEST_ARENA, RankType.PRIEST_ARENA_DAILY, settletList, currentTime);
			Ranking<ArenaSettleComparable, ArenaSettlement> settleRanking = RankingFactory.getRanking(RankType.ARENA_SETTLEMENT);
			settleRanking.clearAndInsert(settletList);
			rewardOnlinePlayers();
		} finally {
			world.updateAttribute(GameWorldKey.DAILY_RANKING_RESET, String.valueOf(System.currentTimeMillis()));
		}
	}

	private void rewardOnlinePlayers() {
		Ranking<ArenaSettleComparable, ArenaSettlement> settleRanking = RankingFactory.getRanking(RankType.ARENA_SETTLEMENT);
		ArenaBM arenaBM = ArenaBM.getInstance();
		Set<String> set = UserChannelMgr.getOnlinePlayerIdSet();
		for (String userId : set) {
			arenaBM.arenaDailyPrize(userId, settleRanking);
		}
	}

	/* 把实时排行榜数据拷贝到每日排行榜 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void changeDailyData(RankType ordinalType, RankType copyType) {
		Ranking ordinalRanking = RankingFactory.getRanking(ordinalType);
		Ranking copyRanking = RankingFactory.getRanking(copyType);
		List<? extends MomentRankingEntry> list = ordinalRanking.getReadOnlyRankingEntries();
		ArrayList<RankingEntityOfRank> copyList = new ArrayList<RankingEntityOfRank>();
		RankingEntityCopyer copyer = copyType.getEntityCopyer();
		// 暂时记log
		if (copyer == null) {
			// TODO 这里可以考虑使用默认深拷贝的BeanUtil复制
			GameLog.error("Ranking", "RankingMgr#changeDailyData()", "#getEntityCopyer()为null:" + ordinalType + "," + copyType, null);
		} else if (copyer != ordinalType.getEntityCopyer()) {
			GameLog.error("Ranking", "RankingMgr#changeDailyData()", "#getEntityCopyer()类型不一致:" + ordinalType + "," + copyType, null);
		}
		int size = list.size();
		for (int i = 1; i <= size; i++) {
			MomentRankingEntry entry = list.get(i - 1);
			Comparable comparable;
			Object ext;
			if (copyer != null) {
				comparable = copyer.copyComparable(entry.getComparable());
				ext = copyer.copyExtension(entry.getExtendedAttribute());
			} else {
				comparable = entry.getComparable();
				ext = entry.getExtendedAttribute();
			}
			RankingEntityOfRankImpl re = new RankingEntityOfRankImpl(i, comparable, entry.getKey(), ext);
			copyList.add(re);
		}
		copyRanking.clearAndInsert(copyList);
	}

	/* 把竞技场排行榜的数据拷贝到每日排行榜 */
	private void changeDailyData(ListRankingType ordinalType, RankType copyType, ArrayList<RankingEntityOfRank<ArenaSettleComparable, ArenaSettlement>> settletList, long currentTime) {
		ListRanking<String, ArenaExtAttribute> sranking = RankingFactory.getSRanking(ordinalType);
		List<? extends ListRankingEntry<String, ArenaExtAttribute>> list = sranking.getEntrysCopy();
		int size = list.size();
		int maxCapacity = copyType.getMaxCapacity();
		int career = ordinalType.getType();
		Ranking<ArenaRankingComparable, RankingLevelData> ranking = RankingFactory.getRanking(copyType);
		int total = Math.min(size, maxCapacity);
		ArrayList<RankingEntityOfRank<ArenaRankingComparable, RankingLevelData>> currentList = new ArrayList<RankingEntityOfRank<ArenaRankingComparable, RankingLevelData>>(total);

		for (int i = 1; i <= size; i++) {
			ListRankingEntry<String, ArenaExtAttribute> entry = list.get(i - 1);
			String key = entry.getKey();
			RankingLevelData levelData = createRankingLevelData(entry);
			int rank = ranking.getRanking(key);
			ArenaSettleComparable sc = new ArenaSettleComparable();
			sc.setRanking(rank);
			ArenaSettlement settlement = new ArenaSettlement();
			settlement.setCareer(career);
			settlement.setSettleMillis(currentTime);
			RankingEntityOfRankImpl<ArenaSettleComparable, ArenaSettlement> settleEntity = new RankingEntityOfRankImpl<ArenaSettleComparable, ArenaSettlement>(i, sc, key, settlement);
			settletList.add(settleEntity);
			if (size > total) {
				continue;
			}
			if (rank > 0) {
				levelData.setRankCount(Math.abs(rank - i));
			}

			levelData.setRankLevel(i);
			ArenaRankingComparable rankComparable = new ArenaRankingComparable();
			rankComparable.setRanking(entry.getRanking());
			RankingEntityOfRankImpl<ArenaRankingComparable, RankingLevelData> entity = new RankingEntityOfRankImpl<ArenaRankingComparable, RankingLevelData>(i, rankComparable, key, levelData);
			currentList.add(entity);
		}
		ranking.clearAndInsert(currentList);
	}

	/* 通过竞技场记录创建一个排行榜实体 */
	private RankingLevelData createRankingLevelData(ListRankingEntry<String, ArenaExtAttribute> entry) {
		ArenaExtAttribute areanExt = entry.getExtension();
		RankingLevelData levelData = new RankingLevelData();
		levelData.setUserId(entry.getKey());
		levelData.setLevel(areanExt.getLevel());
		levelData.setJob(areanExt.getCareer());
		levelData.setFightingAll(areanExt.getFighting());
		levelData.setFightingTeam(areanExt.getFightingTeam());
		levelData.setModelId(areanExt.getModelId());
		levelData.setSex(areanExt.getSex());
		levelData.setUserHead(areanExt.getHeadImage());
		levelData.setUserName(areanExt.getName());
		levelData.setArenaPlace(entry.getRanking());
		levelData.setRankLevel(entry.getRanking());
		return levelData;
	}

	/**
	 * 对外提供排行榜条目属性更新
	 * 
	 * @param p
	 */
	public void onPlayerChange(Player p) {
		updateCurrentInfo(p, RankType.FIGHTING_ALL);
		updateCurrentInfo(p, RankType.TEAM_FIGHTING);
		updateCurrentInfo(p, RankType.LEVEL_ALL);
		ArenaBM.getInstance().onPlayerChanged(p);
		// updateCurrentInfo(p, RankType.WARRIOR_ARENA);
		// updateCurrentInfo(p, RankType.SWORDMAN_ARENA);
		// updateCurrentInfo(p, RankType.MAGICAN_ARENA);
		// updateCurrentInfo(p, RankType.PRIEST_ARENA);
	}

	@SuppressWarnings("rawtypes")
	private void updateCurrentInfo(Player p, RankType type) {
		Ranking ranking = RankingFactory.getRanking(type);
		String userId = p.getUserId();
		RankingEntry entry = ranking.getRankingEntry(userId);
		if (entry != null) {
			RankingLevelData toData = (RankingLevelData) entry.getExtendedAttribute();
			toData.setUserName(p.getUserName());
			toData.setLevel(p.getLevel());
			toData.setExp(p.getExp());
			toData.setFightingAll(p.getHeroMgr().getFightingAll());
			toData.setFightingTeam(p.getHeroMgr().getFightingTeam());
			toData.setUserHead(p.getHeadImage());
			toData.setModelId(p.getModelId());
			toData.setJob(p.getCareer());
			toData.setSex(p.getSex());
			toData.setCareerLevel(p.getStarLevel());
			toData.setArenaPlace(ArenaBM.getInstance().getOtherArenaPlace(userId, p.getCareer()));
		}
	}

	/**
	 * 对外提供获取第一名的排行信息
	 * 
	 * @param type
	 * @return
	 */
	public RankingLevelData getFirstRankingData(ECareer type) {
		RankType rankingType;
		switch (type) {
		case Warrior:
			rankingType = RankType.WARRIOR_ARENA_DAILY;
			break;
		case SwordsMan:
			rankingType = RankType.SWORDMAN_ARENA_DAILY;
			break;
		case Magican:
			rankingType = RankType.MAGICAN_ARENA_DAILY;
			break;
		case Priest:
			rankingType = RankType.PRIEST_ARENA_DAILY;
			break;
		// TODO 这样做真的好吗
		default:
			rankingType = RankType.WARRIOR_ARENA_DAILY;
			break;
		}

		Ranking ranking = RankingFactory.getRanking(rankingType);
		if (ranking == null) {
			GameLog.error("ranking", "getFirstRankingData", "找不到指定竞技场类型：" + type);
			return null;
		}
		RankingEntry entry = ranking.getFirstEntry();
		if (entry == null) {
			return null;
		}
		String userId = (String) entry.getKey();
		Player p = (Player) PlayerMgr.getInstance().getReadOnlyPlayer(userId);
		if (p == null) {
			GameLog.error("ranking", "getFirstRankingData", "找不到玩家：" + userId);
			return null;
		}
		RankingLevelData toData = new RankingLevelData();
		toData.setUserId(userId);
		toData.setUserName(p.getUserName());
		toData.setLevel(p.getLevel());
		toData.setExp(p.getExp());
		toData.setFightingAll(p.getHeroMgr().getFightingAll());
		toData.setFightingTeam(p.getHeroMgr().getFightingTeam());
		toData.setUserHead(p.getHeadImage());
		toData.setModelId(p.getModelId());
		toData.setJob(p.getCareer());
		toData.setSex(p.getSex());
		toData.setCareerLevel(p.getStarLevel());
		toData.setArenaPlace(ArenaBM.getInstance().getOtherArenaPlace(userId, p.getCareer()));
		return toData;
	}

	/**
	 * 根据排行类型获取排行数据 上次排序
	 * 
	 * @param rankType
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<RankingLevelData> getRankList(RankType rankType) {
		Ranking ranking = RankingFactory.getRanking(rankType);
		if (ranking == null) {
			GameLog.error("ranking", "getRankList", "找不到排行榜类型：" + rankType);
			return Collections.EMPTY_LIST;
		}
		if (ranking.size() == 0) {
			return Collections.EMPTY_LIST;
		}
		CfgRanking cfg = CfgRankingDAO.getInstance().getRankingCf(getCfgId(rankType));
		int maxCount = cfg.getRankNum();
		EnumerateList<? extends MomentRankingEntry> enumerateList = ranking.getEntriesEnumeration(1, Math.min(maxCount, ranking.size()));
		ArrayList<RankingLevelData> list = new ArrayList<RankingLevelData>(enumerateList.size());
		while (enumerateList.hasMoreElements()) {
			list.add((RankingLevelData) enumerateList.nextElement().getEntry().getExtendedAttribute());
		}
		return list;
	}

	/** 根据类型、用户ID 获取排名 无数据返回0 0表示未入榜 */
	public int getRankLevel(RankType rankType, String userId) {
		Ranking ranking = RankingFactory.getRanking(rankType);
		if (ranking == null) {
			GameLog.error("ranking", "getRankLevel", "找不到排行榜类型：" + rankType);
			return 0;
		}
		int index = ranking.getRanking(userId);
		return index < 0 ? 0 : index;
	}

	/** 获取玩家的排名数据 上次排序 */
	public RankingLevelData getRankLevelData(RankType rankType, String userId) {
		Ranking ranking = RankingFactory.getRanking(rankType);
		if (ranking == null) {
			GameLog.error("ranking", "getRankLevelData", "找不到排行榜类型：" + rankType);
			return null;
		}
		RankingEntry entry = ranking.getRankingEntry(userId);
		if (entry == null) {
			return null;
		}
		return (RankingLevelData) entry.getExtendedAttribute();
	}

	/** 根据排行类型获取队伍列表 */
	public List<RankingTeamData> getTeamList(ERankingType rankType, String userId) {
		List<RankingTeamData> result = new ArrayList<RankingTeamData>();
		if (rankType.equals(ERankingType.TEAM_FIGHTING_ALL)) {
			result = TableRankingMgr.getInstance().getFiveTeamData(userId);
		} else if (rankType.equals(ERankingType.WARRIOR_DAY) || rankType.equals(ERankingType.SWORDMAN_DAY) || rankType.equals(ERankingType.MAGICAN_DAY) || rankType.equals(ERankingType.PRIEST_DAY)) {
			result = TableRankingMgr.getInstance().getArenaTeamData(userId, rankType);
		}
		return result;
	}

	/** 生成或改变新的一条数据 */
	private boolean changeNewData(Player pPlayer, ERankingType rankType) {
		if (pPlayer == null) {
			return false;
		}
		CfgRanking cfgRank = CfgRankingDAO.getInstance().getRankingCf(rankType.getValue());
		if (cfgRank == null || pPlayer.getLevel() < cfgRank.getLimitLevel()) {// 未到达开放等级返回
			return false;
		}
		return true;
	}

	/**
	 * 对外提供玩家战力发生变化的通知
	 * 
	 * @param player
	 */
	public void onHeroFightingChanged(Player player) {
		int teamFighting = player.getHeroMgr().getFightingTeam();
		int fighting = player.getHeroMgr().getFightingAll();
		String userId = player.getUserId();
		if (checkUpdateFighting(player, RankType.TEAM_FIGHTING, teamFighting, ERankingType.TEAM_FIGHTING_ALL)
				|| checkUpdateFighting(player, RankType.FIGHTING_ALL, fighting, ERankingType.FIGHTING_ALL)) {
			updateEntryFighting(RankType.LEVEL_ALL, fighting, teamFighting, userId);
			// TODO 这两个可以合并在updateFighting操作中
			updateEntryFighting(RankType.TEAM_FIGHTING, fighting, teamFighting, userId);
			updateEntryFighting(RankType.FIGHTING_ALL, fighting, teamFighting, userId);
			// 通知竞技场更新
			ArenaBM.getInstance().onPlayerChanged(player);
			// updateEntryFighting(RankType.WARRIOR_ARENA, fighting,
			// teamFighting, userId);
			// updateEntryFighting(RankType.SWORDMAN_ARENA, fighting,
			// teamFighting, userId);
			// updateEntryFighting(RankType.MAGICAN_ARENA, fighting,
			// teamFighting, userId);
			// updateEntryFighting(RankType.PRIEST_ARENA, fighting,
			// teamFighting, userId);
		}
	}

	/**
	 * 对外提供玩家等级或经验发生变化的通知
	 * 
	 * @param player
	 */
	public void onLevelOrExpChanged(Player player) {
		// 检查玩家是否达到进入等级排行榜的等级
		if (!changeNewData(player, ERankingType.LEVEL_ALL)) {
			return;
		}
		String userId = player.getUserId();
		Ranking<LevelComparable, RankingLevelData> ranking = RankingFactory.getRanking(RankType.LEVEL_ALL);
		RankingEntry<LevelComparable, RankingLevelData> entry = ranking.getRankingEntry(userId);
		int level = player.getLevel();
		long exp = player.getExp();
		if (entry != null) {
			LevelComparable oldComparable = entry.getComparable();
			if (oldComparable.getExp() == exp && oldComparable.getLevel() == level) {
				return;
			}
		}
		LevelComparable levelComparable = new LevelComparable();
		levelComparable.setExp(exp);
		levelComparable.setLevel(level);
		if (entry == null) {
			ranking.addOrUpdateRankingEntry(userId, levelComparable, player);
		} else {
			ranking.updateRankingEntry(entry, levelComparable);
		}

		updateLevelAndExp(RankType.LEVEL_ALL, level, exp, userId);
		updateLevelAndExp(RankType.TEAM_FIGHTING, level, exp, userId);
		updateLevelAndExp(RankType.FIGHTING_ALL, level, exp, userId);
		ArenaBM.getInstance().onPlayerChanged(player);
		// updateLevelAndExp(RankType.WARRIOR_ARENA, level, exp, userId);
		// updateLevelAndExp(RankType.SWORDMAN_ARENA, level, exp, userId);
		// updateLevelAndExp(RankType.MAGICAN_ARENA, level, exp, userId);
		// updateLevelAndExp(RankType.PRIEST_ARENA, level, exp, userId);
	}

	private boolean checkUpdateFighting(Player player, RankType type, int fighting, ERankingType checkOpenType) {
		if (!changeNewData(player, checkOpenType)) {
			return false;
		}
		String userId = player.getUserId();
		Ranking<FightingComparable, RankingLevelData> ranking = RankingFactory.getRanking(type);
		RankingEntry<FightingComparable, RankingLevelData> entry = ranking.getRankingEntry(userId);
		if (entry != null) {
			FightingComparable fightingComparable = entry.getComparable();
			if (fightingComparable.getFighting() == fighting) {
				return false;
			}
		}
		FightingComparable fightingComparable = new FightingComparable();
		fightingComparable.setFighting(fighting);
		if (entry == null) {
			ranking.addOrUpdateRankingEntry(userId, fightingComparable, player);
		} else {
			ranking.updateRankingEntry(entry, fightingComparable);
		}
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void updateEntryFighting(RankType type, int fighting, int teamFighting, String userId) {
		Ranking ranking = RankingFactory.getRanking(type);
		RankingEntry entry = ranking.getRankingEntry(userId);
		if (entry != null) {
			boolean changed = false;
			RankingLevelData toData = (RankingLevelData) entry.getExtendedAttribute();
			if (toData.getFightingTeam() != teamFighting) {
				toData.setFightingTeam(teamFighting);
				changed = true;
			}
			if (toData.getFightingAll() != fighting) {
				toData.setFightingAll(fighting);
				changed = true;
			}
			if (changed) {
				ranking.subimitUpdatedTask(entry);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void updateLevelAndExp(RankType type, int level, long exp, String userId) {
		Ranking ranking = RankingFactory.getRanking(type);
		RankingEntry entry = ranking.getRankingEntry(userId);
		if (entry != null) {
			boolean changed = false;
			RankingLevelData toData = (RankingLevelData) entry.getExtendedAttribute();
			if (toData.getLevel() != level) {
				toData.setLevel(level);
				changed = true;
			}
			if (toData.getExp() != exp) {
				toData.setExp(exp);
				changed = true;
			}
			if (changed) {
				ranking.subimitUpdatedTask(entry);
			}
		}
	}

	// 临时兼容配置的做法
	private int getCfgId(RankType type) {
		int id;
		switch (type) {
		case WARRIOR_ARENA_DAILY:
		case WARRIOR_ARENA:
			id = 101;
			break;
		case SWORDMAN_ARENA_DAILY:
		case SWORDMAN_ARENA:
			id = 102;
			break;
		case MAGICAN_ARENA_DAILY:
		case MAGICAN_ARENA:
			id = 103;
			break;
		case PRIEST_ARENA_DAILY:
		case PRIEST_ARENA:
			id = 104;
			break;
		case FIGHTING_ALL_DAILY:
			id = 201;
			break;
		case TEAM_FIGHTING_DAILY:
			id = 203;
			break;
		case LEVEL_ALL_DAILY:
			id = 301;
			break;
		default:
			id = 101;
			break;
		}
		return id;
	}

}

class RankingEntityOfRankImpl<C extends Comparable<C>, E> implements RankingEntityOfRank<C, E> {

	private final int ranking;
	private final C comparable;
	private final String key;
	private final E extendedAttribute;

	public RankingEntityOfRankImpl(int ranking, C comparable, String key, E extendedAttribute) {
		super();
		this.ranking = ranking;
		this.comparable = comparable;
		this.key = key;
		this.extendedAttribute = extendedAttribute;
	}

	@Override
	public int getRanking() {
		return this.ranking;
	}

	@Override
	public C getComparable() {
		return this.comparable;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public E getExtendedAttribute() {
		return this.extendedAttribute;
	}

}