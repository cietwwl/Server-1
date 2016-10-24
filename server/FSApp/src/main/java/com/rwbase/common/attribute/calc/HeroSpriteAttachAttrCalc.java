package com.rwbase.common.attribute.calc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rwbase.common.attribute.AttributeComponentEnum;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeSet;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.attribute.IComponentCalc;
import com.rwbase.common.attribute.param.SpriteAttachParam;
import com.rwbase.dao.spriteattach.SpriteAttachAttrCfgDAO;
import com.rwbase.dao.spriteattach.SpriteAttachCfgDAO;
import com.rwbase.dao.spriteattach.SpriteAttachItem;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachAttrCfg;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachCfg;

public class HeroSpriteAttachAttrCalc implements IComponentCalc{

	@Override
	public AttributeSet calc(Object obj) {
		// TODO Auto-generated method stub
		SpriteAttachParam spriteAttachParam = (SpriteAttachParam)obj;
		List<SpriteAttachItem> items = spriteAttachParam.getItems();
		HashMap<Integer, AttributeItem> map = new HashMap<Integer, AttributeItem>();
		for (SpriteAttachItem spriteAttachItem : items) {
			int id = spriteAttachItem.getId();
			SpriteAttachCfg spriteAttachCfg = SpriteAttachCfgDAO.getInstance().getCfgById(String.valueOf(id));
			int levelPlanId = spriteAttachCfg.getLevelPlanId();
			int level = spriteAttachItem.getLevel();
			SpriteAttachAttrCfg spriteAttachAttrCfg = SpriteAttachAttrCfgDAO.getInstance().getByPlanIdAndLevel(levelPlanId, level);
			if(spriteAttachAttrCfg != null){
				AttributeUtils.calcAttribute(spriteAttachAttrCfg.getAttrDataMap(), spriteAttachAttrCfg.getPrecentAttrDataMap(), map);
			}
			
		}
		if (map.isEmpty()) {
			return null;
		}
		
		return new AttributeSet.Builder().addAttribute(new ArrayList<AttributeItem>(map.values())).build();
	}

	@Override
	public AttributeComponentEnum getComponentTypeEnum() {
		// TODO Auto-generated method stub
		return AttributeComponentEnum.Hero_SpriteAttach;
	}

}
