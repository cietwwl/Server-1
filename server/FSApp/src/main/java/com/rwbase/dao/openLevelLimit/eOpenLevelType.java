package com.rwbase.dao.openLevelLimit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum eOpenLevelType {
	NONE(0), // 空
	BAG(1), // 背包
	ROLE(2), // 角色
	HERO(3), // 佣兵
	RANKING(5), // 排行榜
	EQUIP_ATTACH(6), // 装备附灵
	TRIAL(7), // 试炼之路-财神爷
	CELETRIAL(8), // 天界之论-生存幻境
	TOWER(9), // 万仙阵-无畏之塔
	EQUIP_INLAY(11), // 装备镶嵌
	MAGIC_FORGE(12), // 法宝锻造
	MAGIC_SMELT(13), // 法宝熔炼
	CAREER(14), // 职业查看
	GOLD_POINT(15), // 点金手
	FRIEND(17), // 好友
	GAMBLE(18),
	EMAIL(19), // 邮件
	DAILY(20), // 日常
	ARENA(21), // 竞技场
	SIGN(22), // 签到
	Magic_Secret(23), // 无尽战火-乾坤幻境
	WORSHIP(24), // 膜拜
	PEAK_ARENA(25), // 巅峰竞技场
	TASK(26), // 任务
	GROUP(27), // 帮派
	SKILL_OPEN(30), // 技能开启
	Player_Wear_Equip(31), // 主角可穿装备等级
	Hero_Wear_Equip(32), // 佣兵可穿装备等级
	HERO_Inlay(33), // 宝石开启等级
	MAIN_CITY(34), // 首页
	SECRET_AREA(35), // 帮派秘境
	SHOP(36), // 商店
	SECRET_SHOP(37), // 神秘商店
	Blackmark_SHOP(38), // 黑市商店
	AUTO_BATTLE(39), // 自动战斗
	FASHION(41), // 时装
	TRIAL2(40), // 炼气山谷
	
	SOUL(42), // 魂石开启
	COPY_SWEEP(43), // 副本扫荡开放
	USE_EXP_ITEM(44), // 使用经验丹
	
	BATTLETOWER(48), // 试炼塔
	TAOIST(52),// 道术
	FIX_EQUIP(53), // 神装
	FIX_EQUIP_STAR(54), // 神装觉醒
	TEAM_BATTLE(56),//组队战，心魔录
	FIX_Exp_EQUIP_4(57), // 左下神装开放等级
	FIX_Exp_EQUIP_5(58), // 右下神装开放等级
	TARGET_SELL(59),//精准营销
	;

	private int order;
	private String orderString;
	
	private static final Map<Integer, eOpenLevelType> _mapOfOrder;

	static {
		eOpenLevelType[] all = values();
		Map<Integer, eOpenLevelType> map = new HashMap<Integer, eOpenLevelType>();
		for (int i = 0; i < all.length; i++) {
			eOpenLevelType temp = all[i];
			map.put(temp.order, temp);
		}
		_mapOfOrder = Collections.unmodifiableMap(map);
	}

	eOpenLevelType(int order) {
		this.order = order;
		this.orderString = String.valueOf(order);
	}
	
	public static eOpenLevelType getByOrder(int order){
//		eOpenLevelType[] lst = eOpenLevelType.values();
//		for (int i = 0; i < lst.length; i++) {
//			eOpenLevelType ty = lst[i];
//			if (ty.getOrder() == order){
//				return ty;
//			}
//		}
//		return null;
		return _mapOfOrder.get(order);
	}

	public int getOrder() {
		return order;
	}

	public String getOrderString() {
		return orderString;
	}
}