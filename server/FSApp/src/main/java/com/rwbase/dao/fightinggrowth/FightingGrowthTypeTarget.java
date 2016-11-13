package com.rwbase.dao.fightinggrowth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.playerdata.fightinggrowth.FSFightingGrowthWayType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FightingGrowthTypeTarget {

	public FSFightingGrowthWayType wayType();
}
