package com.rwbase.common.attribute.impl;

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
			return null;
		}

		Hero hero = player.getHeroMgr().getHeroById(heroId);
		if (hero == null) {
			return null;
		}

		return calcAttribute(player, hero);
	}

	protected abstract AttributeSet calcAttribute(Player player, Hero hero);
}