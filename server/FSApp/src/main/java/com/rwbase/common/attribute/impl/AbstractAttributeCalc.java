package com.rwbase.common.attribute.impl;

import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;

/*
 * @author HC
 * @date 2016年5月13日 下午3:02:12
 * @Description 
 */
public abstract class AbstractAttributeCalc implements IAttributeComponent {

	@Override
	public AttributeSet convertToAttribute(String userId, String heroId) {
		Player player = PlayerMgr.getInstance().find(userId);
		if (player == null) {
			GameLog.error("计算佣兵属性", userId, String.format("Id为[%s]的英雄查找不到自己的Player对象", heroId));
			return null;
		}

//		Hero hero = player.getHeroMgr().getHeroById(heroId);
		Hero hero = player.getHeroMgr().getHeroById(player, heroId);
		if (hero == null) {
			GameLog.error("计算佣兵属性", userId, String.format("Id为[%s]的英雄不能从Player身上查找到", heroId));
			return null;
		}

		return calcAttribute(player, hero);
	}

	protected abstract AttributeSet calcAttribute(Player player, Hero hero);
}