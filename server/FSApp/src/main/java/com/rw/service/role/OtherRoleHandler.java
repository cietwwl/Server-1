package com.rw.service.role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.HeroIF;
import com.playerdata.readonly.ItemDataIF;
import com.playerdata.readonly.PlayerIF;
import com.rwbase.dao.equipment.EquipItemIF;
import com.rwbase.dao.skill.pojo.SkillIF;
import com.rwbase.dao.user.readonly.TableUserIF;
import com.rwproto.EquipProtos.EquipAttrData;
import com.rwproto.EquipProtos.EquipData;
import com.rwproto.ItemBagProtos.EItemAttributeType;
import com.rwproto.OtherRoleAttrProtos.EMsgType;
import com.rwproto.OtherRoleAttrProtos.OtherHero;
import com.rwproto.OtherRoleAttrProtos.OtherRoleAttr;
import com.rwproto.OtherRoleAttrProtos.OtherRoleAttrResponse;
import com.rwproto.OtherRoleAttrProtos.SkillInfo;

public class OtherRoleHandler {
	private static OtherRoleHandler instance = new OtherRoleHandler();

	private OtherRoleHandler() {
	}

	public static OtherRoleHandler getInstance() {
		return instance;
	}

	public ByteString getRoleInfo(String otherUserId, Player player) {
		OtherRoleAttrResponse.Builder res = OtherRoleAttrResponse.newBuilder();
		OtherRoleAttr.Builder otherRoleAttr = OtherRoleAttr.newBuilder();

		PlayerIF playerOther = PlayerMgr.getInstance().getReadOnlyPlayer(
				otherUserId);
		if (playerOther == null) {
			return null;
		}
		setOtherInfo(otherRoleAttr, playerOther);
		res.setInfo(otherRoleAttr);
		res.setType(EMsgType.BaseMsg);

		return res.build().toByteString();
	}

