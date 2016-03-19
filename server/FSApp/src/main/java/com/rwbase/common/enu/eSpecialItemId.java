package com.rwbase.common.enu;

public enum eSpecialItemId {
	Coin(1, eAttrIdDef.PLAYER_COIN), // 1.金币
	Gold(2, eAttrIdDef.PLAYER_GOLD), // 2.钻石
	Power(3, eAttrIdDef.PLAYER_POWER), // 3.体力
	PlayerExp(4, eAttrIdDef.PLAYER_EXP), // 4.经验
	ArenaCoin(5, eAttrIdDef.PLAYER_ARENA_COIN), // 5.竞技币
	BraveCoin(6, eAttrIdDef.PLAYER_TOWER_COIN), // 无畏之塔币
	GuildCoin(7, eAttrIdDef.PLAYER_GUILD_COIN), // 公会币
	UnendingWarCoin(8, eAttrIdDef.PLAYER_UNENDING_WAR_COIN), // 无尽战火币
	PeakArenaCoin(9, eAttrIdDef.PLAYER_PEAK_ARENA_COIN), // 巅峰刀币
	BATTLE_TOWER_COPPER_KEY(10, null), // 试练塔铜钥匙
	BATTLE_TOWER_SILVER_KEY(11, null), // 试练塔银钥匙
	BATTLE_TOWER_GOLD_KEY(12, null), // 试练塔金钥匙
	eSpecial_End(20, null);

	private final int value;
	private final eAttrIdDef eAttrId;// 临时解决方案：定义两者映射关系

	private eSpecialItemId(int value, eAttrIdDef eAttrId) {
		this.value = value;
		this.eAttrId = eAttrId;
	}

	public int getValue() {
		return this.value;
	}

	public eAttrIdDef geteAttrId() {
		return eAttrId;
	}

	private static eSpecialItemId[] array;
	
	static{
		array = eSpecialItemId.values();
	}
	
	public static eSpecialItemId getDef(int value) {
		for (int i = 0; i < array.length; i++) {
			eSpecialItemId eAttr = array[i];
			if (eAttr.getValue() == value) {
				return eAttr;
			}
		}
		return null;
	}
}
