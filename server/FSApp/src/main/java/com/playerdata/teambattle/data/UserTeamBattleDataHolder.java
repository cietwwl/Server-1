package com.playerdata.teambattle.data;

import java.util.List;

import com.common.serverdata.ServerCommonDataHolder;
import com.playerdata.Player;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserTeamBattleDataHolder {
	private static UserTeamBattleDataHolder instance = new UserTeamBattleDataHolder();
	
	public static UserTeamBattleDataHolder getInstance() {
		return instance;
	}

	final private eSynType synType = eSynType.USER_TEAM_BATTLE;
	
	public UserTeamBattleData get(String userID) {
		return UserTeamBattleDAO.getInstance().get(userID);
	}
	
	public void update(Player player, UserTeamBattleData data) {
		UserTeamBattleDAO.getInstance().update(data);
		ClientDataSynMgr.synData(player, data, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	/**
	 * 同步数据
	 * @param player
	 */
	public void synData(Player player) {
		UserTeamBattleData userTBData = get(player.getUserId());
		userTBData.setEnimyMap(ServerCommonDataHolder.getInstance().get().getTeamBattleEnimyMap());
		if (userTBData != null) {
			ClientDataSynMgr.synData(player, userTBData, synType, eSynOpType.UPDATE_SINGLE);
		}
	}
	
	public void dailyReset(Player player) {
		UserTeamBattleData userTBData = get(player.getUserId());
		if (userTBData != null) {
			userTBData.dailyReset();
		}
		update(player, userTBData);
	}
	
	/**
	 * 当角色数据改变时，更新角色的队伍数据
	 * @param player
	 */
	public void updateArmyInfoSimple(Player player){
		UserTeamBattleData utbData = get(player.getUserId());
		if(null == utbData || null == utbData.getSelfTeamInfo() || null == utbData.getSelfTeamInfo().getUserStaticTeam()) return;
		String magicID = utbData.getSelfTeamInfo().getUserStaticTeam().getArmyMagic().getId();
		List<String> heroIdList = utbData.getSelfTeamInfo().getUserStaticTeam().getHeroIdList();
		ArmyInfoSimple simpleArmy = ArmyInfoHelper.getSimpleInfo(player.getUserId(), magicID, heroIdList);
		if(simpleArmy == null) return;
		utbData.getSelfTeamInfo().setUserStaticTeam(simpleArmy);
		update(player, utbData);
	}
}
