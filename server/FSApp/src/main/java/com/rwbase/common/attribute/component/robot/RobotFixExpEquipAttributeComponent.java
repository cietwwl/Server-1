package com.rwbase.common.attribute.component.robot;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.fixEquip.cfg.RoleFixEquipCfg;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfgDAO;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.team.HeroFixEquipInfo;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.FixExpEquipParam;
import com.rwbase.common.attribute.param.FixExpEquipParam.FixExpEquipBuilder;

/*
 * @author HC
 * @date 2016年7月14日 下午3:53:30
 * @Description 
 */
public class RobotFixExpEquipAttributeComponent implements IAttributeComponent {

	private final int heroModelId;
	private final List<HeroFixEquipInfo> fixInfo;

	public RobotFixExpEquipAttributeComponent(int heroModelId, List<HeroFixEquipInfo> fixInfo) {
		this.heroModelId = heroModelId;
		this.fixInfo = fixInfo;
	}

	@Override
	public AttributeSet convertToAttribute(String userId, String heroId) {
		RoleFixEquipCfg cfg = RoleFixEquipCfgDAO.getInstance().getConfig(String.valueOf(heroModelId));
		if (cfg == null) {
			return null;
		}

		if (fixInfo == null || fixInfo.isEmpty()) {
			return null;
		}

		List<String> expCfgIdList = cfg.getExpCfgIdList();
		List<FixExpEquipDataItem> fixExpEquipList = new ArrayList<FixExpEquipDataItem>(expCfgIdList.size());

		for (int i = 0, size = fixInfo.size(); i < size; i++) {
			HeroFixEquipInfo heroFixEquipInfo = fixInfo.get(i);
			if (heroFixEquipInfo == null) {
				continue;
			}

			String id = heroFixEquipInfo.getId();
			if (!expCfgIdList.contains(id)) {
				continue;
			}

			FixExpEquipDataItem item = new FixExpEquipDataItem();
			item.setCfgId(id);
			item.setLevel(item.getLevel());
			item.setQuality(item.getQuality());
			item.setStar(item.getStar());

			fixExpEquipList.add(item);
		}

		FixExpEquipBuilder builder = new FixExpEquipBuilder();
		builder.setUserId(userId);
		builder.setHeroId(heroId);
		builder.setFixExpEquipList(fixExpEquipList);
		FixExpEquipParam param = builder.build();

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}

		return calc.calc(param);
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fix_Exp_Equip;
	}
}