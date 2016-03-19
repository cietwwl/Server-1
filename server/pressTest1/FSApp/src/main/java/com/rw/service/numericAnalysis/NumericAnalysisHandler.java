package com.rw.service.numericAnalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.bm.login.AccoutBM;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.FightingCalculator;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.SkillMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyMagic;
import com.rw.fsutil.util.StringUtil;
import com.rw.netty.UserChannelMgr;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.equipment.EquipItemHelper;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.hero.pojo.RoleBaseInfoHelper;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.skill.pojo.SkillCfg;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.dao.user.UserGameData;
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
		ArmyHero tmpHero = generalArmyHero(roleDatas.get(0), true);
		if (tmpHero != null)
			armyInfo.setPlayer(tmpHero);

		List<ArmyHero> heroList = new ArrayList<ArmyHero>();
		for (int i = 1; i < roleDatas.size(); i++) {
			tmpHero = generalArmyHero(roleDatas.get(i), false);
			if (tmpHero != null)
				heroList.add(tmpHero);
		}
		armyInfo.setHeroList(heroList);
		
		//法宝
		if (roleDatas.get(0).getMagic() > 0) {
			ArmyMagic armyMagic = new ArmyMagic();
			armyMagic.setModelId(roleDatas.get(0).getMagic());
			armyMagic.setLevel(roleDatas.get(0).getMagicLevel());
			armyInfo.setArmyMagic(armyMagic);
		}
		
		return armyInfo;
	}

	private static ArmyHero generalArmyHero(NumericAnalysisData roleData, boolean isPlayer) {
		RoleCfg roleCfg = RoleCfgDAO.getInstance().getConfig(roleData.getRoleId(), roleData.getStarLevel());
		if (roleCfg == null)
			return null;
		
		RoleBaseInfo roleBaseInfo = new RoleBaseInfo();
		roleBaseInfo.setCareerType(roleCfg.getCareerType());
		roleBaseInfo.setTemplateId(roleCfg.getRoleId());
		roleBaseInfo.setModeId(roleData.getRoleId());
		roleBaseInfo.setStarLevel(roleData.getStarLevel());
		roleBaseInfo.setQualityId(roleData.getQuality());
		
		ArmyHero armyHero = new ArmyHero();
		armyHero.setRoleBaseInfo(roleBaseInfo);
		armyHero.setPlayer(isPlayer);
		
		//基本属性
		AttrData attrData = new AttrData();
		attrData.plus(RoleBaseInfoHelper.toAttrData(roleBaseInfo));
		
		AttrData percentAttrData = new AttrData();
		
		//附灵等级
		String[] ghostLevelsStrings = roleData.getGhostLevel().split(",");
		List<Integer> equipModelIds = new ArrayList<Integer>();
		equipModelIds.add(roleData.getEquip1());
		equipModelIds.add(roleData.getEquip2());
		equipModelIds.add(roleData.getEquip3());
		equipModelIds.add(roleData.getEquip4());
		equipModelIds.add(roleData.getEquip5());
		equipModelIds.add(roleData.getEquip6());
		
		//装备
		int index = 0;
		while (index < equipModelIds.size()) {
			HeroEquipCfg cfg = HeroEquipCfgDAO.getInstance().getConfig(equipModelIds.get(index));
			if (cfg != null) {
				int level = 0;
				if (index < ghostLevelsStrings.length)
					level = Integer.parseInt(ghostLevelsStrings[index]);
				attrData.plus(EquipItemHelper.toAttrData(cfg, level));
			}
			index++;
		}

		//宝石
		List<Integer> inlayModelIds = new ArrayList<Integer>();
		inlayModelIds.add(roleData.getGem1());
		inlayModelIds.add(roleData.getGem2());
		inlayModelIds.add(roleData.getGem3());
		inlayModelIds.add(roleData.getGem4());
		inlayModelIds.add(roleData.getGem5());
		inlayModelIds.add(roleData.getGem6());
		index = 0;
		while (index < inlayModelIds.size()) {
			GemCfg cfg = ItemCfgHelper.getGemCfg(inlayModelIds.get(index));
			if (cfg != null) {
				attrData.plus(AttrData.fromObject(cfg));
				percentAttrData.plus(AttrData.fromPercentObject(cfg));
			}
			index++;
		}
		
		//技能
		List<Skill> skills = GetSkillLists(roleData, roleCfg);
		index = 0;
		while (index < skills.size()) {
			String tmpSkillId = skills.get(index).getSkillId();
			if (StringUtils.isNotBlank(tmpSkillId)) {
				SkillCfg cfg = (SkillCfg) SkillCfgDAO.getInstance().getCfgById(tmpSkillId);
				if (cfg != null) {
					attrData.plus(AttrData.fromObject(cfg));
				}
			}
			
			index++;
		}
		
		
		attrData.addPercent(percentAttrData);
		
		armyHero.setAttrData(attrData);
		armyHero.setSkillList(skills);
		
		return armyHero;
	}
	
	private static List<Skill> GetSkillLists(NumericAnalysisData roleData, RoleCfg roleCfg) {
		String[] skillLevels = roleData.getSkillLevels().split(",");
		List<Skill> skills = new ArrayList<Skill>();
		//技能
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
		
		//搜索关联的BUFF
		for (Skill pSkill : skills) {
			SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(pSkill.getSkillId());			
			updateMoreInfo(pSkill, skills);
		}
		return skills;
	}
	
	/**
	 * 更新技能其它信息
	 * @param pSkill
	 */
	private static void updateMoreInfo(Skill pSkill, List<Skill> skillList) {
		if(pSkill.getLevel() <= 0){
			return;
		}
		
		SkillCfg pSkillCfg = (SkillCfg) SkillCfgDAO.getInstance().getCfgById(pSkill.getSkillId());
		if(pSkillCfg == null){
			return;
		}
		
		pSkill.setSkillRate(pSkillCfg.getSkillRate());
		pSkill.setExtraDamage(pSkillCfg.getExtraDamage());
		pSkill.setSkillDamage(pSkillCfg.getSkillDamage());

		//查找控制技能的技能系数和额外伤害
		if(StringUtils.isNotBlank(pSkillCfg.getControlId())){
			String[] relatedSkillCfgId = pSkillCfg.getControlId().split("_");
			for (String cfgIdTmp : relatedSkillCfgId) {
				Skill targetSkill = getSkill(cfgIdTmp, skillList);
				if(targetSkill != null && targetSkill.getLevel() > 0){
					SkillCfg conSkillCfg = SkillCfgDAO.getInstance().getCfg(targetSkill.getSkillId());

					targetSkill.setSkillRate(conSkillCfg.getSkillRate() + pSkillCfg.getSkillRate());
					targetSkill.setExtraDamage(conSkillCfg.getExtraDamage() + pSkillCfg.getExtraDamage());
				}
			}
		}
		pSkill.getBuffId().clear();
		//查找控制技能的buff
		if(pSkill.getLevel() > 0 && StringUtils.isNotBlank(pSkillCfg.getBuffId())){
			String[] skillBufflist = pSkillCfg.getBuffId().split(";");//SkillBuffList xxxx_xxx
			Skill targetSkill;
			String[] targetBuffarr;
			for (String skillBuff : skillBufflist) {
				targetBuffarr = skillBuff.split("_");//skillBuff xxxx
				targetSkill = getSkill(targetBuffarr[0], skillList);//目标技能
				
				if(targetSkill != null && targetSkill.getLevel() > 0){
					for (int j = 1; j < targetBuffarr.length; j++) {
						String targetBuff = targetBuffarr[j];
						if(StringUtils.isNotBlank(targetBuff) && !targetBuff.equals("0")){
							List<Integer> curBuffList = targetSkill.getBuffId();
							if(curBuffList.size() > j - 1){
								curBuffList.set(j-1, Integer.parseInt(targetBuff));
							}else{
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
	 * @param cfgId
	 * @return
	 */
	private static Skill getSkill(String cfgId, List<Skill> skillList) {
		Skill targetItem = null;
		
		for (Skill skill : skillList) {
			if(skill.getSkillId().startsWith(cfgId)){
				targetItem = skill;
				break;
			}
		}
		
		return targetItem;
	}
}
