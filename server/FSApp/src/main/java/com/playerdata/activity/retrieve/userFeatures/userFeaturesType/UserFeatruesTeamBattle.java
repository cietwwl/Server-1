package com.playerdata.activity.retrieve.userFeatures.userFeaturesType;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.retrieve.cfg.RewardBackCfgDAO;
import com.playerdata.activity.retrieve.data.RewardBackSubItem;
import com.playerdata.activity.retrieve.data.RewardBackTodaySubItem;
import com.playerdata.activity.retrieve.userFeatures.IUserFeatruesHandler;
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.playerdata.teambattle.cfg.TeamCfg;
import com.playerdata.teambattle.cfg.TeamCfgDAO;
import com.rwbase.dao.copypve.CopyInfoCfgDAO;
import com.rwbase.dao.copypve.pojo.CopyInfoCfg;

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
	public RewardBackSubItem doFresh(RewardBackTodaySubItem todaySubItem, String userId, RewardBackCfgDAO dao) {
		// TODO Auto-generated method stub
		return null;
	}

}
