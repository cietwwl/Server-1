package com.rwbase.dao.skill.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.playerdata.SkillMgr;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwproto.SkillServiceProtos.TagSkillData;

public class SkillHelper {
	public static String getItemId(String ownerId, String skillCfgId) {

		return ownerId + "_" + skillCfgId;
	}

	public static List<TagSkillData> getSkillProtoList(List<Skill> skillLIst) {
		if (skillLIst == null)
			return null;
		List<TagSkillData> list = new ArrayList<TagSkillData>();
		Collections.sort(skillLIst, new Comparator<Skill>() {
			public int compare(Skill o1, Skill o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});
		for (Skill skill : skillLIst) {
			TagSkillData.Builder tagSkill = TagSkillData.newBuilder();
			tagSkill.setLevel(skill.getLevel());
			tagSkill.setSkillId(skill.getSkillId());
			tagSkill.setOrder(skill.getOrder());
			tagSkill.setExtraDamage(skill.getExtraDamage());
			tagSkill.setSkillRate(skill.getSkillRate());
			for (Integer buff : skill.getBuffId()) {
				tagSkill.addBuffId(buff);
			}

			list.add(tagSkill.build());
		}
		return list;
	}

	/**
	 * 初始化技能列表
	 * 
	 * @param rolecfg
	 * @param qualityId
	 * @param playerLevel
	 * @return
	 */
	public static List<Skill> initSkill(RoleCfg rolecfg, String qualityId, int playerLevel) {
		List<Skill> cfgSkillList = RoleCfgDAO.getInstance().getSkill(rolecfg.getRoleId());
		// 技能buff有相关性，要先一次过加入到列表才行
		List<Skill> battleSkillList = new ArrayList<Skill>();
		for (Skill skilltmp : cfgSkillList) {
			SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(skilltmp.getSkillId());
			if (cfg == null) {
				if (skilltmp.getLevel() != SkillMgr.DIE_SKILL_LEVEL) {
				}
			} else {
				battleSkillList.add(skilltmp);
			}
		}

		for (Skill pSkill : battleSkillList) {
			SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(pSkill.getSkillId());

			int quality = RoleQualityCfgDAO.getInstance().getQuality(qualityId);
			int level = cfg.getRoleLevel() <= playerLevel && cfg.getRoleQuality() <= quality ? 1 : 0;
			pSkill.setLevel(level);
		}

		checkAllSkill(battleSkillList);
		return battleSkillList;
	}

	/**
	 * 重置掉技能伤害，技能对敌Buff和SelfBuff，技能额外附加伤害，以及附加百分比
	 * 
	 * @param skillList
	 */
	public static void checkAllSkill(List<Skill> skillList) {
		SkillCfgDAO cfgDAO = SkillCfgDAO.getInstance();
		// 相互影响的伤害值
		for (int i = skillList.size() - 1; i >= 0; --i) {
			Skill skillInfo = skillList.get(i);
			if (skillInfo.getLevel() <= 0) {
				continue;
			}

			SkillCfg skillCfg = cfgDAO.getCfg(skillInfo.getSkillId());
			if (skillCfg == null) {
				return;
			}

			skillInfo.getBuffId().clear();
			skillInfo.getSelfBuffId().clear();
			skillInfo.setExtraDamage(0);
			skillInfo.setSkillRate(0);

			// 检查控制技能
			checkControl(skillInfo, skillCfg, skillList);

			// 检查技能所受的Buff
			checkSkillBuffs(skillInfo, skillCfg, skillList);
		}
	}

	/**
	 * 检查受那些技能的影响
	 * 
	 * @param skill
	 * @param skillList
	 */
	private static void checkControl(Skill skill, SkillCfg skillCfg, List<Skill> skillList) {
		String updateSkillId = skill.getSkillId();
		if (skill.getLevel() <= 0) {
			return;
		}

		SkillCfgDAO cfgDAO = SkillCfgDAO.getInstance();

		int skillDamage = skillCfg.getSkillDamage();
		float skillRate = skillCfg.getSkillRate();
		int extraDamage = skillCfg.getExtraDamage();

		// 相互影响的伤害值
		for (int i = skillList.size() - 1; i >= 0; --i) {
			Skill skillInfo = skillList.get(i);
			if (skillInfo.getLevel() <= 0) {
				continue;
			}

			String skillId = skillInfo.getSkillId();
			SkillCfg cfg = cfgDAO.getCfg(skillId);
			if (cfg == null) {
				continue;
			}

			String controlId = cfg.getControlId();
			if (StringUtils.isEmpty(controlId)) {
				continue;
			}

			boolean hasControl = false;// 是否受此技能影响
			String[] arr = controlId.split("_");
			for (int j = 0, len = arr.length; j < len; j++) {
				String controlSkillId = arr[j];// 控制的技能到的技能Id
				if (updateSkillId.startsWith(controlSkillId)) {
					hasControl = true;
					break;
				}
			}

			if (hasControl) {
				skillDamage += cfg.getSkillDamage();
				skillRate += cfg.getSkillRate();
				extraDamage += cfg.getExtraDamage();
			}
		}

		skill.setSkillDamage(skillDamage);
		skill.setExtraDamage(extraDamage);
		skill.setSkillRate(skillRate);
	}

	/**
	 * 检查技能所有的buff
	 * 
	 * @param skill
	 * @param skillCfg
	 * @param skillList
	 */
	private static void checkSkillBuffs(Skill skill, SkillCfg skillCfg, List<Skill> skillList) {
		String updateSkillId = skill.getSkillId();
		if (skill.getLevel() <= 0) {
			return;
		}

		SkillCfgDAO cfgDAO = SkillCfgDAO.getInstance();

		List<Integer> buffList = new ArrayList<Integer>();
		List<Integer> selfBuffList = new ArrayList<Integer>();

		// 相互影响的伤害值
		for (int i = skillList.size() - 1; i >= 0; --i) {
			Skill skillInfo = skillList.get(i);
			if (skillInfo.getLevel() <= 0) {
				continue;
			}

			String skillId = skillInfo.getSkillId();
			SkillCfg cfg = cfgDAO.getCfg(skillId);
			if (cfg == null) {
				continue;
			}

			List<Integer> buffs = checkBuffer(updateSkillId, cfg.getBuffId(), buffList);
			if (buffs != null && !buffs.isEmpty()) {
				buffList.addAll(buffs);
			}

			List<Integer> selfBuffs = checkBuffer(updateSkillId, cfg.getSelfBuffId(), selfBuffList);
			if (selfBuffs != null && !selfBuffs.isEmpty()) {
				selfBuffList.addAll(selfBuffs);
			}
		}

		skill.setBuffId(buffList);
		skill.setSelfBuffId(selfBuffList);
	}

	/**
	 * 检查被影响到的Buff列表
	 * 
	 * @param updateSkillId
	 * @param buffIdStr
	 * @param hasBuff
	 * @return
	 */
	private static List<Integer> checkBuffer(String updateSkillId, String buffIdStr, List<Integer> hasBuff) {
		if (StringUtils.isEmpty(buffIdStr)) {
			return null;
		}

		String[] split = buffIdStr.split(";");

		List<Integer> buffList = new ArrayList<Integer>();

		for (int i = 0, len = split.length; i < len; i++) {
			String[] split1 = split[0].split("_");

			if (!updateSkillId.startsWith(split1[0])) {
				return null;
			}

			for (int j = 1, buffLen = split1.length; j < buffLen; j++) {
				int buffId = Integer.parseInt(split1[j]);
				if (buffId <= 0 || hasBuff.contains(buffId)) {
					continue;
				}

				buffList.add(buffId);
			}
		}

		return buffList;
	}
}