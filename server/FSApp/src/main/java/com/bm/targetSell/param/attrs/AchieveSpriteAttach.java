package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.bm.targetSell.param.TargetSellRoleChange;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.hero.core.FSHero;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;
import com.rwbase.dao.user.User;

/**
 * 检查英雄附灵 参数：英雄modelID
 * @author Alex
 * 2017年1月6日 下午4:53:02
 */
public class AchieveSpriteAttach implements AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap) {
		HeroMgr heroMgr = player.getHeroMgr();
		int heroModelId = Integer.parseInt(cfg.getParam());
		
		Hero hero = null;
		if(heroModelId == MainRoleModelID){
			hero = player.getMainRoleHero();
		}else{
			hero = heroMgr.getHeroByModerId(player, heroModelId);
		}
		
		if(hero != null){
			
		}
	}

	@Override
	public void addHeroAttrs(String userID, String heroID, EAchieveType achieveType, TargetSellRoleChange value) {
		
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
