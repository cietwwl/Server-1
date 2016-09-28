package com.playerdata.teambattle.bm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.teambattle.cfg.MonsterCombinationCfg;
import com.playerdata.teambattle.cfg.MonsterCombinationDAO;
import com.playerdata.teambattle.data.TBTeamItem;
import com.playerdata.teambattle.data.TBTeamItemHolder;
import com.playerdata.teambattle.data.TeamMember;
import com.playerdata.teambattle.data.UserTeamBattleData;
import com.playerdata.teambattle.data.UserTeamBattleDataHolder;
import com.playerdata.teambattle.dataException.JoinTeamException;
import com.playerdata.teambattle.dataForClient.StaticMemberTeamInfo;
import com.playerdata.teambattle.dataForClient.TBArmyHerosInfo;
import com.playerdata.teambattle.enums.TBMemberState;
import com.playerdata.teambattle.manager.TBTeamItemMgr;
import com.playerdata.teambattle.manager.UserTeamBattleDataMgr;
import com.rwproto.TeamBattleProto.TBResultType;
import com.rwproto.TeamBattleProto.TeamBattleRspMsg.Builder;


/**
 * 组队战
 * @author aken
 *
 */
public class TeamBattleBM {
	
	private static class InstanceHolder{
		private static TeamBattleBM instance = new TeamBattleBM();
		private static AtomicLong atoTeamID = new AtomicLong(System.nanoTime());
	}
	
	public static TeamBattleBM getInstance(){
		return InstanceHolder.instance;
	}
	
	public long getNewTeamID(){
		return InstanceHolder.atoTeamID.incrementAndGet();
	}

