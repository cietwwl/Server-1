package com.playerdata.activity.retrieve.userFeatures.userFeaturesType;

import com.playerdata.Player;
import com.playerdata.activity.retrieve.cfg.RewardBackCfgDAO;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.userFeatures.IUserFeatruesHandler;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.rwbase.dao.power.RoleUpgradeCfgDAO;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;

public class UserFeatruesPower implements IUserFeatruesHandler{

	@Override
	public RewardBackTodaySubItem doEvent(Player player) {
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem.setId(UserFeaturesEnum.power.getId());
		subItem.setCount(0);
		RoleUpgradeCfg cfg = (RoleUpgradeCfg) RoleUpgradeCfgDAO.getInstance().getCfgById(String.valueOf(player.getLevel()));
		subItem.setMaxCount(cfg.getMostPower());
		return subItem;
	}
	
	@Override
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem, String userId, RewardBackCfgDAO dao) {
		// TODO Auto-generated method stub
		return null;
	}

}
