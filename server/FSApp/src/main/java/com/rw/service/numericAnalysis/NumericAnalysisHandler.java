package com.rw.service.numericAnalysis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyMagic;
import com.playerdata.team.EquipInfo;
import com.playerdata.team.HeroBaseInfo;
import com.playerdata.team.HeroInfo;
import com.playerdata.team.SkillInfo;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.param.MagicParam;
import com.rwbase.common.attribute.param.MagicParam.MagicBuilder;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.skill.pojo.SkillCfg;
import com.rwproto.NumericAnalysisProtos.NumericAnalysisData;
import com.rwproto.NumericAnalysisProtos.NumericAnalysisRequest;
import com.rwproto.NumericAnalysisProtos.NumericAnalysisResponse;

public class NumericAnalysisHandler {

	public static ByteString getArmyInfo(NumericAnalysisRequest numericAnalysisRequest) {
		NumericAnalysisResponse.Builder builder = NumericAnalysisResponse.newBuilder();

		List<NumericAnalysisData> mainPlayDatas = new ArrayList<NumericAnalysisData>();
		List<NumericAnalysisData> enemyDatas = new ArrayList<NumericAnalysisData>();

		for (int i = 0; i < numericAnalysisRequest.getDataListCount(); i++) {
			NumericAnalysisData data = numericAnalysisRequest.getDataList(i);
			if (data.getCamp() == 1) {
				mainPlayDatas.add(data);
			} else {
				enemyDatas.add(data);
			}
		}

		List<String> armyInfos = new ArrayList<String>();
		ArmyInfo tmpArmyInfo = generalArmyInfo(mainPlayDatas);

		String tmpJson = "我方配置错误，不能生成英雄！";
		try {
			String json = tmpArmyInfo.toJson();
			if (json != null)
				tmpJson = json;
		} catch (Exception e1) {
		}
		armyInfos.add(tmpJson);

		tmpArmyInfo = generalArmyInfo(enemyDatas);

		tmpJson = "敌方配置错误，不能生成英雄！";
		try {
			String json = tmpArmyInfo.toJson();
			if (json != null)
				tmpJson = json;
		} catch (Exception e1) {
		}
		armyInfos.add(tmpJson);

		builder.addAllArmyInfos(armyInfos);

		return builder.build().toByteString();
	}

	private static ArmyInfo generalArmyInfo(List<NumericAnalysisData> roleDatas) {
		if (roleDatas.size() <= 0)
			return null;
		ArmyInfo armyInfo = new ArmyInfo();

		MagicParam magicInfo = null;
		// 法宝
		NumericAnalysisData mainRoleData = roleDatas.get(0);
		if (mainRoleData.getMagic() > 0) {
			ArmyMagic armyMagic = new ArmyMagic();
			armyMagic.setModelId(mainRoleData.getMagic());
			armyMagic.setLevel(mainRoleData.getMagicLevel());
			armyInfo.setArmyMagic(armyMagic);

			MagicParam.MagicBuilder builder = new MagicBuilder();
			builder.setMagicId(String.valueOf(mainRoleData.getMagic()));
			builder.setMagicLevel(mainRoleData.getMagicLevel());
			builder.setUserId(String.valueOf(mainRoleData.getRoleId()));
			magicInfo = builder.build();
		}

		ArmyHero tmpHero = generalArmyHero(mainRoleData, true, magicInfo);
		if (tmpHero != null)
			armyInfo.setPlayer(tmpHero);

		List<ArmyHero> heroList = new ArrayList<ArmyHero>();
		for (int i = 1; i < roleDatas.size(); i++) {
			tmpHero = generalArmyHero(roleDatas.get(i), false, magicInfo);
			if (tmpHero != null)
				heroList.add(tmpHero);
		}
		armyInfo.setHeroList(heroList);
		return armyInfo;
	}

