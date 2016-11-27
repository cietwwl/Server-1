package com.bm.rank;

import java.util.HashMap;

import com.bm.rank.arena.ArenaDailyExtension;
import com.bm.rank.arena.ArenaSettleExtension;
import com.bm.rank.consume.ConsumeRankExtension;
import com.bm.rank.fightingAll.FightingExtension;
import com.bm.rank.group.base.GroupBaseRankExtension;
import com.bm.rank.group.createtime.GroupCreateTimeRankExtension;
import com.bm.rank.group.membernum.GroupMemberNumRankExtension;
import com.bm.rank.groupCompetition.groupRank.GCompFightingExtension;
import com.bm.rank.groupCompetition.killRank.GCompKillExtension;
import com.bm.rank.groupCompetition.scoreRank.GCompScoreExtension;
import com.bm.rank.groupCompetition.winRank.GCompContinueWinExtension;
import com.bm.rank.groupFightOnline.GFGroupBiddingExtension;
import com.bm.rank.groupFightOnline.GFOnlineHurtExtension;
import com.bm.rank.groupFightOnline.GFOnlineKillExtension;
import com.bm.rank.groupsecretmatch.GroupSecretMatchRankExtension;
import com.bm.rank.level.LevelExtension;
import com.bm.rank.magicsecret.MagicSecretExtension;
import com.bm.rank.populatity.PopularityRankExtension;
import com.bm.rank.recharge.ChargeRankExtension;
import com.bm.rank.teaminfo.AngelArrayTeamInfoExtension;
import com.rw.fsutil.common.TypeIdentification;
import com.rw.fsutil.ranking.RankingConfig;
import com.rw.fsutil.ranking.RankingExtension;

public enum RankType implements TypeIdentification, RankingConfig {

	// SECRET_RANK(2, 100000, "秘境", 1, SecretRankExtension.class),
	LEVEL_PLAYER(2, 5000, "实时等级排行榜", 5, LevelExtension.class, RankingCopyerFactory.getLevelExtCopyer()),
	LEVEL_ALL(3, 5000, "实时等级排行榜", 5, LevelExtension.class, RankingCopyerFactory.getLevelExtCopyer()),

	LEVEL_ALL_DAILY(4, 5000, "全日等级排行榜", 5, LevelExtension.class, RankingCopyerFactory.getLevelExtCopyer()),
	FIGHTING_ALL(5, 5000, "实时战力排行榜", 5, FightingExtension.class, RankingCopyerFactory.getFightingCopyer()),
	FIGHTING_ALL_DAILY(6, 5000, "全日战力排行榜", 5, FightingExtension.class, RankingCopyerFactory.getFightingCopyer()),
	TEAM_FIGHTING(7, 5000, "实时小队排行榜", 5, FightingExtension.class, RankingCopyerFactory.getFightingCopyer()),
	TEAM_FIGHTING_DAILY(8, 5000, "全日小队排行榜", 5, FightingExtension.class, RankingCopyerFactory.getFightingCopyer()),
	// WARRIOR_ARENA(9, 5000, "实时力士竞技场", 5, ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	// WARRIOR_ARENA_DAILY(10, 5000, "全日力士竞技场", 5, ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	// SWORDMAN_ARENA(11, 5000, "实时剑士竞技场", 5, ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	// SWORDMAN_ARENA_DAILY(12, 5000, "全日剑士竞技场", 5, ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	// MAGICAN_ARENA(13, 5000, "实时术士竞技场", 5, ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	// MAGICAN_ARENA_DAILY(14, 5000, "全日术士竞技场", 5, ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	// PRIEST_ARENA(15, 5000, "实时祭祀竞技场", 5, ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	// PRIEST_ARENA_DAILY(16, 5000, "全日祭祀竞技场", 5, ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	GROUP_BASE_RANK(17, 99, "帮派排行榜", 5, GroupBaseRankExtension.class),
	GROUP_MEMBER_NUM_RANK(18, 5000, "帮派成员排行榜", 5, GroupMemberNumRankExtension.class),
	GROUP_CREATE_TIME_RANK(19, 10, "帮派创建排行榜", 5, GroupCreateTimeRankExtension.class),
	// ANGLE_ARRAY_RANK(20, 20000, "万仙阵匹配排行榜", 1, AngleArrayExtension.class),
	ARENA_SETTLEMENT(21, 40000, "竞技场结算", 5, ArenaSettleExtension.class),
	ANGEL_TEAM_INFO_RANK(22, 20000, "万仙阵匹配玩家阵容", 5, AngelArrayTeamInfoExtension.class),
	MAGIC_SECRET_SCORE_RANK(23, 30000, "法宝秘境积分排行榜", 5, MagicSecretExtension.class),
	GROUP_SECRET_MATCH_RANK(24, 10000, "帮派秘境匹配排行榜", 5, GroupSecretMatchRankExtension.class),

