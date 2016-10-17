package com.rwbase.dao.fighting.pojo;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.rwbase.common.FightingIndexKey;

public class ExpectedHeroStatusCfg {

	private int level;
	private int expectedHeroCount;
	private int expectedQuality;
	private int expectedStar;
	@FightingIndexKey(1)
	private int levelOfTaoist1;
	@FightingIndexKey(2)
	private int levelOfTaoist2;
	@FightingIndexKey(3)
	private int levelOfTaoist3;
	private int fashionSuitCount;
	private int fashionWingCount;
	private int fashionPetCount;

	private Map<Integer, Integer> expectedLevelOfTag;
	
	public void afterInit() throws Exception {
		Field[] fields = this.getClass().getDeclaredFields();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < fields.length; i++) {
			Field tempField = fields[i];
			FightingIndexKey ann = tempField.getAnnotation(FightingIndexKey.class);
			if (ann != null) {
				map.put(ann.value(), (Integer) tempField.get(this));
			}
		}
		expectedLevelOfTag = Collections.unmodifiableMap(map);
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getExpectedHeroCount() {
		return expectedHeroCount;
	}
	
	public int getExpectedQuality() {
		return expectedQuality;
	}

	public int getExpectedStar() {
		return expectedStar;
	}
	
	public int getLevelOfTaoist1() {
		return levelOfTaoist1;
	}

	public int getLevelOfTaoist2() {
		return levelOfTaoist2;
	}

	public int getLevelOfTaoist3() {
		return levelOfTaoist3;
	}

	public int getFashionSuitCount() {
		return fashionSuitCount;
	}
	
	public int getFashionWingCount() {
		return fashionWingCount;
	}
	
	public int getFashionPetCount() {
		return fashionPetCount;
	}

	public Map<Integer, Integer> getExpectedLevelOfTag() {
		return expectedLevelOfTag;
	}
}
