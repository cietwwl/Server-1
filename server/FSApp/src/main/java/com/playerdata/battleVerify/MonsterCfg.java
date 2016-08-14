package com.playerdata.battleVerify;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 怪物模版 没有完全读取所有怪物属性
 * 
 * @author Allen
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonsterCfg {

	private String Id; // id...
	private String name; // 怪物名称...
	private int type; // 类别...
	private int mirrorModel; // 是否镜像...
	private float volumeRadius; // 体积半径...
	private String description; // 怪物描述...
	private int modeId; // 模型id...
	private int Enter; // 入场技能Id...
	private int Die; // 死亡技能Id...
	private String Normal; // 普通攻击Id...
	private String Skill01; // 必杀技01...
	private String Skill02; // 自动技能02...
	private String Skill03; // 自动技能03...
	private String Skill04; // 自动技能04...
	private String Skill05; // 自动技能05...
	private String Chartlet; // 贴图Id...
	private String headID; // 头像Id...
	private float scale; // 缩放...
	private int level; // 怪物等级...
	private int attackType; // 攻击类型...
	private float viewRange; // 视野范围...
	private float moveSpeed; // 移动速度...
	private int AttackRange; // 攻击距离...
	private float reactionTime; // 反应时间...
	private float doHurt; // 硬直阀值...
	private float hardStraight; // 硬直阀值...
	private int life; // 生命...
	private int Attack; // 攻击...
	private int physiqueDef; // 物理防御...
	private int spiritDef; // 法术防御...
	private int HpCount; // 血条数量...
	private int AIGroupId; // ai组id...
	private int energy; // 基础能量...
	private int energyReceive; // 能量回复...
	private int energyPerSecond; // 每秒回复能量...
	private int energyTrans; // 能量转化...
	private String energyTransFactor; // 能量转化系数...
	private int attackEnergy; // 攻击增加能量...
	private int beatenEnergy; // 攻击目标，目标增加的能量...
	private int struckEnergy; // 击杀增加能量...
	private String drop; // 掉落ID...
	private String qualityId; // 品质...
	private int starLevel; // 星级...
	private String imageName; // 头像...
	private float proportion; // 宝箱总比例...
	private int weight; // 怪物权重...
	private int copyId; // 副本Id...
	private String shaderAttri; // shader属性...
	public String getId() {
		return Id;
	}
	public String getName() {
		return name;
	}
	public int getType() {
		return type;
	}
	public int getMirrorModel() {
		return mirrorModel;
	}
	public float getVolumeRadius() {
		return volumeRadius;
	}
	public String getDescription() {
		return description;
	}
	public int getModeId() {
		return modeId;
	}
	public int getEnter() {
		return Enter;
	}
	public int getDie() {
		return Die;
	}
	public String getNormal() {
		return Normal;
	}
	public String getSkill01() {
		return Skill01;
	}
	public String getSkill02() {
		return Skill02;
	}
	public String getSkill03() {
		return Skill03;
	}
	public String getSkill04() {
		return Skill04;
	}
	public String getSkill05() {
		return Skill05;
	}
	public String getChartlet() {
		return Chartlet;
	}
	public String getHeadID() {
		return headID;
	}
	public float getScale() {
		return scale;
	}
	public int getLevel() {
		return level;
	}
	public int getAttackType() {
		return attackType;
	}
	public float getViewRange() {
		return viewRange;
	}
	public float getMoveSpeed() {
		return moveSpeed;
	}
	public int getAttackRange() {
		return AttackRange;
	}
	public float getReactionTime() {
		return reactionTime;
	}
	public float getDoHurt() {
		return doHurt;
	}
	public float getHardStraight() {
		return hardStraight;
	}
	public int getLife() {
		return life;
	}
	public int getAttack() {
		return Attack;
	}
	public int getPhysiqueDef() {
		return physiqueDef;
	}
	public int getSpiritDef() {
		return spiritDef;
	}
	public int getHpCount() {
		return HpCount;
	}
	public int getAIGroupId() {
		return AIGroupId;
	}
	public int getEnergy() {
		return energy;
	}
	public int getEnergyReceive() {
		return energyReceive;
	}
	public int getEnergyPerSecond() {
		return energyPerSecond;
	}
	public int getEnergyTrans() {
		return energyTrans;
	}
	public String getEnergyTransFactor() {
		return energyTransFactor;
	}
	public int getAttackEnergy() {
		return attackEnergy;
	}
	public int getBeatenEnergy() {
		return beatenEnergy;
	}
	public int getStruckEnergy() {
		return struckEnergy;
	}
	public String getDrop() {
		return drop;
	}
	public String getQualityId() {
		return qualityId;
	}
	public int getStarLevel() {
		return starLevel;
	}
	public String getImageName() {
		return imageName;
	}
	public float getProportion() {
		return proportion;
	}
	public int getWeight() {
		return weight;
	}
	public int getCopyId() {
		return copyId;
	}
	public String getShaderAttri() {
		return shaderAttri;
	}
	
	
	
	
}
