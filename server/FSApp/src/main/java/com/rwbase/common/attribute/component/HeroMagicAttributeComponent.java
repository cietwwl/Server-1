package com.rwbase.common.attribute.component;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;
import com.rwbase.common.attribute.param.MagicParam;
import com.rwbase.common.attribute.param.MagicParam.MagicBuilder;
import com.rwbase.dao.item.pojo.ItemData;

/*
 * @author HC
 * @date 2016年5月14日 下午5:11:16
 * @Description 
 */
public class HeroMagicAttributeComponent extends AbstractAttributeCalc {

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		String userId = player.getUserId();
		ItemData magic = player.getMagic();
		if (magic == null) {
			// GameLog.error("计算法宝属性", userId, String.format("Id为[%s]的英雄获取不到角色的法宝", hero.getUUId()));
			return null;
		}

		int modelId = magic.getModelId();// 法宝的模版Id
		int magicLevel = magic.getMagicLevel();// 法宝的等级
		if (magicLevel <= 0) {
			// GameLog.error("计算法宝属性", userId, String.format("主角法宝[%s]，法宝等级[%s]，不能计算属性", modelId, magicLevel));
			return null;
		}

		MagicParam.MagicBuilder builder = new MagicBuilder();
		builder.setUserId(userId);
		builder.setHeroId(hero.getUUId());
		builder.setMagicId(String.valueOf(modelId));
		builder.setMagicLevel(magicLevel);

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			// GameLog.error("计算英雄法宝属性", player.getUserId(), String.format("Id为[%s]的英雄[%s]对应类型的IComponentCacl的实现类为Null", hero.getUUId(),
			// getComponentTypeEnum()));
			return null;
		}
		return calc.calc(builder.build());
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Magic;
	}
}