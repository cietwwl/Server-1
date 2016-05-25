package com.rw.service.PeakArena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.bm.arena.ArenaConstant;
import com.bm.rank.RankType;
import com.bm.rank.peakArena.PeakArenaExtAttribute;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.fsutil.common.SegmentList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.service.PeakArena.datamodel.PeakRecordInfo;
import com.rw.service.PeakArena.datamodel.TablePeakArenaData;
import com.rw.service.PeakArena.datamodel.TablePeakArenaDataDAO;
import com.rw.service.PeakArena.datamodel.TeamData;
import com.rwbase.common.attrdata.TableAttr;
import com.rwbase.dao.arena.ArenaInfoCfgDAO;
import com.rwbase.dao.arena.pojo.ArenaInfoCfg;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.skill.pojo.TableSkill;
import com.rwbase.dao.user.readonly.TableUserIF;

public class PeakArenaBM {

	private static PeakArenaBM instance;
	private TablePeakArenaDataDAO tablePeakArenaDataDAO = TablePeakArenaDataDAO.getInstance();
	private static int RANDOM_COUNT = 11; // 期望最少的随机数量
	private static int INCREMENT = 1000; // 期望积分差
	private static int SCORE_INC = 100; // 超出期望后的积分增量
	private static int FIGHTING_INTERVAL = 1000; // 期望战力差
	private static int LEVEL_INTERVAL = 3; // 期望等级差
	private static int RESULT_COUNT = 3; // 随机后的结果人数
	private static long MILLIS_PER_HOUR = TimeUnit.HOURS.toMillis(1);

	private PeakArenaBM() {
	}

	public static PeakArenaBM getInstance() {
		if (instance == null) {
			instance = new PeakArenaBM();
		}
		return instance;
	}

	public TablePeakArenaData getOrAddPeakArenaData(Player player) {
		return getOrAddPeakArenaData(player, null);
	}

	public TablePeakArenaData getOrAddPeakArenaData(Player player, TempRankingEntry temp) {
		TableUserIF tableUser = player.getTableUser();
		String userId = tableUser.getUserId();
		TablePeakArenaData data = tablePeakArenaDataDAO.get(userId);
		if (data != null) {
			Ranking<Integer, PeakArenaExtAttribute> ranking = RankingFactory.getRanking(RankType.PEAK_ARENA);
			RankingEntry<Integer, PeakArenaExtAttribute> entry = ranking.getRankingEntry(userId);
			if (entry == null) {
				int lastScore = data.getLastScore();
				if (lastScore < 0) {
					lastScore = 0;
				}
				entry = ranking.addOrUpdateRankingEntry(data.getUserId(), lastScore, player);
			}
			if (temp != null && entry != null) {
				temp.setRanking(ranking.getRanking(userId));
			}
			return data;
		}
		if (player.getLevel() < ArenaConstant.PEAK_ARENA_OPEN_LEVEL) {
			return null;
		}
		// 在排行榜创建记录
		//TableUserOtherIF tableUserOther = player.getTableUserOther();
		Ranking<Integer, PeakArenaExtAttribute> ranking = RankingFactory.getRanking(RankType.PEAK_ARENA);
		RankingEntry<Integer, PeakArenaExtAttribute> entry = ranking.addOrUpdateRankingEntry(userId, 0, player);
		int place;
		if (entry == null) {
			place = ranking.getMaxCapacity();
		} else {
			place = ranking.getRanking(userId);
		}
		if (temp != null) {
			temp.setRanking(place);
		}
		data = new TablePeakArenaData();
		data.setUserId(userId);

		data.setCareer(player.getCareer());

		data.setMaxPlace(place);
		ArenaInfoCfg infoCfg = ArenaInfoCfgDAO.getInstance().getPeakArenaInfo();
		data.setRemainCount(infoCfg.getCount());
		data.setLastGainCurrencyTime(System.currentTimeMillis());
		// data.setFighting(tableUserOther.getFighting());

		data.setHeadImage(tableUser.getHeadImageWithDefault());
		data.setLevel(player.getLevel());
		data.setName(tableUser.getUserName());
		data.setTempleteId(player.getTemplateId());

		HashMap<Integer, TeamData> teamMap = new HashMap<Integer, TeamData>();
		TeamData team;
		ItemData magic = player.getMagic();
		for (int i = 1; i <= 3; i++) {
			team = new TeamData();
			team.setTeamId(i);
			if (magic == null) {
				team.setMagicId(0);
				team.setMagicLevel(0);
			} else {
				team.setMagicId(magic.getModelId());
				team.setMagicLevel(magic.getMagicLevel());
			}
			team.setHeros(new ArrayList<RoleBaseInfo>());
			team.setHeroAtrrs(new ArrayList<TableAttr>());
			team.setHeroSkills(new ArrayList<TableSkill>());
			teamMap.put(i, team);
		}
		data.setTeamMap(teamMap);
		data.setRecordList(new ArrayList<PeakRecordInfo>());
		tablePeakArenaDataDAO.update(data);
		return data;
	}

