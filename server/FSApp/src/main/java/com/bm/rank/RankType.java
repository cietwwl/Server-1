package com.bm.rank;

import java.util.HashMap;

import com.bm.rank.arena.ArenaDailyExtension;
import com.bm.rank.arena.ArenaSettleExtension;
import com.bm.rank.fightingAll.FightingExtension;
import com.bm.rank.group.base.GroupBaseRankExtension;
import com.bm.rank.group.createtime.GroupCreateTimeRankExtension;
import com.bm.rank.group.membernum.GroupMemberNumRankExtension;
import com.bm.rank.level.LevelExtension;
import com.bm.rank.teaminfo.AngelArrayTeamInfoExtension;
import com.rw.fsutil.common.TypeIdentification;
import com.rw.fsutil.ranking.RankingConfig;
import com.rw.fsutil.ranking.RankingExtension;

public enum RankType implements TypeIdentification, RankingConfig {

	//TODO 巅峰竞技场排行榜 PEAK_ARENA(1, 100000, "巅峰竞技场", 1, PeakArenaExtension.class), 
//	SECRET_RANK(2, 100000, "秘境", 1, SecretRankExtension.class),
	LEVEL_PLAYER(2, 5000, "实时等级排行榜", 1, LevelExtension.class, RankingCopyerFactory.getLevelExtCopyer()),
	LEVEL_ALL(3, 5000, "实时等级排行榜", 1, LevelExtension.class, RankingCopyerFactory.getLevelExtCopyer()), 
	LEVEL_ALL_DAILY(4, 5000, "全日等级排行榜", 1, LevelExtension.class, RankingCopyerFactory.getLevelExtCopyer()),
	FIGHTING_ALL(5, 5000, "实时战力排行榜", 1, FightingExtension.class, RankingCopyerFactory.getFightingCopyer()),
	FIGHTING_ALL_DAILY(6, 5000, "全日战力排行榜", 1, FightingExtension.class, RankingCopyerFactory.getFightingCopyer()),
	TEAM_FIGHTING(7, 5000, "实时小队排行榜", 1, FightingExtension.class, RankingCopyerFactory.getFightingCopyer()), 
	TEAM_FIGHTING_DAILY(8, 5000, "全日小队排行榜", 1, FightingExtension.class, RankingCopyerFactory.getFightingCopyer()),
	WARRIOR_ARENA(9, 5000,"实时力士竞技场",1,ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	WARRIOR_ARENA_DAILY(10, 5000,"全日力士竞技场",1,ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	SWORDMAN_ARENA(11, 5000,"实时剑士竞技场",1,ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	SWORDMAN_ARENA_DAILY(12, 5000,"全日剑士竞技场",1,ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	MAGICAN_ARENA(13, 5000,"实时术士竞技场",1,ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	MAGICAN_ARENA_DAILY(14, 5000,"全日术士竞技场",1,ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	PRIEST_ARENA(15, 5000,"实时祭祀竞技场",1,ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	PRIEST_ARENA_DAILY(16, 5000,"全日祭祀竞技场",1,ArenaDailyExtension.class, RankingCopyerFactory.getArenaCopyer()),
	GROUP_BASE_RANK(17, 99, "帮派排行榜", 1, GroupBaseRankExtension.class),
	GROUP_MEMBER_NUM_RANK(18, 5000, "帮派成员排行榜", 1, GroupMemberNumRankExtension.class),
	GROUP_CREATE_TIME_RANK(19, 10, "帮派创建排行榜", 1, GroupCreateTimeRankExtension.class),
//	ANGLE_ARRAY_RANK(20, 20000, "万仙阵匹配排行榜", 1, AngleArrayExtension.class),
	ARENA_SETTLEMENT(21,40000,"竞技场结算",1,ArenaSettleExtension.class),
	ANGEL_TEAM_INFO_RANK(22, 20000, "万仙阵匹配玩家阵容", 1, AngelArrayTeamInfoExtension.class),
//	CHARGE_RANK(23, 5000, "充值排行榜", 1, ChargeRankExtension.class),
	;

	private RankType(int type, int maxCapacity, String name, int updatePeriodMinutes, Class<? extends RankingExtension> clazz,RankingEntityCopyer copyer) {
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
	
	static{
		//TODO 兼容旧代码临时的区分实时和每日的方案
		dailyMap = new HashMap<Integer, RankType>();
		dailyMap.put(101, WARRIOR_ARENA_DAILY);
		dailyMap.put(102, SWORDMAN_ARENA_DAILY);
		dailyMap.put(103, MAGICAN_ARENA_DAILY);
		dailyMap.put(104, PRIEST_ARENA_DAILY);
		dailyMap.put(201, FIGHTING_ALL_DAILY);
		dailyMap.put(203, TEAM_FIGHTING_DAILY);
		dailyMap.put(301, LEVEL_ALL_DAILY);
		
		realTimeMap = new HashMap<Integer, RankType>();
		realTimeMap.put(101, WARRIOR_ARENA);
		realTimeMap.put(102, SWORDMAN_ARENA);
		realTimeMap.put(103, MAGICAN_ARENA);
		realTimeMap.put(104, PRIEST_ARENA);
		realTimeMap.put(201, FIGHTING_ALL);
		realTimeMap.put(203, TEAM_FIGHTING);
		realTimeMap.put(301, LEVEL_ALL);
	}
	
	public static RankType getRankType(int type,int realTime){
		if(realTime == 0){
		    return dailyMap.get(type);
		}else{
			return realTimeMap.get(type);
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

	
	/**获取本职业   每日   排行类型
	 * 	None(0),		//新手
		Warrior(1), 	//力士...
		SwordsMan(2),	//行者...
		Magican(3),     //术士...
		Priest(4);		//祭祀...
	*/
	public static RankType getJobDay(int job){
		switch(job){
			case 1:
				return WARRIOR_ARENA_DAILY;
			case 2:
				return SWORDMAN_ARENA_DAILY;
			case 3:
				return MAGICAN_ARENA_DAILY;
			case 4:
				return RankType.PRIEST_ARENA_DAILY;
		}
		return RankType.WARRIOR_ARENA_DAILY;
	}
	
	/**获取本职业  即时   排行类型
	 * 	0.力士
		1.行者
		2.术士
		3.祭司
		-1.无职业
	*/
	public static RankType getJobCurrent(int job){
		switch(job){
			case 1:
				return WARRIOR_ARENA;
			case 2:
				return SWORDMAN_ARENA;
			case 3:
				return MAGICAN_ARENA;
			case 4:
				return PRIEST_ARENA;
		}
		return RankType.WARRIOR_ARENA;
	}
	
	public RankingEntityCopyer getEntityCopyer() {
		return entityCopyer;
	}
}