	private static ArmyHero generalArmyHero(NumericAnalysisData roleData, boolean isPlayer, MagicParam magicInfo) {
		RoleCfg roleCfg = RoleCfgDAO.getInstance().getConfig(roleData.getRoleId(), roleData.getStarLevel());
		if (roleCfg == null)
			return null;

		HeroInfo heroInfo = new HeroInfo();

		HeroBaseInfo heroBaseInfo = new HeroBaseInfo();
		heroBaseInfo.setLevel(roleData.getLevel());
		heroBaseInfo.setQuality(roleData.getQuality());
		heroBaseInfo.setStar(roleData.getStarLevel());
		heroBaseInfo.setTmpId(roleCfg.getRoleId());
		heroInfo.setBaseInfo(heroBaseInfo);

		RoleBaseInfo roleBaseInfo = new RoleBaseInfo();
		roleBaseInfo.setCareerType(roleCfg.getCareerType());
		roleBaseInfo.setTemplateId(roleCfg.getRoleId());
		roleBaseInfo.setModeId(roleData.getRoleId());
		roleBaseInfo.setStarLevel(roleData.getStarLevel());
		roleBaseInfo.setQualityId(roleData.getQuality());

		ArmyHero armyHero = new ArmyHero();
		armyHero.setRoleBaseInfo(roleBaseInfo);
		armyHero.setPlayer(isPlayer);

		// 附灵等级
		String[] ghostLevelsStrings = roleData.getGhostLevel().split(",");
		List<Integer> equipModelIds = new ArrayList<Integer>();
		equipModelIds.add(roleData.getEquip1());
		equipModelIds.add(roleData.getEquip2());
		equipModelIds.add(roleData.getEquip3());
		equipModelIds.add(roleData.getEquip4());
		equipModelIds.add(roleData.getEquip5());
		equipModelIds.add(roleData.getEquip6());

		// 装备
		List<EquipInfo> equipInfoList = new ArrayList<EquipInfo>();
		for (int i = 0, size = equipModelIds.size(); i < size; i++) {
			int modelId = equipModelIds.get(i);
			HeroEquipCfg cfg = HeroEquipCfgDAO.getInstance().getConfig(modelId);
			if (cfg != null) {
				int level = 0;
				if (i < ghostLevelsStrings.length) {
					level = Integer.parseInt(ghostLevelsStrings[i]);
				}

				EquipInfo equipInfo = new EquipInfo();
				equipInfo.settId(String.valueOf(modelId));
				equipInfo.seteLevel(level);
				equipInfoList.add(equipInfo);
			}
		}

		heroInfo.setEquip(equipInfoList);

		// 宝石
		List<String> gemList = new ArrayList<String>();
		gemList.add(String.valueOf(roleData.getGem1()));
		gemList.add(String.valueOf(roleData.getGem2()));
		gemList.add(String.valueOf(roleData.getGem3()));
		gemList.add(String.valueOf(roleData.getGem4()));
		gemList.add(String.valueOf(roleData.getGem5()));
		gemList.add(String.valueOf(roleData.getGem6()));
		heroInfo.setGem(gemList);

		// 技能
		List<Skill> skills = GetSkillLists(roleData, roleCfg);

		List<SkillInfo> skillList = new ArrayList<SkillInfo>();
		for (int i = 0, size = skills.size(); i < size; i++) {
			Skill skill = skills.get(i);
			if (skill == null) {
				continue;
			}

			SkillInfo skillInfo = new SkillInfo();
			skillInfo.setSkillId(skill.getSkillId());
			skillInfo.setSkillLevel(skill.getLevel());
			skillList.add(skillInfo);
		}

		heroInfo.setSkill(skillList);

		armyHero.setAttrData(AttributeBM.getRobotAttrData(magicInfo.getUserId(), heroInfo, magicInfo));
		armyHero.setSkillList(skills);

		return armyHero;
	}

