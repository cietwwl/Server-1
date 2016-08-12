package com.playerdata.fixEquip.attr;

import java.util.ArrayList;
import java.util.List;

import com.bm.arena.ArenaRobotDataMgr;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.AttributeSet.Builder;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

/*
 * @author HC
 * @date 2016年5月13日 下午12:32:49
 * @Description 英雄的装备属性
 */
public class FixExpEquipAttributeComponent extends AbstractAttributeCalc {

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		Builder attrSetBuilder = AttributeSet.newBuilder();

		if (!player.isRobot()) {
			if (CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.FIX_EQUIP, player.getLevel())) {

				String ownerId = hero.getUUId();
				List<AttributeItem> attrItems_level = hero.getFixExpEquipMgr().levelToAttrItems(ownerId);

				List<AttributeItem> attrItems_quality = hero.getFixExpEquipMgr().qualityToAttrItems(ownerId);

				attrSetBuilder.addAttribute(attrItems_level);
				attrSetBuilder.addAttribute(attrItems_quality);
			}

			if (CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.FIX_EQUIP_STAR, player.getLevel())) {

				String ownerId = hero.getUUId();
				List<AttributeItem> attrItems_star = hero.getFixExpEquipMgr().starToAttrItems(ownerId);
				attrSetBuilder.addAttribute(attrItems_star);
			}
		} else {
			String userId = player.getUserId();
			List<FixExpEquipDataItem> fixExpEquipList = ArenaRobotDataMgr.getMgr().getFixExpEquipList(userId, hero.getModelId());
			if (fixExpEquipList != null && !fixExpEquipList.isEmpty()) {
				attrSetBuilder.addAttribute(new ArrayList<AttributeItem>(FixEquipHelper.parseFixExpEquipLevelAttr(userId, fixExpEquipList).values()));
				attrSetBuilder.addAttribute(new ArrayList<AttributeItem>(FixEquipHelper.parseFixExpEquipLevelAttr(userId, fixExpEquipList).values()));
				attrSetBuilder.addAttribute(new ArrayList<AttributeItem>(FixEquipHelper.parseFixExpEquipLevelAttr(userId, fixExpEquipList).values()));
			}
		}
		return attrSetBuilder.build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fix_Exp_Equip;
	}
}