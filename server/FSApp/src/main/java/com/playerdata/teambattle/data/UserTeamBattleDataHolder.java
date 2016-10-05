package com.playerdata.teambattle.data;

import java.util.List;

import com.common.serverdata.ServerCommonDataHolder;
import com.playerdata.Player;
import com.playerdata.army.ArmyFashion;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.teambattle.dataForClient.StaticMemberTeamInfo;
import com.rw.service.fashion.FashionHandle;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.FashionServiceProtos.FashionUsed;

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
	
	public void updateWithoutSyn(Player player, UserTeamBattleData data) {
		UserTeamBattleDAO.getInstance().update(data);
	}
	
	/**
	 * 同步数据
	 * @param player
	 */
	public void synData(Player player) {
		UserTeamBattleData userTBData = get(player.getUserId());
		if (userTBData != null) {
			userTBData.setEnimyMap(ServerCommonDataHolder.getInstance().get().getTeamBattleEnimyMap());
			ClientDataSynMgr.synData(player, userTBData, synType, eSynOpType.UPDATE_SINGLE);
		}
	}
	
	public void dailyReset(Player player) {
		UserTeamBattleData userTBData = get(player.getUserId());
		if (userTBData != null && userTBData.dailyReset()) {
			update(player, userTBData);
		}
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
	
	/**
	 * 当角色时装变化时
	 * @param player
	 */
	public void updateUserTBFashion(Player player){
		UserTeamBattleData utbData = get(player.getUserId());
		if(null == utbData || null == utbData.getSelfTeamInfo() || null == utbData.getSelfTeamInfo().getUserStaticTeam()) return;
		StaticMemberTeamInfo teamMemInfo = utbData.getSelfTeamInfo();
		teamMemInfo.setFashionUsing(toArmyFashionFromBuilder(player.getUserId()));
		updateWithoutSyn(player, utbData);
	}

	public static ArmyFashion toArmyFashionFromBuilder(String userId) {
		FashionUsed.Builder builder = FashionHandle.getInstance().getFashionUsedProto(userId);
		ArmyFashion fashion = new ArmyFashion();
		fashion.setSuitId(builder.getSuitId());
		fashion.setWingId(builder.getWingId());
		fashion.setPetId(builder.getPetId());
		return fashion;
	}
}
