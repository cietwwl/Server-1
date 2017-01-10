package com.rwbase.dao.fightinggrowth.pojo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.playerdata.fightinggrowth.FSFightingGrowthWayType;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.itemPrivilege.PrivilegeDescItem;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.fightinggrowth.FightingGrowthTypeTarget;

public class FSUserFightingGrowthTitleCfg {

	private String key;
	private String fightingTitle; // 称号
	private String fightingIcon; // 图标
	private int fightingRequired; // 需要的战斗力
	private String itemRequired; // 晋级需要的道具（原生的字符串）
	private String rewards; // 晋级奖励（原生的字符串）
	private String emailCfgIdOfReward; // 奖励
	private String privilege; // 特权
	private int frameIconId; // 解锁头像框
	private String nextTitleKey; // 下一个title的key
	private boolean isFirst; // 是否第一个title
	private Map<Integer, Integer> _itemRequiredMap; // 通过itemRequired解析过来的{key=道具的cfgId，value=需求的数量}
	private Map<Integer, Integer> _itemRewardMap; // 通过rewards解析过来的{key=道具的cfgId，value=需求的数量}
	private List<ItemInfo> _itemRewardList; // 奖励道具的list
	private List<PrivilegeDescItem> _privItems;	//掉落特权的描述类 
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.BASIC)
	private int heroFighting;	// 英雄基础属性战力期望值
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.HERO_SKILL)
	private int skillFighting; // 英雄技能战力期望值
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.FIX_EQUIP)
	private int fixEquipLevelFighting; // 英雄神器等级战力期望值
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.FIX_EQUIP)
	private int fixEquipQualityFighting; // 英雄神器品质战力期望值
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.FIX_EQUIP)
	private int fixEquipStarFighting; // 英雄神器星级战力期望值
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.TAOIST)
	private int taoistFighting; // 道术战力期望值
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.MAGIC)
	private int magicLevelFighting; // 法宝等级战力期望值
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.MAGIC)
	private int magicQualityFighting; // 法宝品质真理期望值
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.GROUP_SKILL)
	private int groupSkillFighting; // 帮派技能战力期望值
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.GEM)
	private int gemFighting; // 宝石战力期望值
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.FETTERS)
	private int heroFetterFighting; // 英雄羁绊期望战力
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.FETTERS)
	private int magicFetterFighting; // 法宝羁绊期望战力
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.FETTERS)
	private int fixEquipFetterFighting; // 神器羁绊期望战力
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.FASHION)
	private int fashionFighting; // 时装期望战力
	@FightingGrowthTypeTarget(wayType=FSFightingGrowthWayType.SPRITE_ATTACH)
	private int spriteAttachFighting; // 附灵期望战力
	private Map<FSFightingGrowthWayType, Integer> expectedFightingMap; // 期望战力
	private Map<FSFightingGrowthWayType, Integer> expectedFightingMapRO; // 期望战力
	private Map<Integer, Integer> requiredOfItemOnly;
	private Map<eSpecialItemId, Integer> requiredOfCurrency;
	
	public void setItemRequiredMap(Map<Integer, Integer> map) {
		this._itemRequiredMap = Collections.unmodifiableMap(map);
		Map<eSpecialItemId, Integer> currencyMap = new HashMap<eSpecialItemId, Integer>();
		Map<Integer, Integer> itemMap = new HashMap<Integer, Integer>();
		for (Iterator<Integer> keyItr = map.keySet().iterator(); keyItr.hasNext();) {
			Integer key = keyItr.next();
			Integer value = map.get(key);
			if (key < eSpecialItemId.eSpecial_End.getValue()) {
				currencyMap.put(eSpecialItemId.getDef(key), value);
			} else {
				itemMap.put(key, value);
			}
		}
		requiredOfItemOnly = Collections.unmodifiableMap(itemMap);
		requiredOfCurrency = Collections.unmodifiableMap(currencyMap);
	}
	
	public void setItemRewardMap(Map<Integer, Integer> map) {
		this._itemRewardMap = Collections.unmodifiableMap(map);
	}
	
	public void setItemRewardList(List<ItemInfo> list) {
		this._itemRewardList = Collections.unmodifiableList(list);
	}
	
	public void setPrivilegeDescItem(List<PrivilegeDescItem> privItems){
		this._privItems = privItems;
	}
	
	public List<PrivilegeDescItem> getPrivilegeDescItem(){
		return this._privItems;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getFightingTitle() {
		return fightingTitle;
	}
	
	public String getFightingIcon() {
		return fightingIcon;
	}
	
	public int getFightingRequired() {
		return fightingRequired;
	}
	
	public String getNextTitleKey() {
		return nextTitleKey;
	}
	
	public boolean isFirst() {
		return this.isFirst;
	}
	
	/**
	 * 
	 * 获取晋级要求的原生字符串（类似以下这种格式：806501,1）
	 * 
	 * @return
	 */
	public String getItemRequired() {
		return itemRequired;
	}
	
	/**
	 * 
	 * 获取晋级要求的道具列表（key=道具的cfgId，value=需求的数量）
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getItemRequiredMap() {
		return _itemRequiredMap;
	}
	
	/**
	 * 
	 * 获取晋级奖励的原生字符串（类似以下这种格式：806501,1）
	 * 
	 * @return
	 */
	public String getRewards() {
		return rewards;
	}
	
	/**
	 * 
	 * 获取晋级奖励的道具列表（key=道具的cfgId，value=需求的数量）
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getItemRewardMap() {
		return _itemRewardMap;
	}
	
	/**
	 * 
	 * 获取奖励道具的list
	 * 
	 * @return
	 */
	public List<ItemInfo> getItemRewardList() {
		return _itemRewardList;
	}
	
	public String getEmailCfgIdOfReward() {
		return emailCfgIdOfReward;
	}
	
	/**
	 * 
	 * 获取特权
	 * 
	 * @return
	 */
	public String getPrivilege() {
		return privilege;
	}
	
	/**
	 * 获取解锁的头像资源框id
	 * @return
	 */
	public int getFrameIconId() {
		return frameIconId;
	}

	public int getExpectedFightingMap(FSFightingGrowthWayType type) {
		Integer value = expectedFightingMap.get(type);
		if (value != null) {
			return value.intValue();
		}
		return 0;
	}

	public void setExpectedFightingMap(Map<FSFightingGrowthWayType, Integer> expectedFightingMap) {
		this.expectedFightingMap = expectedFightingMap;
		this.expectedFightingMapRO = Collections.unmodifiableMap(expectedFightingMap);
	}
	
	public Map<FSFightingGrowthWayType, Integer> getExpectedFightingMap() {
		return expectedFightingMapRO;
	}

	public Map<Integer, Integer> getRequiredOfItemOnly() {
		return requiredOfItemOnly;
	}

	public Map<eSpecialItemId, Integer> getRequiredOfCurrency() {
		return requiredOfCurrency;
	}

//	public int getHeroFighting() {
//		return heroFighting;
//	}
//
//	public int getSkillFighting() {
//		return skillFighting;
//	}
//
//	public int getFixEquipLevelFighting() {
//		return fixEquipLevelFighting;
//	}
//
//	public int getFixEquipQualityFighting() {
//		return fixEquipQualityFighting;
//	}
//
//	public int getFixEquipStarFighting() {
//		return fixEquipStarFighting;
//	}
//
//	public int getTaoistFighting() {
//		return taoistFighting;
//	}
//
//	public int getMagicLevelFighting() {
//		return magicLevelFighting;
//	}
//
//	public int getMagicQualityFighting() {
//		return magicQualityFighting;
//	}
//
//	public int getGroupSkillFighting() {
//		return groupSkillFighting;
//	}
//
//	public int getGemFighting() {
//		return gemFighting;
//	}
//
//	public int getHeroFetterFighting() {
//		return heroFetterFighting;
//	}
//
//	public int getMagicFetterFighting() {
//		return magicFetterFighting;
//	}
//
//	public int getFixEquipFetterFighting() {
//		return fixEquipFetterFighting;
//	}
//
//	public int getSpriteAttachFighting() {
//		return spriteAttachFighting;
//	}
//
//	public int getFashionFighting() {
//		return fashionFighting;
//	}
}
