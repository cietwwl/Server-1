package com.playerdata.fixEquip.attr;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
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
		if(CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.FIX_EQUIP, player.getLevel())){
			
			String ownerId = hero.getUUId();
			List<AttributeItem> attrItems = hero.getFixExpEquipMgr().toAttrItems(ownerId );
			
			return AttributeSet.newBuilder().addAttribute(attrItems).build();
		}else{
			return AttributeSet.newBuilder().build();
		}
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fix_Exp_Equip;
	}
}