package com.bm.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.util.StringUtils;

public class RobotEntryCfg {

	private final int ranking; // 排名
	private final int[] level; // 主角等级
	private final int[] quality; // 主角品质
	private final int[] star; // 主角星级
	private final int[] equipments; // 主角装备数量
	private final int[] enchant; // 主角附灵星级
	private final int[] gemCount; // 主角宝石数量
	private final int[] gemType; // 主角宝石种类
	private final int[] gemLevel; // 主角宝石等级
	private final int[] firstSkillLevel;// 第一个技能等级
	private final int[] secondSkillLevel;// 第二个技能等级
	private final int[] thirdSkillLevel; // 第三个技能等级
	private final int[] fourthSkillLevel;// 第四个技能等级
	private final int[] fifthSkillLevel; // 第五个技能等级
	private final List<String> fashions; // 主角时装
	private final int[] vipLevel; // 主角vip等级
	private final int[] magicId; // 主角法宝id
	private final int[] magicLevel; // 法宝技能等级
	private final List<String> heroGroupId; // 佣兵组合ID
	private final int[] heroLevel; // 佣兵等级
	private final int[] heroQuality; // 佣兵品质
	private final int[] heroStar; // 佣兵星级
	private final int[] heroEquipments; // 佣兵装备
	private final int[] heroEnchant; // 佣兵附灵
	private final int[] heroGemCount; // 佣兵宝石数量
	private final int[] heroGemType; // 佣兵宝石种类
	private final int[] heroGemLevel; // 佣兵宝石等级
	private final int[] heroFirstSkillLevel; // 佣兵第一个技能等级
	private final int[] heroSecondSkillLevel;// 佣兵第二个技能等级
	private final int[] heroThirdSkillLevel; // 佣兵第三个技能等级
	private final int[] heroFourthSkillLevel;// 佣兵第四个技能等级
	private final int[] heroFifthSkillLevel; // 佣兵第五个技能等级
	// 增加的机器人数据
	private final int[] fixEquipLevel;// 神器的等级
	private final int[] fixEquipQuality;// 神器的品质
	private final int[] fixEquipStar;// 神器的星数
	private final List<int[]> taoistLevel;// 道术的等级
	private final Map<String, int[]> heroFetters;// 羁绊属性的
	private final int extraAttrId;// 额外的属性Id
	private final int minLimitValue;// 低限制值
	private final int maxLimitValue;// 高限制值

