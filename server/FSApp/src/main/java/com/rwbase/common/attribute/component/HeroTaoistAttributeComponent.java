package com.rwbase.common.attribute.component;

import java.util.ArrayList;
import java.util.Map;

import com.bm.arena.ArenaRobotDataMgr;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.rw.service.TaoistMagic.ITaoistMgr;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;

/*
 * @author HC
 * @date 2016年5月23日 下午10:42:08
 * @Description 
 */
public class HeroTaoistAttributeComponent extends AbstractAttributeCalc {

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Taoist;
	}

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		// String userId = player.getUserId();
		Map<Integer, AttributeItem> map = null;
		if (!player.isRobot()) {
			ITaoistMgr taoistMgr = player.getTaoistMgr();
			map = taoistMgr.getTaoistAttrMap();
		} else {
			Map<Integer, Integer> robotTaoistMap = ArenaRobotDataMgr.getMgr().getRobotTaoistMap(player.getUserId());
			if (robotTaoistMap != null && !robotTaoistMap.isEmpty()) {
				map = TaoistMagicCfgHelper.getInstance().getEffectAttr(robotTaoistMap.entrySet());
			}
		}

		if (map == null || map.isEmpty()) {
			// GameLog.info("计算道术属性", userId, String.format("Id为[%s]英雄计算的道术属性是空的", hero.getUUId()));
			return null;
		}

		// GameLog.info("计算英雄道术属性", userId, AttributeUtils.partAttrMap2Str("道术", map), null);
		return new AttributeSet.Builder().addAttribute(new ArrayList<AttributeItem>(map.values())).build();
	}
}