	public TablePeakArenaData getPeakArenaData(String userId) {
		return tablePeakArenaDataDAO.get(userId);
	}

	// 增加颠覆竞技场数据
	public TablePeakArenaData addPeakArenaData(Player player) {
		if (player.getLevel() < ArenaConstant.PEAK_ARENA_OPEN_LEVEL) {
			return null;
		}
		// 在排行榜创建记录
		TableUserIF tableUser = player.getTableUser();
		//TableUserOtherIF tableUserOther = player.getTableUserOther();
		String userId = tableUser.getUserId();
		Ranking<Integer, PeakArenaExtAttribute> ranking = RankingFactory.getRanking(RankType.PEAK_ARENA);
		RankingEntry<Integer, PeakArenaExtAttribute> entry = ranking.addOrUpdateRankingEntry(userId, 0, player);
		int place;
		if (entry == null) {
			place = ranking.getMaxCapacity();
		} else {
			place = ranking.getRanking(userId);
		}
		TablePeakArenaData data = new TablePeakArenaData();
		data.setUserId(userId);

		data.setCareer(player.getCareer());

		data.setMaxPlace(place);
		ArenaInfoCfg infoCfg = ArenaInfoCfgDAO.getInstance().getPeakArenaInfo();
		data.setRemainCount(infoCfg.getCount());
		data.setLastGainCurrencyTime(System.currentTimeMillis());
		// data.setFighting(tableUserOther.getFighting());

		data.setHeadImage(tableUser.getHeadImageWithDefault());
		data.setLevel(player.getLevel());
		data.setName(tableUser.getUserName());
		data.setTempleteId(player.getTemplateId());

		HashMap<Integer, TeamData> teamMap = new HashMap<Integer, TeamData>();
		ItemData magic = player.getMagic();
		for (int i = 1; i <= 3; i++) {
			TeamData team = new TeamData();
			team.setTeamId(i);
			if (magic == null) {
				team.setMagicId(0);
				team.setMagicLevel(0);
			} else {
				team.setMagicId(magic.getModelId());
				team.setMagicLevel(magic.getMagicLevel());
			}
			team.setHeros(new ArrayList<RoleBaseInfo>());
			team.setHeroAtrrs(new ArrayList<TableAttr>());
			team.setHeroSkills(new ArrayList<TableSkill>());
			teamMap.put(i, team);
		}
		data.setTeamMap(teamMap);
		data.setRecordList(new ArrayList<PeakRecordInfo>());
		tablePeakArenaDataDAO.update(data);
		return data;
	}

