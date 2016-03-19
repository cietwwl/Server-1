package com.rwbase.common.enu;


public enum eAttrIdDef {
	BATTLE_BEGIN(100),
	
	/**基础生命值...*/
	LIFE(110), 						
	/**当前生命值*/
	CURRENTL_LIFE(111),
	/** 能量值*/
	ENERGY(120), 
	CURRENT_ENERGY(121),
	/**攻击..*/
	ATTACK(130),					
	/**体魄防御...*/
	PHYSIQUE_DEF(140),
	/**精神防御...*/
	SPIRIT_DEF(150), 				
	/**攻击吸血...*/
	ATTACK_VAMPIRE(160),
	/**暴击率...*/
	CRITICAL(170), 				
	/**暴击伤害提升...*/
	CRITICAL_HURT(180), 			
	/**韧性...*/
	TOUGHNESS(190), 
	/**生命回复...*/
	LIFE_RECEIVE(200), 	
	/**躲闪*/
    DODGE(201), 
    /**命中*/
    HIT(202),
	
	/**能量回复...*/
	ENERGY_RECEIVE(210),
	/**击杀回复能量...*/
	STRUCK_ENERGY_RECEIVE(220),	
	/**攻击回复能量...*/
	ATTACK_ENERGY_RECEIVE(230),
	/**能量转化...*/
	ENERGY_TRANS(240),	
	/**攻击距离...*/
	ATTACK_DISTANCE(250),	
	/**攻击速度...*/
	ATTACK_SPEED(260),
	/**攻击频率...*/
	ATTACK_FREQUENCE(270),
	/** 移动速度...*/
	MOVE_SPEED(280),
	/**伤害增加*/
	ATTACK_HURT_ADD(290),	
	
	/**伤害减免*/
	CUT_HURT(300),					
	/**暴击伤害减免*/
	CUT_CRIT_HURT(310),				
	/**抵抗*/
	RESIST(320),					
	/**受到治疗效果增加*/
	ADD_CURE(330),					
	/**受到治疗效果减少*/
	CUT_CURE(340),	
	/**反应时间*/
	REACTION_TIME(350),
	/**硬直阀值*/
	HARD_STRAIGHT(360),
	/**反弹伤害*/
	DAMAGE_REFLECT(370),
	/**护甲穿透*/
	ARMOR_PENTRATION(380),
	
	
	GROWUP_BEGIN(450),
	/**生命成长*/
	LIFE_GROWUP(451),
	/**攻击成长*/
	ATTACK_GROWUP(452),
	/**体魄防御成长*/
	PHYSICQUE_DEF_GROWUP(453), 		
	/**精神防御成长*/
	SPIRIT_DEF_GROWUP(454),
	GROWUP_END(455),
	BATTLE_END(459),
	
	
	/**提供附灵经验...*/
	ENCHANTEXP(460),
	/**技能总等级*/
	SKILL_LEVEL(461),				
	/**攻击类型*/
	ATTACK_TYPE(462),	
	/**法宝总等级*/
	MAGIC_LEVEL(463),	
	/**宝石*/
	ROLE_INLAY(464),
	
	
	
	PLAYER_GOLD(510),
	PLAYER_POWER(520),
	PLAYER_BUY_COIN_TIMES(530),
	PLAYER_CAREER(540),
	PLAYER_COIN(550),
	PLAYER_EXP(560),
	PLAEYR_FIGHTING(570),
	PLAYER_FREE_CHAT(580),
	PLAYER_HEAD_IMAGE(590),
	PLAYER_LAST_CHANGE_INFO_TIME(600),
	PLAYER_LEVEL(610),
	PLAYER_MODEL_ID(620),
	PLAYER_NICK(630),
	PLAYER_ROOKIE_FLAG(640),
	PLAYER_SEX(650),
	PLAYER_TEMPLATE_ID(660),
	UPGRADE_EXP(670),
	PLAYER_ATTACK_ID(680),
	PLAYER_USER_ID(690),
	START_LEVEL(700),
	PLAYER_VIP(710),
	PLAYER_RECHARGE(720),
	PLAYER_BUY_HERO_SKILL_TIMES(730),
	PLAYER_HERO_SKILL_POINT(740),
	PLAYER_POTENTIAL(750),
	PLAYER_HEAD_BOX(760),
	PLAYER_HERO_SKILL_TIME(770),
	PLAYER_UNENDING_WAR_COIN(780),
	PLAYER_TOWER_COIN(790),
	/**公会id*/
	PLAYER_GUILDID(810),

	PLAYER_GUILD_COIN(800),
	/**公会名字*/
	PLAYER_GUILDNAME(820),
	PLAYER_PEAK_ARENA_COIN(840),
	/** 竞技场货币 **/
	PLAYER_ARENA_COIN(850),
	HERO_TEMPLATE_ID(1010),
	HERO_LEVEL(1020),
	HERO_QUALITY_ID(1040),
	HERO_EXP(1050),
	HERO_FIGHTING(1060);

	private int order;
	eAttrIdDef(int order){
		this.order = order;
	}
	public int getOrder() {
		return order;
	}
	
	public static eAttrIdDef getDef(int value){
		eAttrIdDef eAttr = null;
		for (int i = 0; i < eAttrIdDef.values().length; i++) {
			eAttr = eAttrIdDef.values()[i];
			if(eAttr.getOrder() == value){
				break;
			}
		}
		return eAttr;
	}
}


