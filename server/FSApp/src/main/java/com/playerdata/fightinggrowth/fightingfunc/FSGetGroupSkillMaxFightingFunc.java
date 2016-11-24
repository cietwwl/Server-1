package com.playerdata.fightinggrowth.fightingfunc;

import java.util.Enumeration;

import com.playerdata.Player;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.ExpectedHeroStatusCfgDAO;
import com.rwbase.dao.fighting.GroupSkillFightingCfgDAO;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.db.GroupSkillItem;

public class FSGetGroupSkillMaxFightingFunc implements IFunction<Player, Integer> {
	
	private static FSGetGroupSkillMaxFightingFunc _instance = new FSGetGroupSkillMaxFightingFunc();

	private GroupSkillFightingCfgDAO _groupSkillFightingCfgDAO;
	private ExpectedHeroStatusCfgDAO _expectedHerStatusCfgDAO;
	
	protected FSGetGroupSkillMaxFightingFunc() {
		_groupSkillFightingCfgDAO = GroupSkillFightingCfgDAO.getInstance();
		_expectedHerStatusCfgDAO = ExpectedHeroStatusCfgDAO.getInstance();
	}
	
	public static FSGetGroupSkillMaxFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player) {
		if (GroupHelper.hasGroup(player.getUserId())) {
			int fighting = 0;
			int exptectedHeroCount = _expectedHerStatusCfgDAO.getExpectedHeroCount(player.getLevel());
			Group group = GroupHelper.getGroup(player);
			Enumeration<GroupSkillItem> allGroupSkills = group.getGroupBaseDataMgr().getGroupData().getResearchSkill();
			GroupSkillItem gsk;
			while (allGroupSkills.hasMoreElements()) {
				gsk = allGroupSkills.nextElement();
				if (gsk.getLevel() > 0) {
					fighting += _groupSkillFightingCfgDAO.getCfgById(gsk.getId()).getFighting() * gsk.getLevel();
				}
			}
			fighting *= exptectedHeroCount;
			return fighting;
		}
		return 0;
	}

}
