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
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.copypve.CopyInfoCfgDAO;
import com.rwbase.dao.copypve.pojo.CopyInfoCfg;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class UserFeatruesCeletriPenglai implements IUserFeatruesHandler{

	@Override
	public RewardBackTodaySubItem doEvent(Player player) {
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem.setId(UserFeaturesEnum.celestial_PengLaiIsland.getId());
		subItem.setCount(0);
		subItem.setMaxCount(0);
		CopyInfoCfg copyInfoCfg = CopyInfoCfgDAO.getInstance().getCfgById(UserFeatruesMgr.celestial_penglai+"");
		boolean isOpen = DateUtils.isOpenOfCelestial(copyInfoCfg.getId());		
		if(!isOpen){
			return subItem;
		}
		if(copyInfoCfg != null)subItem.setMaxCount(copyInfoCfg.getCount());
		return subItem;
	}

	@Override
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem, Player player, CfgOpenLevelLimitDAO dao) {
		int level = player.getLevel();
		if(level >= dao.checkIsOpen(eOpenLevelType.CELETRIAL, player)&&todaySubItem.getMaxCount() > 0){			
			todaySubItem.setMaxCount(CopyInfoCfgDAO.getInstance().getCfgById(UserFeatruesMgr.celestial_penglai+"").getCount());			
		}else{
			todaySubItem.setMaxCount(0);
		}
		return null;
	}

	@Override
	public String getNorReward(NormalRewardsCfg cfg,RewardBackSubItem subItem) {
		// TODO Auto-generated method stub
		return cfg.getPenglaiNorRewards();
	}

	@Override
	public String getPerReward(PerfectRewardsCfg cfg,RewardBackSubItem subItem) {
		// TODO Auto-generated method stub
		return cfg.getPenglaiPerRewards();
	}

	@Override
	public int getNorCost(NormalRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		// TODO Auto-generated method stub
		return cfg.getPenglaiNorCost();
	}

	@Override
	public int getPerCost(PerfectRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		// TODO Auto-generated method stub
		return cfg.getPenglaiPerCost();
	}

}