	public RobotEntryCfg(int ranking, RobotCfg cfg) {
		this.ranking = ranking;
		this.level = parseIntArray(cfg.getLevel());
		this.quality = parseIntArray(cfg.getQuality());
		this.star = parseIntArray(cfg.getStar());
		this.equipments = parseIntArray(cfg.getEquipments());
		this.enchant = parseIntArray(cfg.getEnchant());
		this.gemCount = parseIntArray(cfg.getGemCount());
		this.gemType = parseIntArray(cfg.getGemType()); // 主角宝石种类
		this.gemLevel = parseIntArray(cfg.getGemLevel()); // 主角宝石等级
		this.firstSkillLevel = parseIntArray(cfg.getFirstSkillLevel());// 第一个技能等级
		this.secondSkillLevel = parseIntArray(cfg.getSecondSkillLevel());// 第二个技能等级
		this.thirdSkillLevel = parseIntArray(cfg.getThirdSkillLevel()); // 第三个技能等级
		this.fourthSkillLevel = parseIntArray(cfg.getFourthSkillLevel());// 第四个技能等级
		this.fifthSkillLevel = parseIntArray(cfg.getFifthSkillLevel()); // 第五个技能等级
		this.fashions = parseStringList(cfg.getFashions()); // 主角时装
		this.vipLevel = parseIntArray(cfg.getVipLevel()); // 主角vip等级
		this.magicId = parseIntArray(cfg.getMagicId()); // 主角法宝id
		this.magicLevel = parseIntArray(cfg.getMagicLevel()); // 法宝技能等级
		this.heroGroupId = parseStringList(cfg.getHeroGroupId()); // 佣兵组合ID
		this.heroLevel = parseIntArray(cfg.getHeroLevel()); // 佣兵等级
		this.heroQuality = parseIntArray(cfg.getHeroQuality()); // 佣兵品质
		this.heroStar = parseIntArray(cfg.getHeroStar()); // 佣兵星级
		this.heroEquipments = parseIntArray(cfg.getHeroEquipments()); // 佣兵装备
		this.heroEnchant = parseIntArray(cfg.getHeroEnchant()); // 佣兵附灵
		this.heroGemCount = parseIntArray(cfg.getHeroGemCount()); // 佣兵宝石数量
		this.heroGemType = parseIntArray(cfg.getHeroGemType()); // 佣兵宝石种类
		this.heroGemLevel = parseIntArray(cfg.getHeroGemLevel());// 佣兵宝石等级
		this.heroFirstSkillLevel = parseIntArray(cfg.getHeroFirstSkillLevel()); // 佣兵第一个技能等级
		this.heroSecondSkillLevel = parseIntArray(cfg.getHeroSecondSkillLevel());// 佣兵第二个技能等级
		this.heroThirdSkillLevel = parseIntArray(cfg.getHeroThirdSkillLevel()); // 佣兵第三个技能等级
		this.heroFourthSkillLevel = parseIntArray(cfg.getHeroFourthSkillLevel());// 佣兵第四个技能等级
		this.heroFifthSkillLevel = parseIntArray(cfg.getHeroFifthSkillLevel()); // 佣兵第五个技能等级
		// 增加的机器人数据
		this.fixEquipLevel = parseIntArray(cfg.getFixEquipLevel());
		this.fixEquipQuality = parseIntArray(cfg.getFixEquipQuality());
		this.fixEquipStar = parseIntArray(cfg.getFixEquipStar());
		this.extraAttrId = cfg.getExtraAttrId();

		// 道术
		String taoistLevelStr = cfg.getTaoistLevel();
		if (!StringUtils.isEmpty(taoistLevelStr)) {
			String[] arr = taoistLevelStr.split(",");
			int len = arr.length;

			List<int[]> list = new ArrayList<int[]>(len);
			for (int i = 0; i < len; i++) {
				list.add(parseIntArray(arr[i]));
			}

			this.taoistLevel = Collections.unmodifiableList(list);
		} else {
			this.taoistLevel = Collections.emptyList();
		}

		// 羁绊信息
		String heroFettersStr = cfg.getHeroFetters();
		if (!StringUtils.isEmpty(heroFettersStr)) {
			String[] arr = heroFettersStr.split(";");
			int len = arr.length;

			Map<String, int[]> map = new HashMap<String, int[]>(len);
			for (int i = 0; i < len; i++) {
				String[] temp = arr[i].split(":");
				if (temp.length < 2) {
					continue;
				}

				map.put(temp[0], parseIntArray(temp[1]));
			}

			this.heroFetters = Collections.unmodifiableMap(map);
		} else {
			this.heroFetters = Collections.emptyMap();
		}

		// 限定值范围
		int[] limitValueArr = parseIntArray(cfg.getLimitValue());
		if (limitValueArr.length < 2) {
			minLimitValue = maxLimitValue = limitValueArr[0];
		} else {
			minLimitValue = limitValueArr[0];
			maxLimitValue = limitValueArr[1];
		}
	}

	private int[] parseIntArray(String text) {
		if (text == null || text.isEmpty()) {
			int[] zero = new int[1];
			zero[0] = 0;
			return zero;
		}
		int[] array = parseIntList(text, "~");
		if (array == null) {
			array = parseIntList(text, ",");
			if (array == null) {
				array = new int[1];
				array[0] = Integer.parseInt(text);
			}
		}
		return array;
	}

	private ArrayList<String> parseStringList(String text) {
		if (text == null || text.isEmpty()) {
			ArrayList<String> list = new ArrayList<String>(1);
			list.add("0");
			return list;
		}
		ArrayList<String> list = parseStringList(text, "~");
		if (list == null) {
			list = parseStringList(text, ",");
			if (list == null) {
				list = new ArrayList<String>(1);
				list.add(text);
			}
		}
		return list;
	}

