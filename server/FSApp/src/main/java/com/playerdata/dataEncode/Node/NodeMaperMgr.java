package com.playerdata.dataEncode.Node;


public class NodeMaperMgr {

	private static NodeMaperMgr instance = new NodeMaperMgr();
	
	public static NodeMaperMgr getInstance(){
		return instance;
	}
	
	final private String armyInfoStr = "ArmyFashion-career;ArmyFashion-gender;ArmyFashion-petId;ArmyFashion-suitId;ArmyFashion-wingId;ArmyHero-attrData;ArmyHero-curAttrData;ArmyHero-fighting;ArmyHero-isPlayer;ArmyHero-position;ArmyHero-roleBaseInfo;ArmyHero-skillList;ArmyInfo-armyFashion;ArmyInfo-armyMagic;ArmyInfo-guildName;ArmyInfo-heroList;ArmyInfo-player;ArmyInfo-playerHeadImage;ArmyInfo-playerName;ArmyMagic-id;ArmyMagic-level;ArmyMagic-modelId;AttrData-addCure;AttrData-attackDistance;AttrData-attackEnergy;AttrData-attackHurt;AttrData-attackSpeed;AttrData-attackVampire;AttrData-critical;AttrData-criticalHurt;AttrData-cutCritHurt;AttrData-cutCure;AttrData-cutHurt;AttrData-dodge;AttrData-doHurt;AttrData-energy;AttrData-energyPerSecond;AttrData-energyReceive;AttrData-energyTrans;AttrData-hardStraight;AttrData-hit;AttrData-life;AttrData-lifeGrowUp;AttrData-lifeReceive;AttrData-moveSpeed;AttrData-pAttackGrowUp;AttrData-physicqueDefGrowUp;AttrData-physiqueAttack;AttrData-physiqueDef;AttrData-reactionTime;AttrData-resist;AttrData-sAttackGrowUp;AttrData-spiritAttack;AttrData-spiritDef;AttrData-spiritDefGrowUp;AttrData-struckEnergy;AttrData-toughness;AttrData-viewRange;AttrData-volumeRadius;CurAttrData-curEnergy;CurAttrData-curLife;CurAttrData-id;CurAttrData-maxEnergy;CurAttrData-maxLife;RoleBaseInfo-careerType;RoleBaseInfo-exp;RoleBaseInfo-id;RoleBaseInfo-level;RoleBaseInfo-modeId;RoleBaseInfo-qualityId;RoleBaseInfo-starLevel;SkillItem-buffId;SkillItem-extraDamage;SkillItem-id;SkillItem-level;SkillItem-order;SkillItem-ownerId;SkillItem-selfBuffId;SkillItem-skillDamage;SkillItem-skillId;SkillItem-skillRate;";
	final private String attrDataStr = "AttrData-life;AttrData-energy;AttrData-physiqueAttack;AttrData-spiritAttack;AttrData-physiqueDef;AttrData-spiritDef;AttrData-attackVampire;AttrData-critical;AttrData-criticalHurt;AttrData-toughness;AttrData-lifeReceive;AttrData-dodge;AttrData-hit;AttrData-energyReceive;AttrData-struckEnergy;AttrData-attackEnergy;AttrData-energyTrans;AttrData-cutHurt;AttrData-cutCritHurt;AttrData-resist;AttrData-addCure;AttrData-cutCure;AttrData-lifeGrowUp;AttrData-pAttackGrowUp;AttrData-sAttackGrowUp;AttrData-physicqueDefGrowUp;AttrData-spiritDefGrowUp;AttrData-energyPerSecond;AttrData-attackSpeed;AttrData-attackHurt;";
	private NodeMaper armyInfo = NodeMaper.fromText(armyInfoStr);
	private NodeMaper attrDataMaper = NodeMaper.fromText(attrDataStr);
	

	public NodeMaper getArmyInfo(){
		return armyInfo;
	}

	public NodeMaper getAttrDataMaper() {
		return attrDataMaper;
	}
	
	
//	private Map<String,NodeMaper> map = new ConcurrentHashMap<String,NodeMaper>();
//	
//	private void init(){
//		URL resource = getClass().getResource("encodeClass.txt");
//		String filePath = resource.getPath();
//		File file = new File(filePath);
//		BufferedReader bf = null;
//		try {
//			bf = new BufferedReader(new FileReader(file));
//			String line = bf.readLine();
//			while(line!=null){
//				parse(line);
//				line = bf.readLine();
//			}
//		} catch (IOException e) {
//			GameLog.error(LogModule.DataEncode, "NodeMaperMgr[init]", "initError", e);
//		}finally{
//			try {
//				if(bf!=null){
//					bf.close();
//				}
//			} catch (IOException e) {
//				GameLog.error(LogModule.DataEncode, "NodeMaperMgr[init]", "initError", e);
//			}
//			
//		}
//	}
//
//	private void parse(String line) {
//		String className = StringUtils.substringBetween(line, "[", "]");
//		String fieldInfo = StringUtils.substringAfter(line, "]");
//		
//		if(StringUtils.isNotBlank(fieldInfo)){
//			NodeMaper nodeMaper = NodeMaper.fromText(fieldInfo);
//			map.put(className, nodeMaper);				
//		}
//		
//	}
//	
//	
//	public NodeMaper get(String className){
//		return map.get(className);
//	}
//	
	

	
}
