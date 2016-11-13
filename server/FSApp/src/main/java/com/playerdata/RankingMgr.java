package com.playerdata;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;





import com.alibaba.druid.pool.DruidDataSource;
import com.bm.arena.ArenaBM;
import com.bm.rank.ListRankingType;
import com.bm.rank.RankType;
import com.bm.rank.RankingEntityCopyer;
import com.bm.rank.angelarray.AngelArrayComparable;
import com.bm.rank.arena.ArenaExtAttribute;
import com.bm.rank.arena.ArenaRankingComparable;
import com.bm.rank.arena.ArenaSettleComparable;
import com.bm.rank.arena.ArenaSettlement;
import com.bm.rank.fightingAll.FightingComparable;
import com.bm.rank.level.LevelComparable;
import com.bm.rank.teaminfo.AngelArrayTeamInfoAttribute;
import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntityOfRank;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.ranking.impl.RankingEntryData;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.manager.GameManager;
import com.rw.netty.UserChannelMgr;
import com.rw.service.Email.EmailUtils;
import com.rw.service.PeakArena.PeakArenaBM;
import com.rw.service.PeakArena.datamodel.TablePeakArenaData;
import com.rw.service.PeakArena.datamodel.TablePeakArenaDataDAO;
import com.rw.service.PeakArena.datamodel.TeamData;
import com.rw.service.log.db.BILogDbMgr;
import com.rw.service.ranking.ERankingType;
import com.rw.service.ranking.RankingGetOperation;
import com.rwbase.common.enu.ECareer;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.ranking.CfgRankingDAO;
import com.rwbase.dao.ranking.RankingUtils;
import com.rwbase.dao.ranking.pojo.CfgRanking;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.user.User;
import com.rwbase.gameworld.GameWorld;
import com.rwbase.gameworld.GameWorldConstant;
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
	private static BILogDbMgr biLogDbMgr;

	
	private static RankingMgr m_instance = new RankingMgr();

	public static RankingMgr getInstance() {
		if (m_instance == null) {
			m_instance = new RankingMgr();
		}
		return m_instance;
	}

	private final EnumMap<RankType, RankingGetOperation> operationMap;
	private final RankingGetOperation defaultGetOp;
	private final EnumMap<ListRankingType, Pair<String, String>> emailMap;
	private final EnumMap<RankType, RankType> dailyMapping;

	public RankingMgr() {
		this.operationMap = new EnumMap<RankType, RankingGetOperation>(RankType.class);
		this.operationMap.put(RankType.WARRIOR_ARENA, RankingGetOperation.ARENA_GET_OPERATION);
		this.operationMap.put(RankType.SWORDMAN_ARENA, RankingGetOperation.ARENA_GET_OPERATION);
		this.operationMap.put(RankType.PRIEST_ARENA, RankingGetOperation.ARENA_GET_OPERATION);
		this.operationMap.put(RankType.MAGICAN_ARENA, RankingGetOperation.ARENA_GET_OPERATION);
		this.operationMap.put(RankType.PEAK_ARENA, RankingGetOperation.ARENA_GET_OPERATION);
		this.defaultGetOp = RankingGetOperation.RANKING_GET_OPERATION;
		// TODO 这个邮件id应该配置到竞技场配置表
		this.emailMap = new EnumMap<ListRankingType, Pair<String, String>>(ListRankingType.class);
		this.emailMap.put(ListRankingType.WARRIOR_ARENA, Pair.Create("10011", "10015"));
		this.emailMap.put(ListRankingType.SWORDMAN_ARENA, Pair.Create("10012", "10016"));
		this.emailMap.put(ListRankingType.MAGICAN_ARENA, Pair.Create("10013", "10017"));
		this.emailMap.put(ListRankingType.PRIEST_ARENA, Pair.Create("10014", "10018"));
		// TODO 实时榜与昨日榜的映射关系
		this.dailyMapping = new EnumMap<RankType, RankType>(RankType.class);
		this.dailyMapping.put(RankType.WARRIOR_ARENA, RankType.WARRIOR_ARENA_DAILY);
		this.dailyMapping.put(RankType.SWORDMAN_ARENA, RankType.SWORDMAN_ARENA_DAILY);
		this.dailyMapping.put(RankType.MAGICAN_ARENA, RankType.MAGICAN_ARENA_DAILY);
		this.dailyMapping.put(RankType.PRIEST_ARENA, RankType.PRIEST_ARENA_DAILY);
		this.dailyMapping.put(RankType.LEVEL_ALL, RankType.LEVEL_ALL_DAILY);
		this.dailyMapping.put(RankType.FIGHTING_ALL, RankType.FIGHTING_ALL_DAILY);
		this.dailyMapping.put(RankType.TEAM_FIGHTING, RankType.TEAM_FIGHTING_DAILY);
		
		
		DruidDataSource dataSource = SpringContextUtil.getBean("dataSourceMT");
		biLogDbMgr = new BILogDbMgr(dataSource);
		
		
	}

	public void onInitRankData() {
		resetUpdateState();
		arenaCalculate();
		initAngelArrayTeamInfo();
		checkRobotLevel();//此处机器人生成要比玩家生成优先；如果玩家榜没人说明是新区；可以用方法拷贝；如果玩家榜有人说明是老区；从数据库拷贝
		checkPlayerLevel();
		
	}

	public void checkPlayerLevel() {
		Ranking<LevelComparable, RankingLevelData> levelRanking = RankingFactory.getRanking(RankType.LEVEL_PLAYER);
		if (levelRanking.size() > 0) {
			return;
		}
		changeDailyData(RankType.LEVEL_ALL, RankType.LEVEL_PLAYER, true);
	}

	public void checkRobotLevel() {
		Ranking<LevelComparable, RankingLevelData> levelRanking = RankingFactory.getRanking(RankType.LEVEL_ROBOT);	
		if (levelRanking.size() > 0) {
			GameLog.error(LogModule.robotFriend, "", "重启发现机器人榜有数据，size =" + levelRanking.size()+"  榜id = " + RankType.LEVEL_ROBOT.getType(), null);
			return;
		}
//		RankingEntityCopyer copyer = RankType.LEVEL_ROBOT.getEntityCopyer();
		List<User> userList = getRobotFromUserTable();
		if(userList == null|| userList.isEmpty()){
			GameLog.error(LogModule.COMMON, null, "从user表获取机器人失败,无法生成榜单", null);
			return;
		}
		ArrayList<RankingEntryData> rankingList = userToRankingEntryList(userList,RankType.LEVEL_ROBOT);
		if(rankingList == null || rankingList.isEmpty()){
			GameLog.error(LogModule.COMMON, null, "取出的数据无法生成entrydata", null);
			return;
		}
		GameLog.info("机器人好友", null, "机器人榜单处理开始，size = " + rankingList.size(), null);
		for (int i = 1, size = rankingList.size(); i <= size; i++) {
			RankingEntryData entry = rankingList.get(i - 1);
			LevelComparable comparable = new LevelComparable(Integer.parseInt(entry.getCondition()), 0);
			RankingLevelData levelData = RankingUtils.createRankingLevelDataByEntryData(entry);
			levelRanking.addOrUpdateRankingEntry(entry.getKey(), comparable, levelData);
		}		
		GameLog.info("机器人好友", null, "机器人榜单处理结束，size = " + rankingList.size(), null);
	}
	
	/* 把实时排行榜数据拷贝到每日排行榜 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void changeDailyData(RankType ordinalType, RankType copyType, boolean ignoreRobot) {
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
		for (int i = 1, size = list.size(); i <= size; i++) {
			MomentRankingEntry entry = list.get(i - 1);
			if (ignoreRobot && entry.getKey().length() > 20) {
				continue;
			}
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
			if (ext instanceof RankingLevelData) {
				RankingLevelData levelData = (RankingLevelData) ext;
				levelData.setRankLevel(i);
			}
			copyList.add(re);
		}
		copyRanking.clearAndInsert(copyList);
	}
	
	/**将取出来的机器人user转为排行榜entry*/
	private ArrayList<RankingEntryData> userToRankingEntryList(
			List<User> userList,RankType type) {
		int size = userList.size();
		ArrayList<RankingEntryData> rankingList = new ArrayList<RankingEntryData>(size);
		for (int i = 0; i < size; i++) {
			User user = userList.get(i);
			Long id = (long)(i+1);
			String key = user.getUserId();
			String condition = user.getLevel()+"";
			String extension = user.getLevel()+","+user.getVip();
			RankingEntryData data = new RankingEntryData(id, type.getType(), key, condition, extension);
			rankingList.add(data);
		}
		return rankingList;
	}

	private List<User> getRobotFromUserTable() {
		final List<User> userList = new ArrayList<User>();
		int friendFuncOpenLevel = CfgOpenLevelLimitDAO.getInstance().getOpenLevel(eOpenLevelType.FRIEND);
		final String sql = "SELECT userId,vip,level FROM user where level >= " + friendFuncOpenLevel + " and userId not like '%"+String.valueOf(GameManager.getZoneId())+"%' limit 105" ;
		doReadRobotDb(sql,new RobotInteface() {
			
			@Override
			public void doCount(User user) {				
				if(user.getUserId().length()<= 20||userList.size()>99){
					return;
				}
				userList.add(user);				
			}
		});		
		return userList;
	}

	private void doReadRobotDb(String sql, RobotInteface robotInteface) {
		int OFFSET = 0;
		final int LIMIT = 1000;
		List<User> userList = biLogDbMgr.query(sql, new Object[]{}, User.class);
		for (User user : userList) {
			robotInteface.doCount(user);
		}
	}

	/**
	 * 初始化竞技场阵容排行榜
	 */
	private void initAngelArrayTeamInfo() {
		ArrayList<? extends ListRankingEntry<String, ArenaExtAttribute>> list = new ArrayList<ListRankingEntry<String, ArenaExtAttribute>>();
		list.addAll(RankingFactory.getSRanking(ListRankingType.WARRIOR_ARENA).getEntrysCopy());
		list.addAll(RankingFactory.getSRanking(ListRankingType.SWORDMAN_ARENA).getEntrysCopy());
		list.addAll(RankingFactory.getSRanking(ListRankingType.MAGICAN_ARENA).getEntrysCopy());
		list.addAll(RankingFactory.getSRanking(ListRankingType.PRIEST_ARENA).getEntrysCopy());

		int allSize = list.size();// 几个竞技场排行榜的数据

		Ranking<AngelArrayComparable, AngelArrayTeamInfoAttribute> ranking = RankingFactory.getRanking(RankType.ANGEL_TEAM_INFO_RANK);
		int rankingSize = ranking.size();

		if (allSize <= rankingSize) {
			return;
		}

		long now = System.currentTimeMillis();

		for (int i = 0; i < allSize; i++) {
			ListRankingEntry<String, ArenaExtAttribute> listRankingEntry = list.get(i);
			if (listRankingEntry == null) {
				continue;
			}

			ArenaExtAttribute arenaExtAttr = listRankingEntry.getExtension();
			if (arenaExtAttr == null) {
				continue;
			}

			String key = listRankingEntry.getKey();
			RankingEntry<AngelArrayComparable, AngelArrayTeamInfoAttribute> rankingEntry = ranking.getRankingEntry(key);
			if (rankingEntry == null) {
				AngelArrayComparable comparable = new AngelArrayComparable();
				comparable.setLevel(arenaExtAttr.getLevel());
				comparable.setFighting(arenaExtAttr.getFightingTeam());

				AngelArrayTeamInfoAttribute attr = new AngelArrayTeamInfoAttribute();
				attr.setUserId(key);
				attr.setTime(now);

				ranking.addOrUpdateRankingEntry(key, comparable, attr);
			}
		}
	}

	/**
	 * 对外提供重置每日排行榜的通知
	 */
	@SuppressWarnings("unchecked")
	public void resetUpdateState() {
		GameLog.info("RankingMgr", "resetUpdateState", "执行排行榜重置：" + DateUtils.getyyyyMMddHHmmFormater().format(new Date()));
		try {
			GameWorld world = GameWorldFactory.getGameWorld();
			String lastResetText = world.getAttribute(GameWorldKey.DAILY_RANKING_RESET);
			if (lastResetText != null && !lastResetText.isEmpty()) {
				long lastResetTime = Long.parseLong(lastResetText);
				if (!DateUtils.isResetTime(5, 0, 0, lastResetTime)) {
					return;
				}
			}
			GameLog.info("RankingMgr", "resetUpdateState", "执行排行榜重置开始：" + DateUtils.getyyyyMMddHHmmFormater().format(new Date()));
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
					RankingLevelData levelData = RankingUtils.createRankingLevelData(m);
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

			changeDailyData(RankType.FIGHTING_ALL, RankType.FIGHTING_ALL_DAILY, false);
			changeDailyData(RankType.TEAM_FIGHTING, RankType.TEAM_FIGHTING_DAILY, false);
			changeDailyData(RankType.LEVEL_ALL, RankType.LEVEL_ALL_DAILY, false);

			// // 初始化万仙阵数据
			// changeAngleArrayMatchRankData(RankType.ANGLE_ARRAY_RANK);
			world.updateAttribute(GameWorldKey.DAILY_RANKING_RESET, String.valueOf(System.currentTimeMillis()));
			GameLog.info("RankingMgr", "resetUpdateState", "执行排行榜重置结束：" + DateUtils.getyyyyMMddHHmmFormater().format(new Date()));
		} catch (Exception e) {
			GameLog.error("RankingMgr", "#resetUpdateState()", "重置排行榜异常", e);
		}
	}

	public void arenaCalculate() {
		GameLog.info("RankingMgr", "arenaCalculate", "执行结算：" + DateUtils.getyyyyMMddHHmmFormater().format(new Date()));
		GameWorld world = GameWorldFactory.getGameWorld();
		String lastResetText = world.getAttribute(GameWorldKey.ARENA_CALCULATE);
		long resetMillis = DateUtils.getResetTime(GameWorldConstant.ARENA_CALCULATE_HOUR, GameWorldConstant.ARENA_CALCULATE_MINUTE, GameWorldConstant.ARENA_CALCULATE_SECOND);
		if (lastResetText != null && !lastResetText.isEmpty()) {
			long lastResetTime = Long.parseLong(lastResetText);
			if (lastResetTime > resetMillis) {
				return;
			}
		}
		GameLog.info("RankingMgr", "arenaCalculate", "执行结算开始：" + DateUtils.getyyyyMMddHHmmFormater().format(new Date()));
		try {
			ArrayList<RankingEntityOfRank<ArenaSettleComparable, ArenaSettlement>> settletList = new ArrayList<RankingEntityOfRank<ArenaSettleComparable, ArenaSettlement>>();
			changeDailyData(ListRankingType.WARRIOR_ARENA, RankType.WARRIOR_ARENA_DAILY, settletList, resetMillis);
			changeDailyData(ListRankingType.SWORDMAN_ARENA, RankType.SWORDMAN_ARENA_DAILY, settletList, resetMillis);
			changeDailyData(ListRankingType.MAGICAN_ARENA, RankType.MAGICAN_ARENA_DAILY, settletList, resetMillis);
			changeDailyData(ListRankingType.PRIEST_ARENA, RankType.PRIEST_ARENA_DAILY, settletList, resetMillis);
			Ranking<ArenaSettleComparable, ArenaSettlement> settleRanking = RankingFactory.getRanking(RankType.ARENA_SETTLEMENT);
			settleRanking.clearAndInsert(settletList);
			rewardOnlinePlayers();
		} finally {
			world.updateAttribute(GameWorldKey.ARENA_CALCULATE, String.valueOf(System.currentTimeMillis()));
			GameLog.info("RankingMgr", "arenaCalculate", "执行结算结束：" + DateUtils.getyyyyMMddHHmmFormater().format(new Date()));
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
		String oldChampion = null;
		String currentChampoin = null;
		WorshipMgr.getInstance().changeFirstRanking(copyType);
		for (int i = 1; i <= size; i++) {
			ListRankingEntry<String, ArenaExtAttribute> entry = list.get(i - 1);
			String key = entry.getKey();
			RankingLevelData levelData = RankingUtils.createRankingLevelData(entry);
			if (i == 1) {
				RankingEntry<ArenaRankingComparable, RankingLevelData> lastChampion = ranking.getRankingEntry(1);

				if (lastChampion == null) {
					// 第一名悬空
					currentChampoin = key;
				} else {
					String oldKey = lastChampion.getKey();
					// 第一名易主
					if (!oldKey.equals(key)) {
						currentChampoin = key;
						oldChampion = oldKey;
					}
				}
			}

			ArenaSettleComparable sc = new ArenaSettleComparable();
			sc.setRanking(i);
			ArenaSettlement settlement = new ArenaSettlement();
			settlement.setCareer(career);
			settlement.setSettleMillis(currentTime);
			RankingEntityOfRankImpl<ArenaSettleComparable, ArenaSettlement> settleEntity = new RankingEntityOfRankImpl<ArenaSettleComparable, ArenaSettlement>(i, sc, key, settlement);
			settletList.add(settleEntity);
			if (size > total) {
				continue;
			}
			int rank = ranking.getRanking(key);
			if (rank > 0) {
				levelData.setRankCount(Math.abs(rank - i));
			}
			ArenaExtAttribute attribute = entry.getExtension();
			attribute.setRankLevel(i);
			levelData.setRankLevel(i);
			ArenaRankingComparable rankComparable = new ArenaRankingComparable();
			rankComparable.setRanking(entry.getRanking());
			RankingEntityOfRankImpl<ArenaRankingComparable, RankingLevelData> entity = new RankingEntityOfRankImpl<ArenaRankingComparable, RankingLevelData>(i, rankComparable, key, levelData);
			currentList.add(entity);
		}
		ranking.clearAndInsert(currentList);
		// 增加邮件通知
		boolean hasCurrentChampoin = currentChampoin != null;
		boolean hasOldChampoin = oldChampion != null;
		if (hasCurrentChampoin || hasOldChampoin) {
			try {
				Pair<String, String> emailInfo = this.emailMap.get(ordinalType);
				if (emailInfo == null) {
					return;
				}
				if (hasCurrentChampoin) {
					EmailUtils.sendEmail(currentChampoin, emailInfo.getT1());
				}
				if (hasOldChampoin) {
					EmailUtils.sendEmail(oldChampion, emailInfo.getT2());
				}
			} catch (Exception e) {
				GameLog.error("RankingMgr", "#changeDailyData", "结算邮件发送异常：" + currentChampoin + "," + oldChampion);
			}
		}
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
			// toData.setFightingAll(p.getHeroMgr().getFightingAll());
			// toData.setFightingTeam(p.getHeroMgr().getFightingTeam());
			toData.setFightingAll(p.getHeroMgr().getFightingAll(p));
			toData.setFightingTeam(p.getHeroMgr().getFightingTeam(p));
			toData.setUserHead(p.getHeadImage());
			toData.setHeadbox(p.getHeadFrame());
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
		try {
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
			default:
				return null;
				// rankingType = RankType.WARRIOR_ARENA_DAILY;
				// break;
			}

			Ranking<?, ?> ranking = RankingFactory.getRanking(rankingType);
			if (ranking == null) {
				GameLog.error("ranking", "getFirstRankingData", "找不到指定竞技场类型：" + type);
				return null;
			}
			RankingEntry<?, ?> entry = ranking.getFirstEntry();
			if (entry == null) {
				return null;
			}
			String userId = (String) entry.getKey();
			Player p = (Player) PlayerMgr.getInstance().getReadOnlyPlayer(userId);
			if (p == null) {
				GameLog.error("ranking", "getFirstRankingData", "找不到玩家：" + userId);
				return null;
			}
			RankingLevelData levelData = (RankingLevelData) entry.getExtendedAttribute();
			RankingLevelData toData = new RankingLevelData();
			toData.setUserId(userId);
			toData.setUserName(p.getUserName());
			toData.setLevel(p.getLevel());
			toData.setExp(p.getExp());
			// toData.setFightingAll(p.getHeroMgr().getFightingAll());
			// toData.setFightingTeam(p.getHeroMgr().getFightingTeam());
			toData.setFightingAll(p.getHeroMgr().getFightingAll(p));
			toData.setFightingTeam(p.getHeroMgr().getFightingTeam(p));
			toData.setUserHead(p.getHeadImage());
			toData.setHeadbox(p.getHeadFrame());
			toData.setModelId(RankingUtils.getModelId(levelData));
			toData.setJob(levelData.getJob());
			toData.setSex(p.getSex());
			toData.setCareerLevel(p.getStarLevel());
			toData.setArenaPlace(ArenaBM.getInstance().getOtherArenaPlace(userId, p.getCareer()));
			return toData;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据排行类型获取排行数据 上次排序
	 * 
	 * @param rankType
	 * @return
	 */
	public List<RankingLevelData> getRankList(RankType rankType) {
		CfgRanking cfg = CfgRankingDAO.getInstance().getRankingCf(getCfgId(rankType));
		int maxCount = cfg.getRankNum();
		return getRankList(rankType, maxCount);
	}

	public List<RankingLevelData> getRankList(RankType rankType, int count) {
		return getRankingGetOp(rankType).getRankList(rankType, count);
	}

	/** 根据类型、用户ID 获取排名 无数据返回0 0表示未入榜 */
	public int getRankLevel(RankType rankType, String userId) {
		RankingGetOperation op = getRankingGetOp(rankType);
		return op.getRanking(rankType, userId);
	}

	/** 获取玩家的排名数据 上次排序 */
	public RankingLevelData getRankLevelData(RankType rankType, String userId) {
		RankingGetOperation op = getRankingGetOp(rankType);
		return op.getRankLevelData(rankType, userId);
	}

	/** 生成或改变新的一条数据 */
	private boolean changeNewData(Player pPlayer, ERankingType rankType) {
		if (pPlayer == null) {
			return false;
		}
		if (rankType == ERankingType.ATHLETICS_FIGHTING) {
			return PeakArenaBM.getInstance().isOpen(pPlayer);
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
		// int teamFighting = player.getHeroMgr().getFightingTeam();
		// int fighting = player.getHeroMgr().getFightingAll();
		int teamFighting = player.getHeroMgr().getFightingTeam(player);
		int fighting = player.getHeroMgr().getFightingAll(player);
		String userId = player.getUserId();
		boolean teamFightingChanged = checkUpdateFighting(player, RankType.TEAM_FIGHTING, teamFighting, ERankingType.TEAM_FIGHTING_ALL);
		boolean allFightingChanged = checkUpdateFighting(player, RankType.FIGHTING_ALL, fighting, ERankingType.FIGHTING_ALL);
		checkUpdateFighting(player, RankType.PEAK_ARENA_FIGHTING, getPeakArenaFighting(player), ERankingType.ATHLETICS_FIGHTING);
		if (teamFightingChanged || allFightingChanged) {
			updateEntryFighting(RankType.LEVEL_ALL, fighting, teamFighting, userId);
			// TODO 这两个可以合并在updateFighting操作中
			updateEntryFighting(RankType.TEAM_FIGHTING, fighting, teamFighting, userId);
			updateEntryFighting(RankType.FIGHTING_ALL, fighting, teamFighting, userId);
			updateEntryFighting(RankType.PEAK_ARENA_FIGHTING, fighting, teamFighting, userId);
			// 通知竞技场更新
			ArenaBM.getInstance().onPlayerChanged(player);
		}
	}

	public int getPeakArenaFighting(Player player) {
		TablePeakArenaData peakArenaData = TablePeakArenaDataDAO.getInstance().get(player.getUserId());
		if (peakArenaData == null) {
			return 0;
		}
		TeamData[] array = peakArenaData.getTeams();
		int fighting = 0;
		HeroMgr heroMgr = player.getHeroMgr();
		for (int i = array.length; --i >= 0;) {
			TeamData teamData = array[i];
			List<String> list = teamData.getHeros();
			for (int j = list.size(); --j >= 0;) {
				String id = list.get(j);
				// Hero hero = heroMgr.getHeroById(id);
				Hero hero = heroMgr.getHeroById(player, id);
				if (hero == null) {
					continue;
				}
				fighting += hero.getFighting();
			}
		}
		return fighting;
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
		int level = player.getLevel();
		long exp = player.getExp();
		changeLevel(player, RankType.LEVEL_ALL, level, exp);
		updateLevelAndExp(RankType.LEVEL_ALL, level, exp, userId);
		updateLevelAndExp(RankType.TEAM_FIGHTING, level, exp, userId);
		updateLevelAndExp(RankType.FIGHTING_ALL, level, exp, userId);
		updateLevelAndExp(RankType.POPULARITY_RANK, level, exp, userId);
		if (!player.isRobot()) {
			changeLevel(player, RankType.LEVEL_PLAYER, level, exp);
			updateLevelAndExp(RankType.LEVEL_PLAYER, level, exp, userId);
		}
		ArenaBM.getInstance().onPlayerChanged(player);
	}

	private void changeLevel(Player player, RankType type, int level, long exp) {
		String userId = player.getUserId();
		Ranking<LevelComparable, RankingLevelData> ranking = RankingFactory.getRanking(type);
		RankingEntry<LevelComparable, RankingLevelData> entry = ranking.getRankingEntry(userId);
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
	}

	public void addPeakFightingRanking(Player player) {
		try {
			checkUpdateFighting(player, RankType.PEAK_ARENA_FIGHTING, getPeakArenaFighting(player), ERankingType.ATHLETICS_FIGHTING);
		} catch (Exception ex) {
			GameLog.error("PeakArenaBM", player.getUserId(), "添加到排行榜异常:", ex);
		}
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

	private RankingGetOperation getRankingGetOp(RankType type) {
		RankingGetOperation getOp = this.operationMap.get(type);
		if (getOp != null) {
			return getOp;
		}
		return defaultGetOp;
	}

	public RankType getDailyRankType(RankType type) {
		return this.dailyMapping.get(type);
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
		case PEAK_ARENA:
		case PEAK_ARENA_FIGHTING:
			id = 105;
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
		case POPULARITY_RANK:
			id = 601;
			break;
		default:
			id = 101;
			break;
		}
		return id;
	}
	
	private interface RobotInteface{
		public void doCount(User user);
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