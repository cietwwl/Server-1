package com.playerdata.fightinggrowth.fightingfunc;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.playerdata.team.SkillInfo;
import com.rwbase.common.IFunction;
import com.rwbase.common.attribute.param.SkillParam.SkillBuilder;
import com.rwbase.dao.fighting.ExpectedHeroStatusCfgDAO;
import com.rwbase.dao.fighting.pojo.ExpectedHeroStatusCfg;
import com.rwbase.dao.skill.pojo.SkillItem;

public class FSGetSkillMaxFightingFunc implements IFunction<Player, Integer>{

	private static FSGetSkillMaxFightingFunc _instance = new FSGetSkillMaxFightingFunc();
	
	
	private ExpectedHeroStatusCfgDAO _expectedStatusCfgDAO;
	protected FSGetSkillMaxFightingFunc() {
		_expectedStatusCfgDAO = ExpectedHeroStatusCfgDAO.getInstance();
	}
	
	public static FSGetSkillMaxFightingFunc getInstance() {
		return _instance;
	}
	
	@Override
	public Integer apply(Player player) {
		ExpectedHeroStatusCfg heroStatusCfg = _expectedStatusCfgDAO.getCfgById(String.valueOf(player.getLevel()));
		List<SkillItem> skillList = player.getSkillMgr().getSkillList(player.getUserId());
		List<SkillInfo> infoList = new ArrayList<SkillInfo>(skillList.size());
		SkillInfo info;
		for (SkillItem skillItem : skillList) {
			info = new SkillInfo();
			info.setSkillId(skillItem.getSkillId());
			info.setSkillLevel(player.getLevel());
			infoList.add(info);
		}
		SkillBuilder builder = new SkillBuilder();
		builder.setHeroTemplateId(player.getTemplateId());
		builder.setUserId(player.getUserId());
		builder.setSkillList(infoList);
		int value = FightingCalcComponentType.SKILL.calc.calc(builder.build());
		return value * heroStatusCfg.getExpectedHeroCount();
	}

}
