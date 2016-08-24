package com.rwbase.common.attribute.component;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.team.EquipInfo;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;
import com.rwbase.common.attribute.param.EquipParam;
import com.rwbase.common.attribute.param.EquipParam.EquipBuilder;
import com.rwbase.dao.equipment.EquipItem;

/*
 * @author HC
 * @date 2016年5月13日 下午12:32:49
 * @Description 英雄的装备属性
 */
public class HeroEquipAttributeComponent extends AbstractAttributeCalc {

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		List<EquipItem> equipList = hero.getEquipMgr().getEquipList(hero.getUUId());
		if (equipList == null || equipList.isEmpty()) {
//			GameLog.error("计算英雄装备属性", player.getUserId(), String.format("Id为[%s]的英雄身上的装备列表是空的", hero.getUUId()));
			return null;
		}

		int size = equipList.size();
		List<EquipInfo> equipInfoList = new ArrayList<EquipInfo>(size);
		for (int i = 0; i < size; i++) {
			EquipItem item = equipList.get(i);
			if (item == null) {
				continue;
			}

			EquipInfo info = new EquipInfo();
			info.seteLevel(item.getLevel());
			info.settId(String.valueOf(item.getModelId()));

			equipInfoList.add(info);
		}

		EquipParam.EquipBuilder builder = new EquipBuilder();
		builder.setUserId(player.getUserId());
		builder.setHeroId(hero.getUUId());
		builder.setEquipList(equipInfoList);

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			GameLog.error("计算英雄装备属性", player.getUserId(), String.format("Id为[%s]的英雄[%s]对应类型的IComponentCacl的实现类为Null", hero.getUUId(), getComponentTypeEnum()));
			return null;
		}
		return calc.calc(builder.build());
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Equip;
	}
}