	private ArrayList<String> parseStringList(String text, String split) {
		if (!text.contains(split)) {
			return null;
		}
		ArrayList<String> result = new ArrayList<String>();
		StringTokenizer token = new StringTokenizer(text, split);
		while (token.hasMoreTokens()) {
			result.add(token.nextToken());
		}
		return result;
	}

	private int[] parseIntList(String text, String split) {
		ArrayList<String> result = parseStringList(text, split);
		if (result == null) {
			return null;
		}
		int size = result.size();
		int[] array = new int[size];
		for (int i = 0; i < size; i++) {
			array[i] = Integer.parseInt(result.get(i));
		}
		return array;
	}

	public int getRanking() {
		return ranking;
	}

	public int[] getLevel() {
		return level;
	}

	public int[] getQuality() {
		return quality;
	}

	public int[] getStar() {
		return star;
	}

	public int[] getEquipments() {
		return equipments;
	}

	public int[] getEnchant() {
		return enchant;
	}

	public int[] getGemCount() {
		return gemCount;
	}

	public int[] getGemType() {
		return gemType;
	}

	public int[] getGemLevel() {
		return gemLevel;
	}

	public int[] getFirstSkillLevel() {
		return firstSkillLevel;
	}

	public int[] getSecondSkillLevel() {
		return secondSkillLevel;
	}

	public int[] getThirdSkillLevel() {
		return thirdSkillLevel;
	}

	public int[] getFourthSkillLevel() {
		return fourthSkillLevel;
	}

	public int[] getFifthSkillLevel() {
		return fifthSkillLevel;
	}

	public List<String> getFashions() {
		return fashions;
	}

	public int[] getVipLevel() {
		return vipLevel;
	}

	public int[] getMagicId() {
		return magicId;
	}

	public int[] getMagicLevel() {
		return magicLevel;
	}

	public List<String> getHeroGroupId() {
		return heroGroupId;
	}

	public int[] getHeroLevel() {
		return heroLevel;
	}

	public List<Integer> getHeroLevel(int maxLevel) {
		List<Integer> levelList = new ArrayList<Integer>();
		for (int levelTmp : heroLevel) {
			if (levelTmp <= maxLevel) {
				levelList.add(levelTmp);
			}
		}

		return levelList;
	}

	public int[] getHeroQuality() {
		return heroQuality;
	}

	public int[] getHeroStar() {
		return heroStar;
	}

	public int[] getHeroEquipments() {
		return heroEquipments;
	}

	public int[] getHeroEnchant() {
		return heroEnchant;
	}

	public int[] getHeroGemCount() {
		return heroGemCount;
	}

	public int[] getHeroGemType() {
		return heroGemType;
	}

	public int[] getHeroGemLevel() {
		return heroGemLevel;
	}

	public int[] getHeroFirstSkillLevel() {
		return heroFirstSkillLevel;
	}

	public int[] getHeroSecondSkillLevel() {
		return heroSecondSkillLevel;
	}

	public int[] getHeroThirdSkillLevel() {
		return heroThirdSkillLevel;
	}

	public int[] getHeroFourthSkillLevel() {
		return heroFourthSkillLevel;
	}

	public int[] getHeroFifthSkillLevel() {
		return heroFifthSkillLevel;
	}

	public int[] getFixEquipLevel() {
		return fixEquipLevel;
	}

	public int[] getFixEquipQuality() {
		return fixEquipQuality;
	}

	public int[] getFixEquipStar() {
		return fixEquipStar;
	}

	public List<int[]> getTaoistLevel() {
		return taoistLevel;
	}

	public Map<String, int[]> getHeroFetters() {
		return heroFetters;
	}

	public int getExtraAttrId() {
		return extraAttrId;
	}

	public int getMinLimitValue() {
		return minLimitValue;
	}

	public int getMaxLimitValue() {
		return maxLimitValue;
	}
}