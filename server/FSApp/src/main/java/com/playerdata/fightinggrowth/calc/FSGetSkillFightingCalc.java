package com.playerdata.fightinggrowth.calc;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.playerdata.team.SkillInfo;
import com.rwbase.common.attribute.param.SkillParam;
import com.rwbase.dao.fighting.SkillFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.SkillFightingCfg;
import com.rwbase.dao.role.RoleCfgDAO;

/**
 * @Author HC
 * @date 2016年10月25日 下午1:00:46
 * @desc 获取个人技能的战力
 **/

public class FSGetSkillFightingCalc implements IFightingCalc {

	private Comparator<SkillInfo> skillInfoComparator = new Comparator<SkillInfo>() {

		@Override
		public int compare(SkillInfo o1, SkillInfo o2) {
			int skillId1 = Integer.parseInt(o1.getSkillId().split("_")[0]);
			int skillId2 = Integer.parseInt(o2.getSkillId().split("_")[0]);
			return skillId1 - skillId2;
		}
	};

	private SkillFightingCfgDAO skillFightingCfgDAO;
	private RoleCfgDAO roleCfgDAO;

	protected FSGetSkillFightingCalc() {
		skillFightingCfgDAO = SkillFightingCfgDAO.getInstance();
		roleCfgDAO = RoleCfgDAO.getInstance();
	}

	@Override
	public int calc(Object param) {
		SkillParam skillParam = (SkillParam) param;

		List<SkillInfo> skillList = skillParam.getSkillList();
		if (skillList == null || skillList.isEmpty()) {
			return 0;
		}

		Collections.sort(skillList, skillInfoComparator);

		// 获取英雄的普攻技能Id
		String attackId = roleCfgDAO.getAttackId(skillParam.getHeroId());

		int fighting = 0;
		for (int i = 0, j = 0, size = skillList.size(); i < size; i++) {
			SkillInfo skillInfo = skillList.get(i);

			String skillId = skillInfo.getSkillId();
			if (skillId.equals(attackId)) {
				continue;
			}

			int skillLevel = skillInfo.getSkillLevel();
			if (skillLevel <= 0) {
				continue;
			}

			SkillFightingCfg skillFightingCfg = skillFightingCfgDAO.getByLevel(skillLevel);
			fighting += skillFightingCfg.getFightingOfIndex(++j);
		}

		return fighting;
	}
}