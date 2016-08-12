package com.rwbase.common.attribute.component;

import java.util.ArrayList;
import java.util.HashMap;

import com.bm.arena.ArenaRobotDataMgr;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.attribute.impl.AbstractAttributeCalc;
import com.rwbase.dao.arena.RobotExtraAttributeCfgDAO;
import com.rwbase.dao.arena.pojo.RobotExtraAttributeTemplate;

/*
 * @author HC
 * @date 2016年7月15日 下午4:37:09
 * @Description 
 */
public class HeroExtraAttributeComponent extends AbstractAttributeCalc {

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Extra;
	}

	@Override
	protected AttributeSet calcAttribute(Player player, Hero hero) {
		if (!player.isRobot()) {
			return null;
		}

		int extraAttrId = ArenaRobotDataMgr.getMgr().getExtraAttrId(player.getUserId());

		RobotExtraAttributeTemplate extraTmp = RobotExtraAttributeCfgDAO.getCfgDAO().getRobotExtraAttributeTemplate(extraAttrId);
		if (extraTmp == null) {
			return null;
		}

		HashMap<Integer, AttributeItem> map = new HashMap<Integer, AttributeItem>();
		AttributeUtils.calcAttribute(extraTmp.getAttrDataMap(), extraTmp.getPrecentAttrDataMap(), map);
		if (map.isEmpty()) {
			return null;
		}

		return AttributeSet.newBuilder().addAttribute(new ArrayList<AttributeItem>(map.values())).build();
	}
}