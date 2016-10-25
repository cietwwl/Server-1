package com.playerdata.fightinggrowth.calc;

import java.util.Map;
import java.util.Map.Entry;

import com.rwbase.common.attribute.param.GroupSkillParam;
import com.rwbase.dao.fighting.GroupSkillFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.OneToOneTypeFightingCfg;

/**
 * @Author HC
 * @date 2016年10月25日 上午11:38:15
 * @desc 帮派技能的战力计算
 **/

public class FSGetGroupSkillFightingCalc implements IFightingCalc {

	private GroupSkillFightingCfgDAO groupSkillFightingCfgDAO;

	protected FSGetGroupSkillFightingCalc() {
		groupSkillFightingCfgDAO = GroupSkillFightingCfgDAO.getInstance();
	}

	@Override
	public int calc(Object param) {
		GroupSkillParam groupSkillParam = (GroupSkillParam) param;

		Map<Integer, Integer> groupSkillMap = groupSkillParam.getGroupSkillMap();
		if (groupSkillMap == null || groupSkillMap.isEmpty()) {
			return 0;
		}

		int fighting = 0;

		for (Entry<Integer, Integer> e : groupSkillMap.entrySet()) {
			int level = e.getValue();
			if (level <= 0) {
				continue;
			}

			OneToOneTypeFightingCfg cfg = groupSkillFightingCfgDAO.getCfgById(e.getKey().toString());
			fighting += cfg.getFighting() * level;
		}

		return fighting;
	}
}