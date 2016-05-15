package com.rwbase.common.attribute.calc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.log.GameLog;
import com.playerdata.team.EquipInfo;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.EquipParam;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.role.EquipAttachCfgDAO;
import com.rwbase.dao.role.pojo.EquipAttachCfg;

/*
 * @author HC
 * @date 2016年5月14日 下午7:55:00
 * @Description 
 */
public class HeroEquipAttrCalc implements IComponentCalc {

	@Override
	public AttributeSet calc(Object obj) {
		EquipParam param = (EquipParam) obj;
		String userId = param.getUserId();
		List<EquipInfo> equipList = param.getEquipList();
		if (equipList == null || equipList.isEmpty()) {
			GameLog.error("计算英雄装备属性", userId, "英雄的装备列表是空的");
			return null;
		}

		HeroEquipCfgDAO equipCfgDAO = HeroEquipCfgDAO.getInstance();
		EquipAttachCfgDAO attachCfgDAO = EquipAttachCfgDAO.getInstance();

		HashMap<Integer, AttributeItem> map = new HashMap<Integer, AttributeItem>();

		for (int i = equipList.size() - 1; i >= 0; --i) {
			EquipInfo info = equipList.get(i);
			if (info == null) {
				continue;
			}

			HeroEquipCfg cfg = equipCfgDAO.getCfgById(info.gettId());
			if (cfg == null) {
				continue;
			}

			EquipAttachCfg attachCfg = attachCfgDAO.getConfig(info.geteLevel());
			int addPrecent = 0;
			if (attachCfg != null) {
				addPrecent = attachCfg.getAttriPercent();
			}

			AttributeUtils.calcAttribute(cfg.getAttrDataMap(), cfg.getPrecentAttrDataMap(), map, addPrecent);
		}

		if (map.isEmpty()) {
			GameLog.error("计算英雄装备属性", userId, "英雄装备计算出来的属性是空的");
			return null;
		}

		GameLog.info("计算英雄装备属性", userId, AttributeUtils.partAttrMap2Str("装备", map), null);
		return new AttributeSet.Builder().addAttribute(new ArrayList<AttributeItem>(map.values())).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Equip;
	}
}