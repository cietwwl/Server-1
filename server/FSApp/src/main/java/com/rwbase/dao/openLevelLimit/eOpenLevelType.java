package com.rwbase.dao.openLevelLimit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum eOpenLevelType {
	NONE(0,false), // 空
	BAG(1,false), // 背包
	ROLE(2,false), // 角色
	HERO(3,false), // 佣兵
	RANKING(5,false), // 排行榜
	EQUIP_ATTACH(6,false), // 装备附灵
	TRIAL(7,false), // 试炼之路-财神爷
	CELETRIAL(8,false), // 天界之论-生存幻境
	TOWER(9,false), // 万仙阵-无畏之塔
	EQUIP_INLAY(11,false), // 装备镶嵌
	MAGIC_FORGE(12,false), // 法宝锻造
	MAGIC_SMELT(13,false), // 法宝熔炼
	CAREER(14,false), // 职业查看
	GOLD_POINT(15,false), // 点金手
	FRIEND(17,true), // 好友
	GAMBLE(18,false),
	EMAIL(19,false), // 邮件
	DAILY(20,false), // 日常
	ARENA(21,false), // 竞技场
	SIGN(22,false), // 签到
	Magic_Secret(23,false), // 无尽战火-乾坤幻境
	WORSHIP(24,false), // 膜拜
	PEAK_ARENA(25,false), // 巅峰竞技场
	TASK(26,false), // 任务
	GROUP(27,false), // 帮派
	SKILL_OPEN(30,false), // 技能开启
	Player_Wear_Equip(31,false), // 主角可穿装备等级
	Hero_Wear_Equip(32,false), // 佣兵可穿装备等级
	HERO_Inlay(33,false), // 宝石开启等级
	MAIN_CITY(34,false), // 首页
	SECRET_AREA(35,false), // 帮派秘境
	SHOP(36,false), // 商店
	SECRET_SHOP(37,false), // 神秘商店
	Blackmark_SHOP(38,false), // 黑市商店
	AUTO_BATTLE(39,false), // 自动战斗
	FASHION(41,false), // 时装
	TRIAL2(40,false), // 炼气山谷
	
	SOUL(42,false), // 魂石开启
	COPY_SWEEP(43,false), // 副本扫荡开放
	USE_EXP_ITEM(44,false), // 使用经验丹
	
	BATTLETOWER(48,false), // 试炼塔
	TAOIST(52,false),// 道术
	FIX_EQUIP(53,false), // 神装
	FIX_EQUIP_STAR(54,false), // 神装觉醒
	TEAM_BATTLE(56,false),//组队战，心魔录
	FIX_Exp_EQUIP_4(57,false), // 左下神装开放等级
	FIX_Exp_EQUIP_5(58,false), // 右下神装开放等级
	TARGET_SELL(59,false),//精准营销
	
	Sport_Store(62,false),   	//幻境商店
	Arena_Store(63,false),   	//竞技场商店
	Peak_Store(64,false),		//巅峰竞技商店
	Union_Store(65,false),	//帮派商店
	Tower_Store(66,false),	//仙阵商店
	Waken_Store(67,false),	//觉醒商店
	Maigc_Upgrade(68,false),  //法宝进化
	Magic_Smelt(69,false), 	//法宝熔炼
	MainMsg(70,false),		//跑马灯
	;

	private int order;
	private String orderString;
	private boolean isTigger;
	
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

	eOpenLevelType(int order,boolean isTigger) {
		this.order = order;
		this.orderString = String.valueOf(order);
		this.isTigger = isTigger;
	}
	
	public static eOpenLevelType getByOrder(int order){
		return _mapOfOrder.get(order);
	}

	public int getOrder() {
		return order;
	}

	public String getOrderString() {
		return orderString;
	}

	public boolean isTigger() {
		return isTigger;
	}

	public void setTigger(boolean isTigger) {
		this.isTigger = isTigger;
	}

	public static Map<Integer, eOpenLevelType> getMapoforder() {
		return _mapOfOrder;
	}
	
	
	
}