	// TODO 巅峰竞技场排行榜
	PEAK_ARENA(25, 100000, "巅峰竞技场", 10, ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	PEAK_ARENA_FIGHTING(26, 100000, "巅峰竞技场", 10, FightingExtension.class, RankingCopyerFactory.getFightingCopyer()),
	GF_ONLINE_GROUP_BID_RANK(27, 2000, "在线帮战竞标排行榜", 1, GFGroupBiddingExtension.class),
	GF_ONLINE_KILL_RANK(28, 8000, "在线帮战杀敌排行榜", 1, GFOnlineKillExtension.class),
	GF_ONLINE_HURT_RANK(29, 8000, "在线帮战伤害排行榜", 1, GFOnlineHurtExtension.class),

	// 帮派争霸赛排行榜
	GCOMP_CONTINUE_WIN_RANK(30, 1000, "帮派争霸赛最高连胜排行榜", 1, GCompContinueWinExtension.class),
	GCOMP_KILL_RANK(31, 1000, "帮派争霸赛杀敌排行榜", 1, GCompKillExtension.class),
	GCOMP_SCORE_RANK(32, 1000, "帮派争霸赛最得分排行榜", 1, GCompScoreExtension.class),
	GROUP_FIGHTING_RANK(33, 200, "帮派战力排行榜", 1, GCompFightingExtension.class),

	// 个人人气榜
	POPULARITY_RANK(34, 10000, "个人人气排行榜", 1, PopularityRankExtension.class),
	// 机器人等级榜
	LEVEL_ROBOT(36, 100, "机器人等级排行榜", 1, LevelExtension.class, RankingCopyerFactory.getLevelExtCopyer()),

	// 充值排行榜
	ACTIVITY_CHARGE_RANK(37, 1000, "充值排行榜", 1, ChargeRankExtension.class),
	// 消费排行榜
	ACTIVITY_CONSUME_RANK(38, 1000, "消费排行榜", 1, ConsumeRankExtension.class),

	// 竞技场排行榜
	ARENA(39, 20000, "竞技场", 5, ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	ARENA_DAILY(40, 20000, "竞技场", 5, ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer());

	private RankType(int type, int maxCapacity, String name, int updatePeriodMinutes, Class<? extends RankingExtension> clazz, RankingEntityCopyer copyer) {
		this(type, maxCapacity, name, updatePeriodMinutes, clazz);
		this.entityCopyer = copyer;
	}

	private RankType(int type, int maxCapacity, String name, int updatePeriodMinutes, Class<? extends RankingExtension> clazz) {
		this.type = type;
		this.maxCapacity = maxCapacity;
		this.name = name;
		this.updatePeriodMinutes = updatePeriodMinutes;
		this.clazz = clazz;
	}

	private final int type; // 排行榜类型
	private final int maxCapacity; // 排行榜的最大容量
	private final String name; // 排行榜的名字
	private final int updatePeriodMinutes;// 同步数据到数据库的周期
	private final Class<? extends RankingExtension> clazz;
	private RankingEntityCopyer entityCopyer;

	private static HashMap<Integer, RankType> dailyMap;
	private static HashMap<Integer, RankType> realTimeMap;

	static {
		// TODO 兼容旧代码临时的区分实时和每日的方案
		dailyMap = new HashMap<Integer, RankType>();
		// dailyMap.put(101, WARRIOR_ARENA_DAILY);
		// dailyMap.put(102, SWORDMAN_ARENA_DAILY);
		// dailyMap.put(103, MAGICAN_ARENA_DAILY);
		// dailyMap.put(104, PRIEST_ARENA_DAILY);

		dailyMap.put(101, ARENA_DAILY);

		dailyMap.put(105, PEAK_ARENA);
		dailyMap.put(201, FIGHTING_ALL_DAILY);
		dailyMap.put(203, TEAM_FIGHTING_DAILY);
		dailyMap.put(301, LEVEL_ALL_DAILY);

		realTimeMap = new HashMap<Integer, RankType>();
		// realTimeMap.put(101, WARRIOR_ARENA);
		// realTimeMap.put(102, SWORDMAN_ARENA);
		// realTimeMap.put(103, MAGICAN_ARENA);
		// realTimeMap.put(104, PRIEST_ARENA);

		realTimeMap.put(101, ARENA);

		realTimeMap.put(105, PEAK_ARENA);
		realTimeMap.put(201, FIGHTING_ALL);
		realTimeMap.put(203, TEAM_FIGHTING);
		realTimeMap.put(301, LEVEL_ALL);
		realTimeMap.put(401, MAGIC_SECRET_SCORE_RANK);
		realTimeMap.put(601, POPULARITY_RANK);
		realTimeMap.put(405, GROUP_FIGHTING_RANK);
	}

	public static RankType getRankType(int type, int realTime) {
		if (realTime == 0) {
			RankType rankType = dailyMap.get(type);
			if (rankType == null) {
				return ARENA_DAILY;
			} else {
				return rankType;
			}
		} else {
			RankType rankType = realTimeMap.get(type);
			if (rankType == null) {
				return ARENA;
			} else {
				return rankType;
			}
		}
	}

	public int getType() {
		return type;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public String getName() {
		return name;
	}

	@Override
	public int getTypeValue() {
		return type;
	}

	@Override
	public Class<? extends RankingExtension> getRankingExtension() {
		return this.clazz;
	}

	@Override
	public int getUpdatePeriodMinutes() {
		return this.updatePeriodMinutes;
	}

	/**
	 * 获取本职业 每日 排行类型 None(0), //新手 Warrior(1), //力士... SwordsMan(2), //行者... Magican(3), //术士... Priest(4); //祭祀...
	 */
	public static RankType getJobDay(int job) {
		return RankType.ARENA_DAILY;
	}

	/**
	 * 获取本职业 即时 排行类型 0.力士 1.行者 2.术士 3.祭司 -1.无职业
	 */
	public static RankType getJobCurrent(int job) {
		return RankType.ARENA;
	}

	public RankingEntityCopyer getEntityCopyer() {
		return entityCopyer;
	}
}