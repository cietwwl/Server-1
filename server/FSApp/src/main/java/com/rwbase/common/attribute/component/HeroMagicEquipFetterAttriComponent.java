package com.rwbase.common.attribute.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.attribute.AttributeSet.Builder;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;
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
		List<Integer> list = player.getMe_FetterMgr().getHeroFixEqiupFetter(hero.getModelId());
		if(hero.isMainRole()){
			List<Integer> magicFetter = player.getMe_FetterMgr().getMagicFetter();
			list.addAll(magicFetter);
		}
		if(list.isEmpty()){
			return null;
		}
		
		
		//属性Map集合
		HashMap<Integer, AttributeItem> map = new HashMap<Integer, AttributeItem>();
		
		MagicEquipConditionCfg cfg;
		for (int id : list) {
			cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
			
			// 计算固定值
			Map<Integer, Integer> attrDataMap = cfg.getAttrDataMap();
			for (Entry<Integer, Integer> entry : attrDataMap.entrySet()) {
				Integer key = entry.getKey();
				AttributeItem attributeItem = map.get(key);
				int value = 0;
				int precentValue = 0;
				if (attributeItem != null) {
					value = attributeItem.getIncreaseValue();
					precentValue = attributeItem.getIncPerTenthousand();
				}

				attributeItem = new AttributeItem(AttributeType.getAttributeType(key), entry.getValue() + value, precentValue);
				map.put(key, attributeItem);
			}
			
			// 计算百分比值
			Map<Integer, Integer> precentAttrDataMap = cfg.getPrecentAttrDataMap();
			for (Entry<Integer, Integer> entry : precentAttrDataMap.entrySet()) {
				Integer key = entry.getKey();
				AttributeItem attributeItem = map.get(key);
				int value = 0;
				int precentValue = 0;
				if (attributeItem != null) {
					value = attributeItem.getIncreaseValue();
					precentValue = attributeItem.getIncPerTenthousand();
				}

				attributeItem = new AttributeItem(AttributeType.getAttributeType(key), value, entry.getValue() + precentValue);
				map.put(key, attributeItem);
			}
		}
		
		
		// 计算属性
		if (map.isEmpty()) {
			return null;
		}
	
		return new Builder().addAttribute(new ArrayList<AttributeItem>(map.values())).build();
	}

	
	
}
