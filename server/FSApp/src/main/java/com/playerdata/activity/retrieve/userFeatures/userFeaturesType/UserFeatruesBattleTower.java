package com.playerdata.activity.retrieve.userFeatures.userFeaturesType;

import com.playerdata.Player;
import com.playerdata.activity.retrieve.cfg.NormalRewardsCfg;
import com.playerdata.activity.retrieve.cfg.NormalRewardsCfgDAO;
import com.playerdata.activity.retrieve.cfg.PerfectRewardsCfg;
import com.playerdata.activity.retrieve.cfg.PerfectRewardsCfgDAO;
import com.playerdata.activity.retrieve.cfg.RewardBackCfg;
import com.playerdata.activity.retrieve.cfg.RewardBackCfgDAO;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.userFeatures.IUserFeatruesHandler;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwproto.PrivilegeProtos.PvePrivilegeNames;

public class UserFeatruesBattleTower implements IUserFeatruesHandler{

	@Override
	public RewardBackTodaySubItem doEvent(Player player) {
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem.setId(UserFeaturesEnum.battleTower.getId());
		subItem.setCount(0);
		int count = player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.maxResetCount);
		subItem.setMaxCount(count);
		return subItem;
	}

	@Override
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem, Player player, CfgOpenLevelLimitDAO dao) {
		int level = player.getLevel();
		if(level >= dao.checkIsOpen(eOpenLevelType.BATTLETOWER, player)){
			todaySubItem.setMaxCount(player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.maxResetCount));			
		}else{
			todaySubItem.setMaxCount(0);
		}	
		return null;
	}

	@Override
	public String getNorReward(NormalRewardsCfg cfg,RewardBackSubItem subItem) {
		// TODO Auto-generated method stub
		return cfg.getFengshenNorRewards();
	}

	@Override
	public String getPerReward(PerfectRewardsCfg cfg,RewardBackSubItem subItem) {
		// TODO Auto-generated method stub
		return cfg.getFengshenPerRewards();
	}

	@Override
	public int getNorCost(NormalRewardsCfg cfg) {
		// TODO Auto-generated method stub
		return cfg.getFengshenNorCost();
	}

	@Override
	public int getPerCost(PerfectRewardsCfg cfg) {
		// TODO Auto-generated method stub
		return cfg.getFengshenPerCost();
	}





	

}
