package com.playerdata.activity.retrieve.userFeatures.userFeaturesType;

import com.playerdata.Player;
import com.playerdata.activity.retrieve.cfg.RewardBackCfgDAO;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.userFeatures.IUserFeatruesHandler;
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.rwbase.dao.copypve.CopyInfoCfgDAO;
import com.rwbase.dao.copypve.pojo.CopyInfoCfg;

public class UserFeatruesCeletriPenglai implements IUserFeatruesHandler{

	@Override
	public RewardBackTodaySubItem doEvent(Player player) {
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem.setId(UserFeaturesEnum.celestial_PengLaiIsland.getId());
		subItem.setCount(0);
		CopyInfoCfg copyInfoCfg = CopyInfoCfgDAO.getInstance().getCfgById(UserFeatruesMgr.celestial_penglai+"");
		subItem.setMaxCount(0);
		if(copyInfoCfg != null)subItem.setMaxCount(copyInfoCfg.getCount());
		return subItem;
	}

	@Override
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem, String userId, RewardBackCfgDAO dao) {
		// TODO Auto-generated method stub
		return null;
	}

}
