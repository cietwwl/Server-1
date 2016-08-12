package com.rwbase.common.attribute.component.robot;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.fixEquip.cfg.RoleFixEquipCfg;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfgDAO;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.playerdata.team.HeroFixEquipInfo;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.IAttributeComponent;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.FixNormEquipParam;
import com.rwbase.common.attribute.param.FixNormEquipParam.FixNormEquipBuilder;

/*
 * @author HC
 * @date 2016年7月14日 下午3:54:21
 * @Description 
 */
public class RobotFixNormEquipAttributeComponent implements IAttributeComponent {

	private final int heroModelId;
	private final List<HeroFixEquipInfo> fixInfo;

	public RobotFixNormEquipAttributeComponent(int heroModelId, List<HeroFixEquipInfo> fixInfo) {
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

		List<String> normCfgIdList = cfg.getNormCfgIdList();
		List<FixNormEquipDataItem> fixNormEquipList = new ArrayList<FixNormEquipDataItem>(normCfgIdList.size());

		for (int i = 0, size = fixInfo.size(); i < size; i++) {
			HeroFixEquipInfo heroNormEquipInfo = fixInfo.get(i);
			if (heroNormEquipInfo == null) {
				continue;
			}

			String id = heroNormEquipInfo.getId();
			if (!normCfgIdList.contains(id)) {
				continue;
			}

			FixNormEquipDataItem item = new FixNormEquipDataItem();
			item.setCfgId(id);
			item.setLevel(heroNormEquipInfo.getLevel());
			item.setQuality(heroNormEquipInfo.getQuality());
			item.setStar(heroNormEquipInfo.getStar());

			fixNormEquipList.add(item);
		}

		FixNormEquipBuilder builder = new FixNormEquipBuilder();
		builder.setUserId(userId);
		builder.setHeroId(heroId);
		builder.setFixNormEquipList(fixNormEquipList);
		FixNormEquipParam param = builder.build();

		IComponentCalc calc = AttributeBM.getComponentCalc(getComponentTypeEnum());
		if (calc == null) {
			return null;
		}

		return calc.calc(param);
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		return AttributeComponentEnum.Hero_Fix_Norm_Equip;
	}
}