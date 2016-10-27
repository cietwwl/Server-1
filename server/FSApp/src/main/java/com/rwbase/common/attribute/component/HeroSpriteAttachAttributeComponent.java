package com.rwbase.common.attribute.component;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.SpriteAttachMgr;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;
import com.rwbase.common.attribute.param.SpriteAttachParam;
import com.rwbase.common.attribute.param.SpriteAttachParam.SpriteAttachBuilder;
import com.rwbase.dao.spriteattach.SpriteAttachItem;

public class HeroSpriteAttachAttributeComponent extends AbstractAttributeCalc{

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_SpriteAttach;
	}

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		String userId = player.getUserId();
		String heroId = hero.getUUId();
		List<SpriteAttachItem> spriteAttachItemList = SpriteAttachMgr.getInstance().getSpriteAttachHolder().getSpriteAttachItemList(heroId);


		SpriteAttachParam.SpriteAttachBuilder builder = new SpriteAttachBuilder();
		builder.setUserId(userId);
		builder.setHeroId(hero.getUUId());
		builder.setItems(spriteAttachItemList);

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}
		return calc.calc(builder.build());
	}

}
