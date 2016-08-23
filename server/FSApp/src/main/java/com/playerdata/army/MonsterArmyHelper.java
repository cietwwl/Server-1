package com.playerdata.army;

import java.util.ArrayList;
import java.util.List;

import com.common.BeanCopyer;
import com.playerdata.battleVerify.MonsterCfg;
import com.playerdata.battleVerify.MonsterCfgDao;
import com.playerdata.hero.core.RoleBaseInfoImpl;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.AttrData.Builder;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.skill.pojo.SkillHelper;

public class MonsterArmyHelper {

	public static ArmyHero buildMonster(String monsterId) {
		MonsterCfg monster = MonsterCfgDao.getInstance().getConfig(monsterId);

		ArmyHero hero = new ArmyHero();
		AttrData attrData = getAttrData(monster);
		hero.setAttrData(attrData);
		;

		List<Skill> skillList = getSkillList(monster);
		if (skillList != null) {
			hero.setSkillList(skillList);
		}

		RoleBaseInfoImpl roleBaseInfo = getRoleBaseInfo(monster);
		if (roleBaseInfo != null) {
			hero.setRoleBaseInfo(roleBaseInfo);
		}

		// hero.MonsterPosition = getPosition(monster);

		hero.setPlayer(false);
		return hero;
	}

	private static AttrData getAttrData(MonsterCfg monster) {
		AttrData.Builder builder = new Builder();
		AttrData data = builder.build();
		BeanCopyer.copy(monster, data);
		return data;
	}

	private static List<Skill> getSkillList(MonsterCfg monster) {
		List<String> tempList = new ArrayList<String>();
		tempList.add(monster.getSkill01());
		tempList.add(monster.getSkill02());
		tempList.add(monster.getSkill03());
		tempList.add(monster.getSkill04());
		tempList.add(monster.getSkill05());

		return createSkillItems(tempList);

	}

	public static List<Skill> createSkillItems(List<String> ids) {
		List<Skill> skillList = SkillHelper.getSkillList(ids, null, null);
		SkillHelper.checkAllSkill(skillList);
		return skillList;
		// List<Skill> skillItemList = new ArrayList<Skill>();
		// SkillCfg skillCfg = null;
		// Map<String, List<Integer>> skillBuffs = new HashMap<String, List<Integer>>();
		// Map<String, List<Integer>> selfBuffs = new HashMap<String, List<Integer>>();
		// // 遍历技能和Buff的关系
		// for (String skillIdTmp : ids) {
		// skillCfg = SkillCfgDAO.getInstance().getCfg(skillIdTmp);
		// parseSkillBuffs(skillCfg.getBuffId(), skillBuffs);
		// parseSkillBuffs(skillCfg.getSelfBuffId(), selfBuffs);
		// }
		//
		// // 绑定技能和BUFF的关系
		// int order = 0;
		// for (String skillIdTmp : ids) {
		// if (StringUtils.isNotBlank(skillIdTmp)) {
		// skillCfg = SkillCfgDAO.getInstance().getCfg(skillIdTmp);
		// List<Integer> buffIds = new ArrayList<Integer>();
		// String skillEffectId = skillCfg.getSkillEffectId();
		// if (skillBuffs.containsKey(skillEffectId)) {
		// buffIds.addAll((skillBuffs.get(skillEffectId)));
		// }
		// List<Integer> selfbuffIds = new ArrayList<Integer>();
		// if (selfBuffs.containsKey(skillEffectId)) {
		// selfbuffIds.addAll(selfBuffs.get(skillEffectId));
		// }
		// Skill skillItem = newSkill(skillIdTmp, order, buffIds, skillCfg, selfbuffIds);
		// skillItemList.add(skillItem);
		// }
		// order++;
		// }
		// return skillItemList;
	}

	// private static Skill newSkill(String id, int order, List<Integer> buffIds, SkillCfg skillCfg, List<Integer> selfbuffIds) {
	// Skill skill = new Skill();
	// skill.setId(id);
	// skill.setOwnerId(id);
	// skill.setSkillId(id);
	// skill.setLevel(1);
	// skill.setOrder(order);
	// skill.setSkillRate(skillCfg.getSkillRate());
	// skill.setExtraDamage(skillCfg.getExtraDamage());
	// skill.setSkillDamage(skillCfg.getSkillDamage());
	//
	// // 这里已经不再需要这个Buff了
	// skill.setBuffId(buffIds);
	// skill.setSelfBuffId(selfbuffIds);
	// return skill;
	// }
	//
	// private static void parseSkillBuffs(String id, Map<String, List<Integer>> skillBuffs) {
	// String[] tempBuffarr = id.split(";");
	// for (int k = 0; k < tempBuffarr.length; k++) {
	// String[] buffarr = tempBuffarr[k].split("_");
	// if (buffarr.length > 0) {
	// List<Integer> buffId = new ArrayList<Integer>();
	// String skillEffectId = buffarr[0];
	// for (int j = 1; j < buffarr.length; j++) {
	// int buffid = Integer.parseInt((buffarr[j]));
	// if (!buffId.contains(buffid) && buffid != 0) {
	// buffId.add(buffid);
	// }
	// }
	//
	// if (buffId.size() > 0) {
	// if (skillBuffs.containsKey(skillEffectId)) {
	// skillBuffs.get(skillEffectId).addAll(buffId);
	// } else {
	// skillBuffs.put(skillEffectId, buffId);
	// }
	// }
	// }
	// }
	// }

	private static RoleBaseInfoImpl getRoleBaseInfo(MonsterCfg monster) {
		RoleBaseInfoImpl roleInfo = new RoleBaseInfoImpl();
		BeanCopyer.copy(monster, roleInfo);
		return roleInfo;
	}

}
