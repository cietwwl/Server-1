package com.playerdata.fixEquip.attr;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;

/*
 * @author HC
 * @date 2016年5月13日 下午12:32:49
 * @Description 英雄的装备属性
 */
public class FixExpEquipAttributeComponent extends AbstractAttributeCalc {

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		
		String ownerId = hero.getUUId();
		List<AttributeItem> attrItems = hero.getFixExpEquipMgr().toAttrItems(ownerId );

		return AttributeSet.newBuilder().addAttribute(attrItems).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fix_Exp_Equip;
	}
}