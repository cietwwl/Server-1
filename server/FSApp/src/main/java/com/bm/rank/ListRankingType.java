package com.bm.rank;

import java.util.EnumMap;

import com.bm.rank.arena.ArenaExtension;
import com.common.HPCUtil;
import com.rw.fsutil.common.TypeIdentification;
import com.rw.fsutil.ranking.ListRankingConfig;
import com.rw.fsutil.ranking.ListRankingExtension;
import com.rw.service.PeakArena.datamodel.PeakArenaExtension;
import com.rwbase.common.enu.ECareer;

public enum ListRankingType implements TypeIdentification, ListRankingConfig {

	WARRIOR_ARENA(ECareer.Warrior.getValue(), 20000, "力士竞技场", 1, ArenaExtension.class, RankType.WARRIOR_ARENA), 
	SWORDMAN_ARENA(ECareer.SwordsMan.getValue(), 20000, "剑士竞技场", 1, ArenaExtension.class, RankType.SWORDMAN_ARENA), 
	MAGICAN_ARENA(ECareer.Magican.getValue(), 20000, "术士竞技场", 1, ArenaExtension.class, RankType.MAGICAN_ARENA), 
	PRIEST_ARENA(ECareer.Priest.getValue(), 20000, "祭祀竞技场", 1, ArenaExtension.class, RankType.PRIEST_ARENA), 
	PEAK_ARENA(100,20000,"巅峰竞技场",10,PeakArenaExtension.class,null),
	;

	private final int type;
	private final int maxCapacity;
	private final String name;
	private final int updatePeriodMinutes;
	private final Class<? extends ListRankingExtension> clazz;
	private final RankType rankType;

	private ListRankingType(int type, int maxCapacity, String name, int updatePeriodMinutes,
			Class<? extends ListRankingExtension> clazz,RankType rankType) {
		this.type = type;
		this.maxCapacity = maxCapacity;
		this.name = name;
		this.updatePeriodMinutes = updatePeriodMinutes;
		this.clazz = clazz;
		this.rankType = rankType;
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
	
	public RankType getRankType() {
		return rankType;
	}

	private static ListRankingType[] typeArray;
	private static EnumMap<RankType,ListRankingType> rankTypeMapping;

	static {
		ListRankingType[] array = values();
		TypeIdentification[] orignal = HPCUtil.toMappedArray(array);
		typeArray = new ListRankingType[orignal.length];
		HPCUtil.copy(orignal, typeArray);
		rankTypeMapping = new EnumMap<RankType,ListRankingType>(RankType.class);
		for(ListRankingType type:array){
			rankTypeMapping.put(type.getRankType(), type);
		}
	}

	public static ListRankingType getListRankingType(int type) {
		return typeArray[type];
	}
	
	public static ListRankingType getListRankingType(RankType type){
		return rankTypeMapping.get(type);
	}
}
