package com.bm.targetSell.param.attrs;

import java.util.List;
import java.util.Map;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.fixEquip.exp.FixExpEquipMgr;
import com.playerdata.fixEquip.norm.FixNormEquipMgr;
import com.playerdata.team.HeroFixEquipInfo;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;


/**
 * 英雄的神器觉醒星级  参数为英雄modelid
 * @author Alex
 *
 * 2016年11月17日 下午12:10:04
 */
public class AchieveveHeroFixEquipUpgradStar extends AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg,
			Map<String, Object> AttrMap) {
		
		HeroMgr heroMgr = player.getHeroMgr();
		String heroModelId = cfg.getParam();
		
		Hero hero = heroMgr.getHeroByModerId(player, Integer.parseInt(heroModelId));
		if(hero != null){
			List<HeroFixEquipInfo> dataInfo = FixNormEquipMgr.getInstance().getHeroFixSimpleInfo(hero.getId());//普通神器数据
			String[] attrName = cfg.getAttrName().split(",");
			for (int i = 0; i < dataInfo.size(); i++) {
				HeroFixEquipInfo info = dataInfo.get(i);
				if(info.getSlot() >= attrName.length){//这里做个容错
					GameLog.error(LogModule.COMMON.getName(), "AchiveveHeroFixEquipUpgradStar", "精准营销获取角色的神器觉醒星级时出现问题，神器id:"
							+info.getId()+"位置slot值为"+info.getSlot()+"，参数长度为"+ attrName.length, null);
					continue;
				}
				AttrMap.put(attrName[info.getSlot()], info.getStar());
			}
			
			//经验神器数据
			List<HeroFixEquipInfo> fixSimpleInfo = FixExpEquipMgr.getInstance().getHeroFixSimpleInfo(hero.getId());
			
			for (int i = 0; i < fixSimpleInfo.size(); i++) {
				HeroFixEquipInfo info = dataInfo.get(i);
				if(info.getSlot() >= attrName.length){//这里做个容错
					GameLog.error(LogModule.COMMON.getName(), "AchiveveHeroFixEquipUpgradStar", "精准营销获取角色的神器觉醒星级时出现问题，神器id:"
							+info.getId()+"位置slot值为"+info.getSlot()+"，参数长度为"+ attrName.length, null);
					continue;
				}
				AttrMap.put(attrName[info.getSlot()], info.getStar());
			}
		}
		
		
		
		
	}

}
