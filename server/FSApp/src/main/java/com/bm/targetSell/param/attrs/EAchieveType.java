package com.bm.targetSell.param.attrs;

import com.common.HPCUtil;

public enum EAchieveType {
	AcheiveLevel(1, new AchieveLevel()),
	AcheiveVipLevel(2, new AchieveVipLevel()),
	AcheiveCharge(3, new AchieveCharge()),
	AcheiveTeamPower(4, new AchieveTeamPower()),
	AcheiveAllPower(5, new AchieveAllPower()),
	AcheiveCreateTime(6, new AchieveCreateTime()),
	AcheiveLastLoginTime(7, new AchieveLastLoginTime()),
	AcheiveCoin(8, new AchieveCoin()),
	AcheivePower(9, new AchievePower()),
	AcheiveQuality(10, new AchieveQuality()),
	AcheiveCarrer(11, new AchieveCarrer()),
	AcheiveStar(12, new AchieveStar()),
	AchieveveHeroFixEquipUpgradStar(13, new AchieveveHeroFixEquipUpgradStar()),
	AchieveStoneType(14, new AchieveStoneAttrValue()),
	AchieveStoneLevel(15, new AchieveStoneAttrValue()),
	AchieveMagicUpgradLv(16, new AchieveMagicUpgradLv()),//角色某一类法宝的最高进化等级
	AchieveMagicLevel(17, new AchieveMagicLevel()),//角色某一类法宝的最高等级
	AchieveItemCount(18, new AchieveItemCount()),//检查某个道具数量
	AchieveSpriteAttach(19, new AchieveSpriteAttach()),//英雄附灵等级
	;
	
	private int id;
	private AbsAchieveAttrValue instance;
	private EAchieveType(int id, AbsAchieveAttrValue instance){
		this.id = id;
		this.instance = instance;
	}
	public int getId() {
		return id;
	}
	
	private static EAchieveType[] array;
	
	static{
		EAchieveType[] temp = EAchieveType.values();
		Object[] copy = HPCUtil.toMappedArray(temp, "id");
		array = new EAchieveType[copy.length];
		HPCUtil.copy(copy, array);
	}
	
	public static EAchieveType getAchieveType(int type){
		return array[type];
	}
	public AbsAchieveAttrValue getInstance() {
		return instance;
	}
}
