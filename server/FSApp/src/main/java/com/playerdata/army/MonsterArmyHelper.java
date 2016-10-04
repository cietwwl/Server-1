package com.playerdata.army;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.common.beanCopy.FastBeanCopyer;
import com.playerdata.battleVerify.MonsterCfg;
import com.playerdata.battleVerify.MonsterCfgDao;
import com.playerdata.hero.core.RoleBaseInfo;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.AttrData.Builder;
import com.rwbase.dao.skill.pojo.SkillHelper;
import com.rwbase.dao.skill.pojo.SkillItem;

public class MonsterArmyHelper {

	public static ArmyHero buildMonster(String monsterId) {
		MonsterCfg monster = MonsterCfgDao.getInstance().getConfig(monsterId);

		ArmyHero hero = new ArmyHero();
		AttrData attrData = getAttrData(monster);
		hero.setAttrData(attrData);

		List<SkillItem> skillList = getSkillList(monster);
		if (skillList != null) {
			hero.setSkillList(skillList);
		}

		RoleBaseInfo roleBaseInfo = getRoleBaseInfo(monster);
		if (roleBaseInfo != null) {
			hero.setRoleBaseInfo(roleBaseInfo);
		}

		hero.setPlayer(false);
		return hero;
	}

	private static AttrData getAttrData(MonsterCfg monster) {
		AttrData.Builder builder = new Builder();
		AttrData data = builder.build();
		FastBeanCopyer.getInstance().copy(monster, data);
		return data;
	}

	private static List<SkillItem> getSkillList(MonsterCfg monster){
        List<String> tempList = new ArrayList<String>();
        if(StringUtils.isNotBlank(monster.getSkill01())){
        	tempList.add(monster.getSkill01());
        }
        if(StringUtils.isNotBlank(monster.getSkill02())){
        	tempList.add(monster.getSkill02());
        }
        if(StringUtils.isNotBlank(monster.getSkill03())){
        	tempList.add(monster.getSkill03());
        }
        if(StringUtils.isNotBlank(monster.getSkill04())){
        	tempList.add(monster.getSkill04());
        }
        if(StringUtils.isNotBlank(monster.getSkill05())){
        	tempList.add(monster.getSkill05());
        }
        
		return createSkillItems(tempList);
	}

	public static List<SkillItem> createSkillItems(List<String> ids) {
		List<SkillItem> skillList = SkillHelper.getSkillList(ids, null, null);
		SkillHelper.checkAllSkill(skillList);
		return skillList;
	}

	private static RoleBaseInfo getRoleBaseInfo(MonsterCfg monster) {
		RoleBaseInfo roleInfo = new RoleBaseInfo();
		roleInfo.setId(monster.getId());
		roleInfo.setTemplateId(monster.getId());
		roleInfo.setModeId(monster.getModeId());
		return roleInfo;
	}
	
	
}