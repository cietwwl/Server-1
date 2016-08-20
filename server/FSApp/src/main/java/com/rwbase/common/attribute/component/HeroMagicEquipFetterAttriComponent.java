package com.rwbase.common.attribute.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.AttributeSet.Builder;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;
import com.rwbase.common.attribute.param.MagicEquipFetterParam;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;
import com.rwbase.dao.fetters.pojo.cfg.dao.FetterMagicEquipCfgDao;

/**
 * 法宝神器羁绊属性计算
 * @author Alex
 *
 * 2016年7月21日 下午5:30:54
 */
public class HeroMagicEquipFetterAttriComponent extends AbstractAttributeCalc{

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Magic_Equip_Fetter;
	}

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		
//		System.out.println("=======================开始计算神器法宝羁绊属性数据~");
		
		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}

		MagicEquipFetterParam param = new MagicEquipFetterParam(player.getUserId(), hero.getModeId());
		
		
		return calc.calc(param);
	
	}

	
	
}
