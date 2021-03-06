package com.playerdata.fightinggrowth.fightingfunc;

import java.util.Collections;
import java.util.List;

import com.playerdata.FightingCalculator;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.impl.AttributeFormula;
import com.rwbase.common.attribute.param.HeroBaseParam;
import com.rwbase.common.attribute.param.HeroBaseParam.HeroBaseBuilder;
import com.rwbase.dao.fighting.ExpectedHeroStatusCfgDAO;
import com.rwbase.dao.fighting.pojo.ExpectedHeroStatusCfg;

/**
 * 
 * 玩家在当前等级，基础属性所能达到的最高战力
 * 
 * @author CHEN.P
 *
 */
public class FSGetBasicMaxFightingFunc implements IFunction<Player, Integer> {
	
	private static FSGetBasicMaxFightingFunc _instance = new FSGetBasicMaxFightingFunc();

	private AttributeFormula _formula = new AttributeFormula();
	
	protected FSGetBasicMaxFightingFunc() {
		
	}
	
	public static FSGetBasicMaxFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player) {
		ExpectedHeroStatusCfg cfg = ExpectedHeroStatusCfgDAO.getInstance().getCfgById(String.valueOf(player.getLevel()));
		Hero mainHero = player.getMainRoleHero();
		HeroBaseParam.HeroBaseBuilder builder = new HeroBaseBuilder();
		builder.setUserId(player.getUserId());
		builder.setHeroId(player.getMainRoleHero().getUUId());
		builder.setHeroTmpId(mainHero.getModeId() + "_" + cfg.getExpectedStar()); // 英雄的模板id构成：modeId + 星级
		builder.setLevel(mainHero.getLevel());
		builder.setQualityId(String.valueOf(cfg.getExpectedQuality()));
		IComponentCalc componentCalc = AttributeBM.getComponentCalc(AttributeComponentEnum.Hero_Base);
		AttributeSet calc = componentCalc.calc(builder.build());
		List<AttributeItem> list;
		if (calc != null) {
			list = calc.getReadOnlyAttributes();
		} else {
			list = Collections.emptyList();
		}
		AttrData data = _formula.convertOne(list, false);
		return FightingCalculator.calOnlyAttributeFighting(player.getTemplateId(), data) * cfg.getExpectedHeroCount();
	}

}