	public void synTeamBattle(Player player, Builder tbRsp) {
		UserTeamBattleDataMgr.getInstance().synData(player);
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	public void saveTeamInfo(Player player, Builder tbRsp, TBArmyHerosInfo item) {
		ArmyInfoSimple simpleArmy = ArmyInfoHelper.getSimpleInfo(player.getUserId(), item.getMagicID(), item.getHeroIDs());
		if(simpleArmy == null) {
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("队伍数据异常，保存失败");
			return;
		}
		StaticMemberTeamInfo staticMemInfo = new StaticMemberTeamInfo();
		staticMemInfo.setUserID(player.getUserId());
		staticMemInfo.setUserStaticTeam(simpleArmy);
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		utbData.setSelfTeamInfo(staticMemInfo);
		UserTeamBattleDataHolder.getInstance().synData(player);
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	public void createTeam(Player player, Builder tbRsp, String hardID) {
		//TODO 判断是否可以创建队伍

		if(StringUtils.isBlank(hardID)){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("不存在该难度的副本");
			return;
		}
		UserTeamBattleDataMgr.getInstance().leaveTeam(player.getUserId());
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		TeamMember tMem = new TeamMember();
		tMem.setUserID(player.getUserId());
		tMem.setState(TBMemberState.Ready);
		String teamID = hardID + "_" + getNewTeamID();
		TBTeamItem teamItem = new TBTeamItem();
		teamItem.setTeamID(teamID);
		teamItem.setHardID(hardID);
		teamItem.setLeaderID(player.getUserId());
		teamItem.addMember(tMem);
		TBTeamItemHolder.getInstance().addNewTeam(teamItem);
		utbData.setTeamID(teamID);
		UserTeamBattleDataHolder.getInstance().update(player, utbData);
		UserTeamBattleDataMgr.getInstance().synData(player);
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	public void joinTeam(Player player, Builder tbRsp, String hardID) {
		//TODO 判断是否可以加入队伍，判断是否有可以加入的队伍
		
		TBTeamItem canJionTeam = TBTeamItemMgr.getInstance().getOneCanJionTeam(hardID);
		if(canJionTeam == null) {
			createTeam(player, tbRsp, hardID);
			return;
		}
		try {
			joinTeam(player, canJionTeam);
			tbRsp.setRstType(TBResultType.SUCCESS);
			canJionTeam.setSelecting(false);
		} catch (JoinTeamException e) {
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg(e.getMessage());
		}
	}

	public void acceptInvite(Player player, Builder tbRsp, String hardID, String teamID) {
		//TODO 先判断是否可以切换队伍
		
		TBTeamItem teamItem = TBTeamItemHolder.getInstance().getItem(hardID, teamID);
		if(teamItem == null) {
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("队伍不存在");
			return;
		}
		if(teamItem.isFull()) {
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("队伍已满");
			return;
		}
		try {
			joinTeam(player, teamItem);
			tbRsp.setRstType(TBResultType.SUCCESS);
		} catch (JoinTeamException e) {
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg(e.getMessage());
		}
	}

	public void setTeamFreeJion(Player player, Builder tbRsp, String teamID) {
		TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(teamID);
		if(!StringUtils.equals(teamItem.getLeaderID(), player.getUserId())){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("权限不足");
			return;
		}
		teamItem.setCanFreeJion(!teamItem.isCanFreeJion());
		tbRsp.setFreeJoin(teamItem.isCanFreeJion());
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	public void kickoffMember(Player player, Builder tbRsp, String userID, String teamID) {
		TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(teamID);
		if(!StringUtils.equals(teamItem.getLeaderID(), player.getUserId())){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("权限不足");
			return;
		}
		TeamMember kickMember = teamItem.findMember(userID);
		if(!kickMember.getState().equals(TBMemberState.Ready)){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("不能踢出非空闲状态的玩家");
			return;
		}
		UserTeamBattleDataMgr.getInstance().leaveTeam(userID);
		UserTeamBattleDataMgr.getInstance().synData(userID);
		tbRsp.setRstType(TBResultType.SUCCESS);
	}
	
	public void leaveTeam(Player player, Builder tbRsp) {
		UserTeamBattleDataMgr.getInstance().leaveTeam(player.getUserId());
		UserTeamBattleDataMgr.getInstance().synData(player);
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	public void invitePlayer(Player player, Builder tbRsp) {
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	public void startFight(Player player, Builder tbRsp, String loopID, int battleTime) {
		Map<Integer, MonsterCombinationCfg> cfgMap = MonsterCombinationDAO.getInstance().getLoopValues(loopID);
		if(cfgMap == null || !cfgMap.containsKey(battleTime)){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("关卡数据不存在");
			return;
		}
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(utbData == null || StringUtils.isBlank(utbData.getTeamID())){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("组队数据异常");
			return;
		}
		if(utbData.getFinishedLoops().contains(loopID)){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("不能重复攻击已通过的关卡");
			return;
		}
		TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(utbData.getTeamID());
		if(teamItem == null){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("组队不存在或者组队信息已过期");
			return;
		}
		TeamMember teamMember = teamItem.findMember(player.getUserId());
		if(teamMember == null){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("您已经不是该组队的成员");
			return;
		}
		if(teamMember.getState().equals(TBMemberState.Ready)) {
			teamMember.setState(TBMemberState.Fight);
			TBTeamItemHolder.getInstance().updateTeam(teamItem);
		}
		for(StaticMemberTeamInfo teamInfoSimple : teamItem.getTeamMembers()){
			if(StringUtils.equals(teamInfoSimple.getUserID(), player.getUserId())) continue;
			ArmyInfo army = ArmyInfoHelper.getArmyInfo(teamInfoSimple.getUserStaticTeam(), false);
			tbRsp.addArmyInfo(ClientDataSynMgr.toClientData(army));
		}
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	public void informFightResult(Player player, Builder tbRsp, String loopID, int battleTime, boolean isSuccess) {
		Map<Integer, MonsterCombinationCfg> cfgMap = MonsterCombinationDAO.getInstance().getLoopValues(loopID);
		if(cfgMap == null || !cfgMap.containsKey(battleTime)){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("关卡数据不存在");
			return;
		}
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(utbData == null || StringUtils.isBlank(utbData.getTeamID())){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("组队数据异常");
			return;
		}
		if(utbData.getFinishedLoops().contains(loopID)){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("不能重复结算已通过的关卡");
			return;
		}
		TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(utbData.getTeamID());
		if(teamItem == null){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("组队不存在或者组队信息已过期");
			return;
		}
		TeamMember teamMember = teamItem.findMember(player.getUserId());
		if(teamMember == null){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("您已经不是该组队的成员");
			return;
		}
		if(isSuccess){
			if(battleTime >= cfgMap.size()){
				teamMember.setState(TBMemberState.Finish);
				utbData.getFinishedHards().add(teamItem.getHardID());
				//TODO 给结算的奖励
			}else{
				teamMember.setState(TBMemberState.HalfFinish);
			}
			utbData.getFinishedLoops().add(loopID);
			TBTeamItemMgr.getInstance().synData(teamItem.getTeamID());
			UserTeamBattleDataHolder.getInstance().synData(player);
		}else{
			teamMember.setState(TBMemberState.Ready);
		}
		TBTeamItemHolder.getInstance().updateTeam(teamItem);
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	public void scoreExchage(Player player, Builder tbRsp, String rewardID, int count) {
		
		tbRsp.setRstType(TBResultType.SUCCESS);
	}
	
	private void joinTeam(Player player, TBTeamItem canJionTeam) throws JoinTeamException {
		//脱离当前的队伍
		UserTeamBattleDataMgr.getInstance().leaveTeam(player.getUserId());
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		TeamMember tMem = new TeamMember();
		tMem.setUserID(player.getUserId());
		tMem.setState(TBMemberState.Ready);
		if(!canJionTeam.addMember(tMem)){
			throw new JoinTeamException("加入失败");
		}
		utbData.setTeamID(canJionTeam.getTeamID());
		
		TBTeamItemHolder.getInstance().updateTeam(canJionTeam);
		UserTeamBattleDataHolder.getInstance().update(player, utbData);
		UserTeamBattleDataHolder.getInstance().synData(player);
		TBTeamItemMgr.getInstance().synData(canJionTeam.getId());
	}
}
