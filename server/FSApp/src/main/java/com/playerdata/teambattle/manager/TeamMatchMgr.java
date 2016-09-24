package com.playerdata.teambattle.manager;

import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.bm.robot.RandomData;
import com.bm.robot.RobotHeroBuilder;
import com.playerdata.Player;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmySimpleInfoHelper;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.team.TeamInfo;
import com.playerdata.teambattle.cfg.TeamMatchCfg;
import com.playerdata.teambattle.cfg.TeamMatchCfgDAO;

public class TeamMatchMgr {
	
	
	private static TeamMatchMgr instance = new TeamMatchMgr();
	
	public static TeamMatchMgr getInstance(){		
		
		return instance;
	}
	
	
	public TeamMatchData newMatchTeamArmy(Player player, String copyId){
		
		TeamMatchData teamMatchData = null;
		TeamMatchCfg matchCfg = TeamMatchCfgDAO.getInstance().getCfgById(copyId);
		
		if(matchCfg!=null){
			int robotId = matchCfg.getRobotTeamId();
			RandomData randomData = RandomData.newInstance(robotId).doHeroMakeup(false);
			TeamInfo robotTeamInfo = RobotHeroBuilder.getRobotTeamInfo(randomData);	
			if(robotTeamInfo!=null){
				ArmyInfo armyInfo = AngelArrayTeamInfoHelper.parseTeamInfo2ArmyInfo(robotTeamInfo);					
				ArmyInfoSimple armySimpleInfo = ArmySimpleInfoHelper.fromArmyInfo(armyInfo);
				teamMatchData = new TeamMatchData(armySimpleInfo, randomData.asData());
			}
		}		
		
		return teamMatchData;
	}
	public TeamMatchData getMatchTeamArmy(RandomData randomData){
		TeamMatchData teamMatchData = null;
		TeamInfo robotTeamInfo = RobotHeroBuilder.getRobotTeamInfo(randomData.asData());	
		if(robotTeamInfo!=null){
			ArmyInfo armyInfo = AngelArrayTeamInfoHelper.parseTeamInfo2ArmyInfo(robotTeamInfo);					
			ArmyInfoSimple armySimpleInfo = ArmySimpleInfoHelper.fromArmyInfo(armyInfo);
			teamMatchData = new TeamMatchData(armySimpleInfo, randomData);
		}
		
		return teamMatchData;
	}


	public ArmyInfo getArmyInfo(RandomData randomData) {
		TeamInfo robotTeamInfo = RobotHeroBuilder.getRobotTeamInfo(randomData.asData());	
		ArmyInfo armyInfo = null;
		if(robotTeamInfo!=null){
			armyInfo = AngelArrayTeamInfoHelper.parseTeamInfo2ArmyInfo(robotTeamInfo);					
		}
		return armyInfo;
	}
	


}