	public void setOtherInfo(OtherRoleAttr.Builder otherRoleAttr,
			PlayerIF player) {
		TableUserIF tableUser = player.getTableUser();
		otherRoleAttr.setUserId(tableUser.getUserId());
		otherRoleAttr.setLevel(player.getLevel());
		otherRoleAttr.setUserName(tableUser.getUserName());

		if (tableUser.getHeadImageWithDefault() == null) {
			otherRoleAttr.setHeadImage("");
		} else {
			otherRoleAttr.setHeadImage(tableUser.getHeadImageWithDefault());
		}
		otherRoleAttr.setHeadbox(player.getHeadFrame());

		Hero mainPHero = null;
		List<Hero> heroList = null;
		List<Hero> tempHeroList = new ArrayList<Hero>();
		Enumeration<? extends HeroIF> pHeroMap = player.getHeroMgr()
				.getHerosEnumeration();
		while (pHeroMap.hasMoreElements()) { // 佣兵信息的遍历
			Hero pHero = (Hero) pHeroMap.nextElement();
			if (!pHero.isMainRole()) {
				tempHeroList.add(pHero);
			} else {
				mainPHero = pHero;
			}
		}

		HeroIF mainRoleHero = player.getMainRoleHero();
		otherRoleAttr.setTemplateId(mainRoleHero.getTemplateId());
		otherRoleAttr.setFighting(mainRoleHero.getFighting());

		List<? extends EquipItemIF> equipList = player.getEquipMgr()
				.getEquipList();
		ItemDataIF itemDataIFfb = player.getMagic();
		if (itemDataIFfb != null) {

			EquipData.Builder tagItem = EquipData.newBuilder();

			tagItem.setCount(1);
			tagItem.setId(itemDataIFfb.getModelId());
			tagItem.setSolt(6);

			// EquipAttrData.Builder levelData = EquipAttrData.newBuilder();
			// levelData.setAttrId(EItemAttributeType.Equip_AttachLevel_VALUE);
			// levelData.setAttValue(String.valueOf(tagItem.get));

			// EquipAttrData.Builder expData = EquipAttrData.newBuilder();
			// expData.setAttrId(EItemAttributeType.Equip_AttachExp_VALUE);
			// expData.setAttValue(String.valueOf(tagItem.getExp()));

			otherRoleAttr.addEquipInfo(tagItem);
		}

		for (EquipItemIF equipItem : equipList) {

			EquipData.Builder tagItem = EquipData.newBuilder();

			tagItem.setCount(1);
			tagItem.setId(equipItem.getModelId());
			tagItem.setSolt(equipItem.getEquipIndex());

			EquipAttrData.Builder levelData = EquipAttrData.newBuilder();
			levelData.setAttrId(EItemAttributeType.Equip_AttachLevel_VALUE);
			levelData.setAttValue(String.valueOf(equipItem.getLevel()));

			EquipAttrData.Builder expData = EquipAttrData.newBuilder();
			expData.setAttrId(EItemAttributeType.Equip_AttachExp_VALUE);
			expData.setAttValue(String.valueOf(equipItem.getExp()));

			otherRoleAttr.addEquipInfo(tagItem);
		}

		List<? extends SkillIF> skillList = player.getSkillMgr().getSkillList();// player.getSkillMgr().getTableSkill().getSkillLIst();

		for (int j = 0; j < skillList.size(); j++) {

			SkillInfo.Builder skillInfo = SkillInfo.newBuilder();
			SkillIF skill = skillList.get(j);
			skillInfo.setId(skill.getSkillId());
			skillInfo.setLevel(skill.getLevel());
			skillInfo.setOrder(skill.getOrder());
			otherRoleAttr.addSkillInfo(skillInfo);
		}

		// 服务器筛选最强佣兵优先级：筛选依次为：高到低品阶>高到低等级>高到低资质>高到低战力>低到高佣兵ID
		if (tempHeroList.size() > 4) {
			heroList = new ArrayList<Hero>(sortHero(tempHeroList).subList(0, 4));
		} else {
			heroList = new ArrayList<Hero>(tempHeroList);
		}

		heroList.add(0, mainPHero);

		for (Hero hero : heroList) {
			OtherHero.Builder otherHero = OtherHero.newBuilder();
			otherHero.setHeroId(hero.getHeroData().getTemplateId());
			otherHero.setModeId(String.valueOf(hero.getHeroData().getModeId()));
			if (hero.getHeroCfg().getImageId() != null) {
				otherHero.setIcon(hero.getHeroCfg().getImageId());
			}
			otherHero.setLevel(hero.getLevel());
			otherHero.setQualityId(hero.getQualityId());
			otherHero.setName(hero.getHeroCfg().getName());
			otherHero.setStarLevel(hero.getHeroCfg().getStarLevel());
			otherHero.setFighting(hero.getFighting());

			skillList = hero.getSkillMgr().getSkillList();
			for (int j = 0; j < skillList.size(); j++) {

				SkillInfo.Builder skillInfo = SkillInfo.newBuilder();
				SkillIF skill = skillList.get(j);
				skillInfo.setId(skill.getSkillId());
				skillInfo.setLevel(skill.getLevel());
				skillInfo.setOrder(skill.getOrder());
				otherHero.addSkillInfo(skillInfo);
			}

			List<? extends EquipItemIF> heroequipList = hero.getEquipMgr()
					.getEquipList();

			if (itemDataIFfb != null && hero.isMainRole()) {
				EquipData.Builder tagItem = EquipData.newBuilder();
				tagItem.setCount(1);
				tagItem.setId(itemDataIFfb.getModelId());
				tagItem.setSolt(6);

				EquipAttrData.Builder levelData = EquipAttrData.newBuilder();
				levelData.setAttrId(2);
				int magicLevel = player.getMagic().getMagicLevel();
				levelData.setAttValue(magicLevel + "");
				// String
				// lvv=itemDataIFfb.getExtendAttr(EItemAttributeType.Equip_AttachLevel_VALUE);
				// levelData.setAttValue();

				// EquipAttrData.Builder expData = EquipAttrData.newBuilder();
				// expData.setAttrId(EItemAttributeType.Equip_AttachExp_VALUE);

				tagItem.addExtendAttr(levelData);
				// tagItem.addExtendAttr(expData);
				otherHero.addEquipInfo(tagItem);
			}

			for (EquipItemIF equipItem : heroequipList) {

				EquipData.Builder tagItem = EquipData.newBuilder();

				tagItem.setCount(1);
				tagItem.setId(equipItem.getModelId());
				tagItem.setSolt(equipItem.getEquipIndex());

				EquipAttrData.Builder levelData = EquipAttrData.newBuilder();
				levelData.setAttrId(EItemAttributeType.Equip_AttachLevel_VALUE);
				levelData.setAttValue(String.valueOf(equipItem.getLevel()));

				EquipAttrData.Builder expData = EquipAttrData.newBuilder();
				expData.setAttrId(EItemAttributeType.Equip_AttachExp_VALUE);
				expData.setAttValue(String.valueOf(equipItem.getExp()));

				otherHero.addEquipInfo(tagItem);
			}

			otherRoleAttr.addHeroList(otherHero);
		}
	}

	// 服务器筛选最强佣兵优先级：筛选依次为：高到低品阶>高到低等级>高到低资质>高到低战力>低到高佣兵ID
	private Comparator<Hero> heroComparator = new Comparator<Hero>() {

		@Override
		public int compare(Hero o1, Hero o2) {
			if (o1.GetHeroQuality() > o2.GetHeroQuality()) {
				return -1;
			} else if (o1.GetHeroQuality() < o2.GetHeroQuality()) {
				return 1;
			}

			if (o1.getLevel() > o2.getLevel()) {
				return -1;
			} else if (o1.getLevel() < o2.getLevel()) {
				return 1;
			}

			if (o1.getHeroCfg().getStarLevel() > o2.getHeroCfg().getStarLevel()) {
				return -1;
			} else if (o1.getHeroCfg().getStarLevel() < o2.getHeroCfg()
					.getStarLevel()) {
				return 1;
			}

			if (o1.getFighting() > o2.getFighting()) {
				return -1;
			} else if (o1.getFighting() < o2.getFighting()) {
				return 1;
			}

			if (o1.getHeroCfg().getModelId() > o2.getHeroCfg().getModelId()) {
				return 1;
			} else if (o1.getHeroCfg().getModelId() < o2.getHeroCfg()
					.getModelId()) {
				return -1;
			}

			return 0;
		}
	};

	private List<Hero> sortHero(List<Hero> list) {
		Collections.sort(list, heroComparator);
		return list;
	}

}