	// 玩家筛选
	@SuppressWarnings("unchecked")
	public List<MomentRankingEntry<Integer, PeakArenaExtAttribute>> SelectPeakArenaInfos(TablePeakArenaData data, Player player) {
		Ranking<Integer, PeakArenaExtAttribute> ranking = RankingFactory.getRanking(RankType.PEAK_ARENA);
		RankingEntry<Integer, PeakArenaExtAttribute> entry = ranking.getRankingEntry(data.getUserId());
		if (entry == null) {
			entry = ranking.addOrUpdateRankingEntry(data.getUserId(), data.getLastScore(), player);
		}
		String userId = data.getUserId();
		int score = (entry == null ? data.getLastScore() : entry.getComparable());
		int minScore = score - INCREMENT;
		int maxScore = score + INCREMENT;
		SegmentList<MomentRankingEntry<Integer, PeakArenaExtAttribute>> enumberateList;
		{
			SegmentList<? extends MomentRankingEntry<Integer, PeakArenaExtAttribute>> tmp = ranking.getSegmentList(minScore, maxScore);
			enumberateList = (SegmentList<MomentRankingEntry<Integer, PeakArenaExtAttribute>>) tmp;
		}
		
		int refSize = enumberateList.getRefSize();
		if (refSize == 0) {
			return Collections.emptyList();
		}

		int maxSize = enumberateList.getMaxSize();
		int maxIndex = maxSize - 1;
		int startIndex = enumberateList.getRefStartIndex();
		int endIndex = enumberateList.getRefEndIndex();
		if (refSize < RANDOM_COUNT) {
			outter: for (; startIndex > 0 || endIndex < maxIndex;) {
				minScore -= SCORE_INC;
				for (; startIndex > 0;) {
					MomentRankingEntry<Integer, PeakArenaExtAttribute> start = enumberateList.get(startIndex - 1);
					if (start.getEntry().getComparable() >= minScore) {
						startIndex--;
						refSize++;
						if (refSize >= RANDOM_COUNT) {
							break outter;
						}
					} else {
						break;
					}
				}

				maxScore += SCORE_INC;
				for (; endIndex < maxIndex;) {
					MomentRankingEntry<Integer, PeakArenaExtAttribute> end = enumberateList.get(endIndex + 1);
					if (end.getEntry().getComparable() <= maxScore) {
						endIndex++;
						refSize++;
						if (refSize >= RANDOM_COUNT) {
							break outter;
						}
					} else {
						break;
					}
				}
			}
		}

		int level = data.getLevel();
		PeakArenaScoreLevel scoreLv = PeakArenaScoreLevel.getSocre(score);
		int fighting = data.getFighting();
		List<MomentRankingEntry<Integer, PeakArenaExtAttribute>> randomList = new ArrayList<MomentRankingEntry<Integer, PeakArenaExtAttribute>>();
		for (int i = startIndex; i <= endIndex; i++) {
			MomentRankingEntry<Integer, PeakArenaExtAttribute> rankingEntry = enumberateList.get(i);
			PeakArenaExtAttribute info = rankingEntry.getEntry().getExtendedAttribute();
			// 等级差在3级以内
			if (Math.abs(info.getLevel() - level) > LEVEL_INTERVAL) {
				continue;
			}
			// 积分段在同一段
			if (PeakArenaScoreLevel.getSocre(rankingEntry.getEntry().getComparable()) != scoreLv) {
				continue;
			}
			// 战力差在1000以内
			int absFighting = info.getFighting() - fighting;
			if (Math.abs(absFighting) > FIGHTING_INTERVAL) {
				continue;
			}
			randomList.add(rankingEntry);
		}

		int randomSize = randomList.size();
		if (randomSize < RANDOM_COUNT) {
			// 数量少的时候用clear
			randomList = enumberateList.getSemgentCopy(startIndex, endIndex);
			Collections.sort(randomList, new PeakAreanComparator(level, scoreLv.getLevel(), fighting));
			randomSize = RANDOM_COUNT;
		}

		// TODO 这里迟点专门优化
		Collections.shuffle(randomList);
		int count = 0;
		ArrayList<MomentRankingEntry<Integer, PeakArenaExtAttribute>> result = new ArrayList<MomentRankingEntry<Integer, PeakArenaExtAttribute>>();
		for (int i = randomList.size(); --i >= 0;) {
			MomentRankingEntry<Integer, PeakArenaExtAttribute> entry_ = randomList.get(count++);
			if (!entry_.getEntry().getKey().equals(userId)) {
				result.add(entry_);
			}
			if (result.size() == RESULT_COUNT) {
				break;
			}
		}
		return result;
	}