	private static List<Skill> GetSkillLists(NumericAnalysisData roleData, RoleCfg roleCfg) {
		String[] skillLevels = roleData.getSkillLevels().split(",");
		List<Skill> skills = new ArrayList<Skill>();
		// 技能
		List<String> skillList = new ArrayList<String>();
		skillList.add(roleCfg.getSkillId01());
		skillList.add(roleCfg.getSkillId02());
		skillList.add(roleCfg.getSkillId03());
		skillList.add(roleCfg.getSkillId04());
		skillList.add(roleCfg.getSkillId05());

		for (int i = 0; i < skillList.size(); i++) {
			String tmpSkillId = skillList.get(i);
			if (StringUtils.isNotBlank(tmpSkillId)) {
				if (!StringUtils.isNotBlank(skillLevels[i]))
					continue;

				Skill pSkill = new Skill();
				String[] tmpIds = tmpSkillId.split("_");
				tmpSkillId = tmpIds[0] + "_" + skillLevels[i];
				pSkill.setSkillId(tmpSkillId);
				pSkill.setLevel(Integer.parseInt(skillLevels[i]));

				skills.add(pSkill);
			}
		}

		// 搜索关联的BUFF
		for (Skill pSkill : skills) {
			SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(pSkill.getSkillId());
			if (cfg == null) {
				continue;
			}

			updateMoreInfo(pSkill, skills);
		}
		return skills;
	}

	/**
	 * 更新技能其它信息
	 * 
	 * @param pSkill
	 */
	private static void updateMoreInfo(Skill pSkill, List<Skill> skillList) {
		if (pSkill.getLevel() <= 0) {
			return;
		}

		SkillCfg pSkillCfg = (SkillCfg) SkillCfgDAO.getInstance().getCfgById(pSkill.getSkillId());
		if (pSkillCfg == null) {
			return;
		}

		pSkill.setSkillRate(pSkillCfg.getSkillRate());
		pSkill.setExtraDamage(pSkillCfg.getExtraDamage());
		pSkill.setSkillDamage(pSkillCfg.getSkillDamage());

		// 查找控制技能的技能系数和额外伤害
		if (StringUtils.isNotBlank(pSkillCfg.getControlId())) {
			String[] relatedSkillCfgId = pSkillCfg.getControlId().split("_");
			for (String cfgIdTmp : relatedSkillCfgId) {
				Skill targetSkill = getSkill(cfgIdTmp, skillList);
				if (targetSkill != null && targetSkill.getLevel() > 0) {
					SkillCfg conSkillCfg = SkillCfgDAO.getInstance().getCfg(targetSkill.getSkillId());

					targetSkill.setSkillRate(conSkillCfg.getSkillRate() + pSkillCfg.getSkillRate());
					targetSkill.setExtraDamage(conSkillCfg.getExtraDamage() + pSkillCfg.getExtraDamage());
				}
			}
		}
		pSkill.getBuffId().clear();
		// 查找控制技能的buff
		if (pSkill.getLevel() > 0 && StringUtils.isNotBlank(pSkillCfg.getBuffId())) {
			String[] skillBufflist = pSkillCfg.getBuffId().split(";");// SkillBuffList xxxx_xxx
			Skill targetSkill;
			String[] targetBuffarr;
			for (String skillBuff : skillBufflist) {
				targetBuffarr = skillBuff.split("_");// skillBuff xxxx
				targetSkill = getSkill(targetBuffarr[0], skillList);// 目标技能

				if (targetSkill != null && targetSkill.getLevel() > 0) {
					for (int j = 1; j < targetBuffarr.length; j++) {
						String targetBuff = targetBuffarr[j];
						if (StringUtils.isNotBlank(targetBuff) && !targetBuff.equals("0")) {
							List<Integer> curBuffList = targetSkill.getBuffId();
							if (curBuffList.size() > j - 1) {
								curBuffList.set(j - 1, Integer.parseInt(targetBuff));
							} else {
								curBuffList.add(Integer.parseInt(targetBuff));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 根据id获取技能
	 * 
	 * @param cfgId
	 * @return
	 */
	private static Skill getSkill(String cfgId, List<Skill> skillList) {
		Skill targetItem = null;

		for (Skill skill : skillList) {
			if (skill.getSkillId().startsWith(cfgId)) {
				targetItem = skill;
				break;
			}
		}

		return targetItem;
	}
}
