package com.playerdata.fixEquip.attr;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
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
public class FixNormEquipAttributeComponent extends AbstractAttributeCalc {

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		
		Builder attrSetBuilder = AttributeSet.newBuilder();
		if(CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.FIX_EQUIP, player.getLevel())){
			
			String ownerId = hero.getUUId();
			List<AttributeItem> attrItems_level = hero.getFixNormEquipMgr().levelToAttrItems(ownerId );
			List<AttributeItem> attrItems_quality = hero.getFixNormEquipMgr().qualityToAttrItems(ownerId );
			
			attrSetBuilder.addAttribute(attrItems_level);
			attrSetBuilder.addAttribute(attrItems_quality);
		}
		
		if(CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.FIX_EQUIP_STAR, player.getLevel())){
			
			String ownerId = hero.getUUId();
			List<AttributeItem> attrItems_star = hero.getFixNormEquipMgr().starToAttrItems(ownerId );
			
			attrSetBuilder.addAttribute(attrItems_star);
		}
		
		return attrSetBuilder.build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fix_Norm_Equip;
	}
}