package com.playerdata.teambattle.bm;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.simple.ArmyInfoSimple;
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

	public void startFight(Player player, Builder tbRsp) {
		
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	public void informFightResult(Player player, Builder tbRsp) {
		
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	public void scoreExchage(Player player, Builder tbRsp) {
		
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
		TBTeamItemHolder.getInstance().synData(canJionTeam);
	}
}