	private class PeakAreanComparator implements Comparator<MomentRankingEntry<Integer, PeakArenaExtAttribute>> {

		private final int level;
		private final int scoreLevel;
		private final int fighting;

		public PeakAreanComparator(int level, int scoreLevel, int fighting) {
			super();
			this.level = level;
			this.scoreLevel = scoreLevel;
			this.fighting = fighting;
		}

		@Override
		public int compare(MomentRankingEntry<Integer, PeakArenaExtAttribute> o1, MomentRankingEntry<Integer, PeakArenaExtAttribute> o2) {
			int o1Level = Math.abs(o1.getEntry().getExtendedAttribute().getLevel() - level);
			int o2Level = Math.abs(o2.getEntry().getExtendedAttribute().getLevel() - level);

			int lvInterval = o1Level - o2Level;
			if (lvInterval != 0) {
				return lvInterval;
			}

			int o1Lv = Math.abs(PeakArenaScoreLevel.getSocre(o1.getEntry().getComparable()).getLevel() - scoreLevel);
			int o2Lv = Math.abs(PeakArenaScoreLevel.getSocre(o2.getEntry().getComparable()).getLevel() - scoreLevel);

			int levelInterval = o1Lv - o2Lv;
			if (levelInterval != 0) {
				return levelInterval;
			}

			int o1Fighting = Math.abs(o1.getEntry().getExtendedAttribute().getFighting() - fighting);
			int o2Fighting = Math.abs(o2.getEntry().getExtendedAttribute().getFighting() - fighting);
			// 用列表排序，不区分0
			return o1Fighting - o2Fighting;
		}
	}

	public TablePeakArenaData switchTeam(Player player) {
		TablePeakArenaData myPeakArenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		Map<Integer, TeamData> teamMap = myPeakArenaData.getTeamMap();
		TeamData team1 = teamMap.get(1);
		teamMap.put(1, teamMap.get(2));
		teamMap.put(2, teamMap.get(3));
		teamMap.put(3, team1);
		tablePeakArenaDataDAO.update(myPeakArenaData);
		return myPeakArenaData;
	}

	public void addOthersRecord(TablePeakArenaData table, PeakRecordInfo record) {
		List<PeakRecordInfo> list = table.getRecordList();
		list.add(record);
		tablePeakArenaDataDAO.update(table);
	}

	public TablePeakArenaData addScore(Player player, int value) {
		String userId = player.getUserId();
		TablePeakArenaData myPeakArenaData = tablePeakArenaDataDAO.get(userId);
		// Ranking底层保证不为null
		Ranking<Integer, PeakArenaExtAttribute> ranking = RankingFactory.getRanking(RankType.PEAK_ARENA);
		RankingEntry<Integer, PeakArenaExtAttribute> entry = ranking.getRankingEntry(userId);
		if (entry == null) {
			entry = ranking.addOrUpdateRankingEntry(userId, 0, player);
		}
		int score = entry.getComparable();
		int newScore;
		if (value < 0) {
			int minScore = ArenaConstant.PEAK_AREAN_MIN_SCORE;
			if (score <= minScore) {
				// 不执行更新
				return myPeakArenaData;
			}
			newScore = score + value;
			if (newScore < minScore) {
				newScore = minScore;
			}
		} else {
			newScore = score + value;
		}
		ranking.updateRankingEntry(entry, newScore);

		int place = ranking.getRanking(userId);
		if (myPeakArenaData.getMaxPlace() > place) {
			myPeakArenaData.setMaxPlace(place);
		}

		return myPeakArenaData;
	}

	public List<PeakRecordInfo> getArenaRecordList(String userId) {
		// TODO 需要判断非null
		return tablePeakArenaDataDAO.get(userId).getRecordList();
	}

