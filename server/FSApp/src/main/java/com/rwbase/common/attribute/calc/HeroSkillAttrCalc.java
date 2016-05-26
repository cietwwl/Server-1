package com.rwbase.common.attribute.calc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.log.GameLog;
import com.playerdata.team.SkillInfo;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.SkillParam;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.pojo.SkillCfg;

/*
 * @author HC
 * @date 2016年5月14日 下午8:04:12
 * @Description 
 */
public class HeroSkillAttrCalc implements IComponentCalc {
	@Override
	public AttributeSet calc(Object obj) {
		SkillParam param = (SkillParam) obj;
		String userId = param.getUserId();
		List<SkillInfo> skillList = param.getSkillList();
		if (skillList == null || skillList.isEmpty()) {
			GameLog.error("计算英雄技能属性", userId, String.format("Id为[%s]的英雄身上没有任何技能", param.getHeroId()));
			return null;
		}

		HashMap<Integer, AttributeItem> map = new HashMap<Integer, AttributeItem>();

		SkillCfgDAO skillCfgDAO = SkillCfgDAO.getInstance();

		for (int i = skillList.size() - 1; i >= 0; --i) {
			SkillInfo skill = skillList.get(i);
			if (skill == null) {
				continue;
			}

			SkillCfg cfg = skillCfgDAO.getCfg(skill.getSkillId());
			if (cfg == null) {
				continue;
			}

			AttributeUtils.calcAttribute(cfg.getAttrDataMap(), cfg.getPrecentAttrDataMap(), map);
		}

		if (map.isEmpty()) {
			GameLog.error("计算英雄技能属性", userId, String.format("Id为[%s]的英雄技能计算出来的属性是空的", param.getHeroId()));
			return null;
		}

		GameLog.info("计算英雄技能属性", userId, AttributeUtils.partAttrMap2Str("技能", map), null);
		return new AttributeSet.Builder().addAttribute(new ArrayList<AttributeItem>(map.values())).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Skill;
	}
}