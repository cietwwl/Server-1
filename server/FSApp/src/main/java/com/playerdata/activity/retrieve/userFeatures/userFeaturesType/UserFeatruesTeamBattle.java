package com.playerdata.activity.retrieve.userFeatures.userFeaturesType;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.retrieve.cfg.NormalRewardsCfg;
import com.playerdata.activity.retrieve.cfg.NormalRewardsCfgDAO;
import com.playerdata.activity.retrieve.cfg.PerfectRewardsCfg;
import com.playerdata.activity.retrieve.cfg.PerfectRewardsCfgDAO;
import com.playerdata.activity.retrieve.cfg.RewardBackCfgDAO;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.userFeatures.IUserFeatruesHandler;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.playerdata.teambattle.cfg.TeamCfg;
import com.playerdata.teambattle.cfg.TeamCfgDAO;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class UserFeatruesTeamBattle implements	IUserFeatruesHandler{

	@Override
	public RewardBackTodaySubItem doEvent(Player player) {
		RewardBackTodaySubItem subItem = new RewardBackTodaySubItem();
		subItem.setId(UserFeaturesEnum.teamBattle.getId());
		subItem.setCount(0);		
		int tmp = 0;
		subItem.setMaxCount(tmp);
		List<TeamCfg> teamList = TeamCfgDAO.getInstance().getAllCfg();
		int level = player.getLevel();
		for(TeamCfg cfg : teamList){
			if(level >= cfg.getLevel())tmp += cfg.getTimes();
		}
		subItem.setMaxCount(tmp);
		return subItem;
	}

	@Override
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem, Player player, CfgOpenLevelLimitDAO dao) {
		int tmp = 0;
		List<TeamCfg> teamList = TeamCfgDAO.getInstance().getAllCfg();
		int level = player.getLevel();
		for(TeamCfg cfg : teamList){
			if(level >= cfg.getLevel())tmp += cfg.getTimes();
		}
		todaySubItem.setMaxCount(tmp);		
		return null;
	}

	@Override
	public String getNorReward(NormalRewardsCfg cfg,RewardBackSubItem subItem) {
		// TODO Auto-generated method stub
		return cfg.getXinmo1NorRewards();
	}

	@Override
	public String getPerReward(PerfectRewardsCfg cfg,RewardBackSubItem subItem) {
		// TODO Auto-generated method stub
		return cfg.getXinmo1PerRewards();
	}

	@Override
	public int getNorCost(NormalRewardsCfg cfg) {
		// TODO Auto-generated method stub
		return cfg.getXinmo1NorCost();
	}

	@Override
	public int getPerCost(PerfectRewardsCfg cfg) {
		// TODO Auto-generated method stub
		return cfg.getXinmo1PerCost();
	}

}
