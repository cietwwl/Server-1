package com.bm.rank;

import com.bm.rank.arena.ArenaExtension;
import com.common.HPCUtil;
import com.rw.fsutil.common.TypeIdentification;
import com.rw.fsutil.ranking.ListRankingConfig;
import com.rw.fsutil.ranking.ListRankingExtension;

public enum ListRankingType implements TypeIdentification, ListRankingConfig {

	WARRIOR_ARENA(1, 100000, "力士竞技场", 0, ArenaExtension.class), 
	SWORDMAN_ARENA(2, 100000, "剑士竞技场", 0, ArenaExtension.class), 
	MAGICAN_ARENA(3, 100000, "术士竞技场", 0, ArenaExtension.class), 
	PRIEST_ARENA(4, 100000, "祭祀竞技场", 0, ArenaExtension.class), 
	
	WARRIOR_ARENA_DAILY(11, 100000, "全日力士竞技场", 0, ArenaExtension.class), 
	SWORDMAN_ARENA_DAILY(12, 100000, "全日剑士竞技场", 0, ArenaExtension.class), 
	MAGICAN_ARENA_DAILY(13, 100000, "全日术士竞技场", 0, ArenaExtension.class), 
	PRIEST_ARENA_DAILY(14, 100000, "全日祭祀竞技场", 0, ArenaExtension.class), 
	;

	private final int type;
	private final int maxCapacity;
	private final String name;
	private final int updatePeriodMinutes;
	private final Class<? extends ListRankingExtension> clazz;

	private ListRankingType(int type, int maxCapacity, String name, int updatePeriodMinutes, Class<? extends ListRankingExtension> clazz) {
		this.type = type;
		this.maxCapacity = maxCapacity;
		this.name = name;
		this.updatePeriodMinutes = updatePeriodMinutes;
		this.clazz = clazz;
	}

	@Override
	public int getMaxCapacity() {
		return maxCapacity;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public Class<? extends ListRankingExtension> getSRankingExtension() {
		return clazz;
	}

	@Override
	public int getUpdatePeriodMinutes() {
		return updatePeriodMinutes;
	}

	@Override
	public int getTypeValue() {
		return type;
	}

	private static ListRankingType[] typeArray;

	static {
		TypeIdentification[] orignal = HPCUtil.toMappedArray(values());
		typeArray = new ListRankingType[orignal.length];
		HPCUtil.copy(orignal, typeArray);
	}

	public static ListRankingType getListRankingType(int type) {
		return typeArray[type];
	}
}
