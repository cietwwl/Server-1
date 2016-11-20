package com.bm.targetSell.param.attrs;

import java.util.List;
import java.util.Map;

import com.bm.targetSell.param.TargetSellRoleChange;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.fixEquip.exp.FixExpEquipMgr;
import com.playerdata.fixEquip.norm.FixNormEquipMgr;
import com.playerdata.hero.core.FSHero;
import com.playerdata.hero.core.FSHeroMgr;
import com.playerdata.team.HeroFixEquipInfo;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;
import com.rwbase.dao.user.User;


/**
 * 英雄的神器觉醒星级  参数为英雄modelid
 * @author Alex
 *
 * 2016年11月17日 下午12:10:04
 */
public class AchieveveHeroFixEquipUpgradStar implements AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg,
			Map<String, Object> AttrMap) {
		
		HeroMgr heroMgr = player.getHeroMgr();
		int heroModelId = Integer.parseInt(cfg.getParam());
		
		Hero hero = null;
		if(heroModelId == MainRoleModelID){
			hero = player.getMainRoleHero();
		}else{
			hero = heroMgr.getHeroByModerId(player, heroModelId);
		}
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

	@Override
	public void addHeroAttrs(String userID, String heroID, EAchieveType achieveType, TargetSellRoleChange value) {

		//因为神器数据没有保存角色的数据，所以要在这里补加进去
		FSHero hero = FSHeroMgr.getInstance().getHeroById(userID, heroID);
		if(hero == null){
			return;
		}
		int heroModelID = userID.equals(heroID) ? MainRoleModelID : hero.getModeId();
		
		BenefitAttrCfg cfg = BenefitAttrCfgDAO.getInstance().getCfgByHeroModelIdAndProcessType(heroModelID, achieveType.getId());
		if(cfg != null){
			value.addChange(cfg.getId());
		}
		
	}

}
