package com.playerdata.teambattle.manager;

import org.apache.commons.lang3.StringUtils;

import com.bm.robot.RandomData;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.teambattle.cfg.TBBuyCostCfg;
import com.playerdata.teambattle.cfg.TBBuyCostCfgDAO;
import com.playerdata.teambattle.cfg.TeamCfg;
import com.playerdata.teambattle.cfg.TeamCfgDAO;
import com.playerdata.teambattle.data.TBTeamItem;
import com.playerdata.teambattle.data.TBTeamItemHolder;
import com.playerdata.teambattle.data.TeamHardInfo;
import com.playerdata.teambattle.data.TeamMember;
import com.playerdata.teambattle.data.UserTeamBattleData;
import com.playerdata.teambattle.data.UserTeamBattleDataHolder;
import com.playerdata.teambattle.dataForClient.StaticMemberTeamInfo;
import com.playerdata.teambattle.enums.TBMemberState;

public class UserTeamBattleDataMgr {
	private static UserTeamBattleDataMgr instance = new UserTeamBattleDataMgr();

	public static UserTeamBattleDataMgr getInstance() {
		return instance;
	}

	public void synData(Player player) {
		UserTeamBattleDataHolder.getInstance().synData(player);
	}

	/**
	 * 踢出玩家时使用的同步
	 * 
	 * @param userID
	 */
	public void synData(String userID) {
		Player player = PlayerMgr.getInstance().findPlayerFromMemory(userID);
		if (player == null)
			return;
		UserTeamBattleDataHolder.getInstance().synData(player);
	}

	/**
	 * 离开队伍（主动离开，切换队伍，被踢） 不能是机器人
	 * 
	 * @param userID
	 */
	public void leaveTeam(String userID) {
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(userID);
		if (null == utbData || StringUtils.isBlank(utbData.getTeamID())) {
			return;
		}
		TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(utbData.getTeamID());
		leaveTeam(userID, teamItem);
		utbData.clearCurrentTeam();
	}

	/**
	 * 离开队伍（主动离开，切换队伍，被踢） 支持机器人
	 * 
	 * @param userID
	 * @param eamId
	 */
	public void leaveTeam(String userID, String teamId) {
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(userID);
		if (null == utbData || StringUtils.isBlank(utbData.getTeamID())) {
			TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(teamId);
			// 判断是不是机器人离队
			TeamMember teamMember = teamItem.findMember(userID);
			StaticMemberTeamInfo robotInfo = getRobotStaticTeamInfo(teamMember);
			if (null == robotInfo) {
				return;
			}
			// 机器人离队
			leaveTeam(userID, teamItem);
		} else {
			// 玩家离队
			leaveTeam(userID);
		}
	}

	/**
	 * 离开队伍 （无论机器人还是真实玩家）
	 * 
	 * @param userID
	 * @param teamItem
	 */
	private void leaveTeam(String userID, TBTeamItem teamItem) {
		if (null == teamItem || StringUtils.isBlank(userID)) {
			return;
		}
		synchronized (teamItem) {
			TeamMember self = teamItem.findMember(userID);
			if (null != self) {
				if (self.getState().equals(TBMemberState.Ready) || self.getState().equals(TBMemberState.Fight)) {
					teamItem.removeMember(self);
					TBTeamItemMgr.getInstance().changeTeamSelectable(teamItem);
					if (!TBTeamItemMgr.getInstance().removeTeam(teamItem)) {
						TBTeamItemHolder.getInstance().synData(teamItem);
					}
				}
			}
		}
	}

	public void dailyReset(Player player) {
		UserTeamBattleDataHolder.getInstance().dailyReset(player);
	}

	/**
	 * 判断是否还有挑战次数
	 * 
	 * @param player
	 * @param hardID 副本id
	 * @return
	 */
	public boolean haveFightTimes(Player player, String hardID){
		TeamCfg teamCfg = TeamCfgDAO.getInstance().getCfgById(hardID);
		if(null == teamCfg) return false;
		if(player.getLevel() < teamCfg.getLevel()) return false;//等级还没达到开放等级，返回false
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(null == utbData) return false;
		TeamHardInfo thInfo = utbData.getFinishedHardMap().get(hardID);
		if(null == thInfo) return true;
		int totalTimes = teamCfg.getTimes();
		TBBuyCostCfgDAO tbBuyCostCfgDAO = TBBuyCostCfgDAO.getInstance();
		for(int i = 1; i <= thInfo.getBuyTimes(); i++){
			String buyId = hardID + "_" + i;
			TBBuyCostCfg buyCfg = tbBuyCostCfgDAO.getCfgById(buyId);
			if(null == buyCfg) continue;
			totalTimes += buyCfg.getNumbers();
		}
		return thInfo.getFinishTimes() < totalTimes;
	}

	/**
	 * 判断玩家的队伍是否满员（用于红点）
	 * 
	 * @param player
	 * @return
	 */
	public boolean isTeamFull(Player player) {
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if (null == utbData)
			return false;
		if (StringUtils.isBlank(utbData.getTeamID()))
			return false;
		TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(utbData.getTeamID());
		if (null == teamItem)
			return false;
		return teamItem.isFull();
	}

	public StaticMemberTeamInfo getRobotStaticTeamInfo(TeamMember member) {

		RandomData randomData = member.getRandomData();
		TeamMatchData matchTeamArmy = TeamMatchMgr.getInstance().getMatchTeamArmy(randomData);
		StaticMemberTeamInfo staticMemberTeamInfo = null;
		if (matchTeamArmy != null) {
			staticMemberTeamInfo = matchTeamArmy.toStaticMemberTeamInfo();
		}
		return staticMemberTeamInfo;
	}

	public int getTeamBattleCoin(Player player) {
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if (utbData == null) {
			return 0;
		}
		return utbData.getScore();
	}

	public boolean addTeamBattleCoin(Player player, int count) {
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if (utbData == null) {
			return false;
		}
		if (count + utbData.getScore() < 0) {
			return false;
		}
		utbData.setScore(utbData.getScore() + count);
		UserTeamBattleDataHolder.getInstance().update(player, utbData);
		return true;
	}
}
