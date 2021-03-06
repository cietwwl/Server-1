package com.playerdata.activity.retrieve.userFeatures.userFeaturesType;

import com.playerdata.Player;
import com.playerdata.activity.retrieve.ActivityRetrieveTypeHelper;
import com.playerdata.activity.retrieve.cfg.NormalRewardsCfg;
import com.playerdata.activity.retrieve.cfg.PerfectRewardsCfg;
import com.playerdata.activity.retrieve.cfg.RewardBackCfg;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.userFeatures.IUserFeatruesHandler;
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.rwbase.dao.copypve.CopyInfoCfgDAO;
import com.rwbase.dao.copypve.pojo.CopyInfoCfg;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class UserFeatruesCeletriKunlun implements IUserFeatruesHandler{

	@Override
	public RewardBackTodaySubItem doEvent() {
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem.setId(UserFeaturesEnum.celestial_KunLunWonderLand.getId());
		subItem.setCount(0);
		subItem.setMaxCount(0);
		CopyInfoCfg copyInfoCfg = CopyInfoCfgDAO.getInstance().getCfgById(UserFeatruesMgr.celestial_kunlun+"");
		boolean isOpen = ActivityRetrieveTypeHelper.isOpenOfCelestial(copyInfoCfg.getId());		
		if(!isOpen){
			return subItem;
		}
		if(copyInfoCfg != null)subItem.setMaxCount(copyInfoCfg.getCount());
		return subItem;
	}

	@Override
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem, Player player, CfgOpenLevelLimitDAO dao) {
		int level = player.getLevel();
		//部分功能当天不开放的话返回-1，必须加多一个判断
		if(dao.isOpen(eOpenLevelType.CELETRIAL, player) && todaySubItem.getMaxCount() > 0){			
			todaySubItem.setMaxCount(CopyInfoCfgDAO.getInstance().getCfgById(UserFeatruesMgr.celestial_kunlun+"").getCount());			
		}else{
			todaySubItem.setMaxCount(0);
		}
		return null;
	}

	@Override
	public String getNorReward(NormalRewardsCfg cfg,RewardBackSubItem subItem) {
		// TODO Auto-generated method stub
		return cfg.getKunlunNorRewards();
	}

	@Override
	public String getPerReward(PerfectRewardsCfg cfg,RewardBackSubItem subItem) {
		// TODO Auto-generated method stub
		return cfg.getKunlunPerRewards();
	}

	@Override
	public int getNorCost(NormalRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		// TODO Auto-generated method stub
		return cfg.getKunlunNorCost();
	}

	@Override
	public int getPerCost(PerfectRewardsCfg cfg,RewardBackSubItem subItem,RewardBackCfg mainCfg) {
		// TODO Auto-generated method stub
		return cfg.getKunlunPerCost();
	}
	
}
