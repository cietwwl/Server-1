package com.rwbase.common.attribute.calc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.MagicParam;
import com.rwbase.dao.battle.pojo.BufferCfgDAO;
import com.rwbase.dao.battle.pojo.cfg.BufferCfg;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.MagicCfg;

/*
 * @author HC
 * @date 2016年5月14日 下午8:04:46
 * @Description 
 */
public class HeroMagicAttrCalc implements IComponentCalc {
	@Override
	public AttributeSet calc(Object obj) {
		MagicParam param = (MagicParam) obj;
		// String userId = param.getUserId();
		String magicId = param.getMagicId();
		int magicLevel = param.getMagicLevel();

		MagicCfg magicCfg = MagicCfgDAO.getInstance().getCfgById(String.valueOf(magicId));
		if (magicCfg == null) {
			// GameLog.error("计算法宝属性", userId, String.format("主角法宝[%s]没有找到对应的MagicCfg表", magicId));
			return null;
		}

		List<Integer> passiveSkillIdList = magicCfg.getPassiveSkillIdList();
		if (passiveSkillIdList == null || passiveSkillIdList.isEmpty()) {
			// GameLog.error("计算法宝属性", userId, String.format("主角法宝[%s]没有找到任何的被动技能", magicId));
			return null;
		}

		// 计算buffer加成
		HashMap<Integer, AttributeItem> map = new HashMap<Integer, AttributeItem>();

		BufferCfgDAO cfgDAO = BufferCfgDAO.getCfgDAO();
		for (int i = 0, size = passiveSkillIdList.size(); i < size; i++) {
			String bufferId = passiveSkillIdList.get(i) + "_" + magicLevel;
			BufferCfg bufferCfg = cfgDAO.getCfgById(bufferId);
			if (bufferCfg == null) {
				// GameLog.error("计算法宝属性", userId, String.format("主角法宝[%s]的被动技能[%s]找不到BufferCfg的配置表", magicId, bufferId));
				continue;
			}

			AttributeUtils.calcAttribute(bufferCfg.getAttrDataMap(), bufferCfg.getPrecentAttrDataMap(), map);
		}

		if (map.isEmpty()) {
			// GameLog.error("计算法宝属性", userId, String.format("主角法宝[%s]的所有被动属性没有计算到任何属性加成", magicId));
			return null;
		}

		// AttrCheckLoger.logAttr("法宝属性", userId, map);
		return new AttributeSet.Builder().addAttribute(new ArrayList<AttributeItem>(map.values())).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Magic;
	}
}