	public int getPlace(Player player) {
		if (player.getLevel() < ArenaConstant.PEAK_ARENA_OPEN_LEVEL) {
			return -1;
		}
		String userId = player.getUserId();
		Ranking<Integer, PeakArenaExtAttribute> ranking = RankingFactory.getRanking(RankType.PEAK_ARENA);
		int place = ranking.getRanking(userId);
		if (place > 0) {
			return place;
		}
		TempRankingEntry entry = new TempRankingEntry();
		getOrAddPeakArenaData(player, entry);
		return entry.getRanking();
	}

	public int getEnemyPlace(String userId) {
		Ranking<Integer, PeakArenaExtAttribute> ranking = RankingFactory.getRanking(RankType.PEAK_ARENA);
		return ranking.getRanking(userId);
	}

	/**
	 * 计算当前能获得的总预期巅峰币
	 * 
	 * @param data
	 * @return
	 */
	public int gainExpectCurrency(TablePeakArenaData data) {
		long currentTime = System.currentTimeMillis();
		long lastTime = data.getLastGainCurrencyTime();
		if (lastTime <= 0) {
			data.setLastGainCurrencyTime(currentTime);
			TablePeakArenaDataDAO.getInstance().update(data);
			return 0;
		}

		int score = getScore(data.getUserId());
		PeakArenaScoreLevel level = PeakArenaScoreLevel.getSocre(score);
		int gainPerHour = level.getGainCurrency();
		// TODO 这个可以优化，缓存起来不需要每次计算
		long millisPerCurrency = MILLIS_PER_HOUR / gainPerHour;
		int expectCurrency = data.getExpectCurrency();
		long passTime = currentTime - lastTime;
		long currency_ = passTime / millisPerCurrency;
		if (currency_ == 0) {
			return expectCurrency;
		}
		long interval = passTime % millisPerCurrency;
		data.setLastGainCurrencyTime(currentTime - interval);
		int result = (int) (expectCurrency + currency_);
		data.setExpectCurrency(result);
		TablePeakArenaDataDAO.getInstance().update(data);
		return result;
	}

	public boolean gainCurrency(Player player, TablePeakArenaData data) {
		int expectCurrency = data.getExpectCurrency();
		if (expectCurrency == 0) {
			return false;
		}
		if (player.getUserGameDataMgr().addPeakArenaCoin(expectCurrency)) {
			data.setExpectCurrency(0);
			this.tablePeakArenaDataDAO.update(data);
			return true;
		}
		return false;
	}

	public int getScore(String userId) {
		Ranking<Integer, PeakArenaExtAttribute> ranking = RankingFactory.getRanking(RankType.PEAK_ARENA);
		RankingEntry<Integer, PeakArenaExtAttribute> entry = ranking.getRankingEntry(userId);
		if (entry == null) {
			TablePeakArenaData arenaData = PeakArenaBM.getInstance().getPeakArenaData(userId);
			if (arenaData != null) {
				return arenaData.getLastScore();
			}
			return 0;
		}
		return entry.getComparable();
	}

	public void resetDataInNewDay(Player player) {
		int level = player.getLevel();
		if (level < ArenaConstant.PEAK_ARENA_OPEN_LEVEL) {
			return;
		}
		String userId = player.getUserId();
		ArenaInfoCfg infoCfg = ArenaInfoCfgDAO.getInstance().getArenaInfo();
		if (infoCfg == null) {
			GameLog.error("竞技场", userId, "重置时找不到巅峰竞技场配置：" + userId);
			return;
		}
		TablePeakArenaData peakArenaData = getPeakArenaData(userId);
		if (peakArenaData == null) {
			GameLog.error("竞技场", userId, "重置时找不到巅峰竞技场玩家：" + userId);
			return;
		}
		peakArenaData.setRemainCount(infoCfg.getCount());
		tablePeakArenaDataDAO.update(peakArenaData);
	}

}

class TempRankingEntry {
	private int ranking = -1;

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

}