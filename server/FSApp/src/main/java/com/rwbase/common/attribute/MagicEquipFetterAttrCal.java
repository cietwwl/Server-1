package com.rwbase.common.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.common.attribute.AttributeSet.Builder;
import com.rwbase.common.attribute.param.MagicEquipFetterParam;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;
import com.rwbase.dao.fetters.pojo.cfg.dao.FetterMagicEquipCfgDao;

public class MagicEquipFetterAttrCal implements IComponentCalc{

	@Override
	public AttributeSet calc(Object obj) {
		MagicEquipFetterParam param = (MagicEquipFetterParam) obj;

		Player player = PlayerMgr.getInstance().find(param.getUserID());
		Hero hero = player.getHeroMgr().getHeroByModerId(param.getHeroModelID());
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
//		StringBuilder sb = new StringBuilder("完成法宝神器羁绊属性计算，增加的属性有：");
//		for (AttributeItem abi : map.values()) {
//			sb.append("类型：[").append(abi.getType().attrFieldName).append("],数值：增加的千分比：[").append(abi.getIncPerTenthousand())
//			.append("],增加的固定值：[").append(abi.getIncreaseValue()).append("]");
//		}
//		System.out.println(sb.toString());
	
		return new Builder().addAttribute(new ArrayList<AttributeItem>(map.values())).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Magic_Equip_Fetter;
	}

}
