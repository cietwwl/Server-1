package com.rwbase.common.attribute.component;

import java.util.Map;

import com.bm.arena.ArenaRobotDataMgr;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;
import com.rwbase.common.attribute.param.FettersParam;
import com.rwbase.common.attribute.param.FettersParam.FettersBuilder;
import com.rwbase.dao.fetters.pojo.SynConditionData;
import com.rwbase.dao.fetters.pojo.SynFettersData;

/*
 * @author HC
 * @date 2016年5月12日 下午3:18:59
 * @Description 
 */
public class HeroFettersAttributeComponent extends AbstractAttributeCalc {

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		// String userId = player.getUserId();
		Map<Integer, SynConditionData> openMap = null;
		if (!player.isRobot()) {
			SynFettersData heroFetters = player.getHeroFettersByModelId(hero.getModelId());
			if (heroFetters == null) {
				// GameLog.error("计算英雄羁绊属性", userId, String.format("Id为[%s]模版为[%s]的英雄没有激活的羁绊数据", hero.getUUId(), hero.getModelId()));
				return null;
			}

			openMap = heroFetters.getOpenList();
		} else {
			openMap = ArenaRobotDataMgr.getMgr().getHeroFettersInfo(player.getUserId(), hero.getModelId());
		}

		if (openMap == null || openMap.isEmpty()) {
			// GameLog.error("计算英雄羁绊属性", userId, String.format("Id为[%s]模版为[%s]的英雄所有的羁绊都没有被激活过", hero.getUUId(), hero.getModelId()));
			return null;
		}

		FettersBuilder builder = new FettersBuilder();
		builder.setUserId(player.getUserId());
		builder.setHeroId(hero.getUUId());
		builder.setOpenMap(openMap);
		FettersParam param = builder.build();

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}

		return calc.calc(param);
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fetters;
	}
}