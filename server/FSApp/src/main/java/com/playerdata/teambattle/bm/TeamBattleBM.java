package com.playerdata.teambattle.bm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

import com.bm.chat.ChatBM;
import com.bm.chat.ChatInteractiveType;
import com.bm.group.GroupBM;
import com.common.serverdata.ServerCommonData;
import com.common.serverdata.ServerCommonDataHolder;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.teambattle.cfg.MonsterCombinationCfg;
import com.playerdata.teambattle.cfg.MonsterCombinationDAO;
import com.playerdata.teambattle.cfg.TeamCfg;
import com.playerdata.teambattle.cfg.TeamCfgDAO;
import com.playerdata.teambattle.cfg.TeamStoreCfg;
import com.playerdata.teambattle.cfg.TeamStoreCfgDAO;
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
import com.rw.service.Email.EmailUtils;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
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

	/**
	 * 同步个人组队战信息，以及组队队伍的信息
	 * @param player
	 * @param tbRsp
	 */
	public void synTeamBattle(Player player, Builder tbRsp) {
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(utbData != null) utbData.setSynTeam(true);
		UserTeamBattleDataMgr.getInstance().synData(player);
		TBTeamItemMgr.getInstance().synData(player);
		tbRsp.setRstType(TBResultType.SUCCESS);
	}
	
	/**
	 * 不再同步组队数据
	 * @param player
	 * @param tbRsp
	 */
	public void nonSynTeamBattle(Player player, Builder tbRsp){
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(utbData != null) utbData.setSynTeam(false);
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	/**
	 * 保存组队战个人队伍信息
	 * @param player
	 * @param tbRsp
	 * @param item
	 */
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
		HashMap<String, Integer> heroPosMap = new HashMap<String, Integer>();
		if(item.getHeroIDs() != null){
			for(int i = 0; i < item.getHeroIDs().size(); i++){
				heroPosMap.put(item.getHeroIDs().get(i), i + 1);
			}
		}
		staticMemInfo.setHeroPosMap(heroPosMap);
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		utbData.setSelfTeamInfo(staticMemInfo);
		UserTeamBattleDataHolder.getInstance().synData(player);
		if(StringUtils.isNotBlank(utbData.getTeamID())){
			TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(utbData.getTeamID());
			if(teamItem != null) TBTeamItemMgr.getInstance().synData(utbData.getTeamID());
		}
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	/**
	 * 创建队伍
	 * @param player
	 * @param tbRsp
	 * @param hardID
	 */
	public void createTeam(Player player, Builder tbRsp, String hardID) {
		if(StringUtils.isBlank(hardID)){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("不存在该难度的副本");
			return;
		}
		TeamCfg teamCfg = TeamCfgDAO.getInstance().getCfgById(hardID);
		if(teamCfg == null){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("不存在该难度的副本");
			return;
		}
		if(player.getLevel() < teamCfg.getLevel()){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("该难度的副本" + teamCfg.getLevel() + "级开放");
			return;
		}
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(StringUtils.isNotBlank(utbData.getTeamID())){
			TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(utbData.getTeamID());
			if(teamItem != null){
				TeamMember member = teamItem.findMember(player.getUserId());
				if(member != null && member.getState().equals(TBMemberState.HalfFinish)){
					tbRsp.setRstType(TBResultType.DATA_ERROR);
					tbRsp.setTipMsg("请您先完成当前的副本");
					return;
				}
			}
		}
		UserTeamBattleDataMgr.getInstance().leaveTeam(player.getUserId());
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
		utbData.setMemPos("");
		UserTeamBattleDataHolder.getInstance().update(player, utbData);
		UserTeamBattleDataMgr.getInstance().synData(player);
		TBTeamItemMgr.getInstance().synData(player);
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	/**
	 * 快速加入队伍
	 * @param player
	 * @param tbRsp
	 * @param hardID
	 */
	public void joinTeam(Player player, Builder tbRsp, String hardID) {
		if(StringUtils.isBlank(hardID)){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("不存在该难度的副本");
			return;
		}
		TeamCfg teamCfg = TeamCfgDAO.getInstance().getCfgById(hardID);
		if(teamCfg == null){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("不存在该难度的副本");
			return;
		}
		if(player.getLevel() < teamCfg.getLevel()){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("该难度的副本" + teamCfg.getLevel() + "级开放");
			return;
		}
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(StringUtils.isNotBlank(utbData.getTeamID())){
			TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(utbData.getTeamID());
			if(teamItem != null){
				TeamMember member = teamItem.findMember(player.getUserId());
				if(member != null && member.getState().equals(TBMemberState.HalfFinish)){
					tbRsp.setRstType(TBResultType.DATA_ERROR);
					tbRsp.setTipMsg("请您先完成当前的副本");
					return;
				}
			}
		}
		TBTeamItem canJionTeam = TBTeamItemMgr.getInstance().getOneCanJionTeam(hardID);
		if(canJionTeam == null) {
			createTeam(player, tbRsp, hardID);
			return;
		}
		try {
			joinTeam(player, canJionTeam);
			canJionTeam.setSelecting(false);
			tbRsp.setRstType(TBResultType.SUCCESS);
		} catch (JoinTeamException e) {
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg(e.getMessage());
		}
	}

	/**
	 * 接受组队邀请
	 * @param player
	 * @param tbRsp
	 * @param hardID
	 * @param teamID
	 */
	public void acceptInvite(Player player, Builder tbRsp, String hardID, String teamID) {
		if(StringUtils.isBlank(hardID)){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("不存在该难度的副本");
			return;
		}
		TeamCfg teamCfg = TeamCfgDAO.getInstance().getCfgById(hardID);
		if(teamCfg == null){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("不存在该难度的副本");
			return;
		}
		if(player.getLevel() < teamCfg.getLevel()){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("该难度的副本" + teamCfg.getLevel() + "级开放");
			return;
		}
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(StringUtils.isNotBlank(utbData.getTeamID())){
			//获取的是玩家可能已经存在的队伍
			TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(utbData.getTeamID());
			if(teamItem != null){
				TeamMember member = teamItem.findMember(player.getUserId());
				if(member != null && StringUtils.equals(teamItem.getTeamID(), teamID)){
					tbRsp.setRstType(TBResultType.DATA_ERROR);
					tbRsp.setTipMsg("已是该队成员，无需重新加入");
					return;
				}
				if(member != null && member.getState().equals(TBMemberState.HalfFinish)){
					tbRsp.setRstType(TBResultType.DATA_ERROR);
					tbRsp.setTipMsg("请您先完成当前的副本");
					return;
				}
			}
		}
		//获取的是玩家想要加入的队伍
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
			utbData.setSynTeam(true);
			joinTeam(player, teamItem);
			tbRsp.setRstType(TBResultType.SUCCESS);
		} catch (JoinTeamException e) {
			utbData.setSynTeam(false);
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg(e.getMessage());
		}
	}

	/**
	 * 设置队伍是否可以自由加入
	 * @param player
	 * @param tbRsp
	 * @param teamID
	 */
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

	/**
	 * 踢出队员
	 * @param player
	 * @param tbRsp
	 * @param userID
	 * @param teamID
	 */
	public void kickoffMember(Player player, Builder tbRsp, String userID, String teamID) {
		TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(teamID);
		if(!StringUtils.equals(teamItem.getLeaderID(), player.getUserId())){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("权限不足");
			return;
		}
		TeamMember kickMember = teamItem.findMember(userID);
		if(kickMember == null){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("该玩家不在队伍中");
			return;
		}
		if(kickMember.getState().equals(TBMemberState.Fight)){
			if(System.currentTimeMillis() - kickMember.getFightStartTime() < TeamBattleConst.MAX_FIGHT_TIME){
				tbRsp.setRstType(TBResultType.DATA_ERROR);
				tbRsp.setTipMsg("该玩家已经进入战斗");
				return;
			}
		}else if(!kickMember.getState().equals(TBMemberState.Ready)){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("不能踢出非空闲状态的玩家");
			return;
		}
		UserTeamBattleDataMgr.getInstance().leaveTeam(userID);
		UserTeamBattleDataMgr.getInstance().synData(userID);
		tbRsp.setRstType(TBResultType.SUCCESS);
	}
	
	/**
	 * 主动离开队伍
	 * @param player
	 * @param tbRsp
	 */
	public void leaveTeam(Player player, Builder tbRsp) {
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(StringUtils.isNotBlank(utbData.getTeamID())){
			TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(utbData.getTeamID());
			if(teamItem != null){
				TeamMember member = teamItem.findMember(player.getUserId());
				if(member != null && member.getState().equals(TBMemberState.HalfFinish)){
					tbRsp.setRstType(TBResultType.DATA_ERROR);
					tbRsp.setTipMsg("请您先完成当前的副本");
					return;
				}
			}
		}
		UserTeamBattleDataMgr.getInstance().leaveTeam(player.getUserId());
		UserTeamBattleDataMgr.getInstance().synData(player);
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	public void invitePlayer(Player player, Builder tbRsp, int inviteType, List<String> inviteUsers, String inviteContent) {
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(null == utbData || StringUtils.isBlank(utbData.getTeamID())){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("组队信息不存在");
			return;
		}
		TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(utbData.getTeamID());
		if(null == teamItem || !StringUtils.equals(teamItem.getLeaderID(), player.getUserId())){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("权限不足，队长才能邀请");
			return;
		}
		if(teamItem.isFull()){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("成员已满");
			return;
		}
		ServerCommonData scData = ServerCommonDataHolder.getInstance().get();
		if(null == scData){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("组队怪物信息有误");
			return;
		}
		String enimyID = scData.getTeamBattleEnimyMap().get(teamItem.getHardID());
		MonsterCombinationCfg cfg = MonsterCombinationDAO.getInstance().getCfgById(enimyID + "_1");
		if(null == cfg){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("组队怪物信息有误");
			return;
		}
		String displayMsg = String.format("快来加入挑战%s的队伍，一起来打败他们吧！", cfg.getName()) + "\n" + inviteContent;
		switch (inviteType) {
		case 1:
			//世界邀请
			ChatBM.getInstance().sendInteractiveMsgToWorld(player, ChatInteractiveType.TEAM, displayMsg, teamItem.getTeamID(), "");
			break;
		case 2:
			//公会邀请
			String groupId = GroupHelper.getUserGroupId(player.getUserId());
			if(!StringUtils.isBlank(groupId)){
				Group gp = GroupBM.get(groupId);
				if(null != gp){
					List<? extends GroupMemberDataIF> members = gp.getGroupMemberMgr().getMemberSortList(null);
					List<String> memIDs = new ArrayList<String>();
					for(GroupMemberDataIF mem : members){
						if(!StringUtils.equals(mem.getUserId(), player.getUserId())) memIDs.add(mem.getUserId());
					}
					if(!memIDs.isEmpty()){
						ChatBM.getInstance().sendInteractiveMsg(player, ChatInteractiveType.TEAM, displayMsg, teamItem.getTeamID(), "", memIDs);
					}
				}
			}
			break;
		case 3:
			//好友邀请
			ChatBM.getInstance().sendInteractiveMsg(player, ChatInteractiveType.TEAM, displayMsg, teamItem.getTeamID(), "", inviteUsers);
			break;
		default:
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("邀请的类型有误");
			return;
		}
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	/**
	 * 开始战斗
	 * @param player
	 * @param tbRsp
	 * @param loopID
	 * @param battleTime
	 */
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
		if(utbData.getFinishedLoops().contains(battleTime)){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("不能重复攻击已通过的关卡");
			return;
		}
		if(battleTime - 1 > 0 && !utbData.getFinishedLoops().contains(battleTime - 1)){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("关卡数据错误");
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
			teamMember.setFightStartTime(System.currentTimeMillis());
			TBTeamItemHolder.getInstance().updateTeam(teamItem);
		}
		for(StaticMemberTeamInfo teamInfoSimple : teamItem.getTeamMembers()){
			if(StringUtils.equals(teamInfoSimple.getUserID(), player.getUserId())) continue;
			ArmyInfo army = ArmyInfoHelper.getArmyInfo(teamInfoSimple.getUserStaticTeam(), false);
			tbRsp.addArmyInfo(ClientDataSynMgr.toClientData(army));
		}
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	/**
	 * 通知战斗结果
	 * @param player
	 * @param tbRsp
	 * @param loopID
	 * @param battleTime
	 * @param isSuccess
	 */
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
		if(battleTime - 1 > 0 && !utbData.getFinishedLoops().contains(battleTime - 1)){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("关卡数据错误");
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
			MonsterCombinationCfg cfg = cfgMap.get(battleTime);
			if(battleTime >= cfgMap.size()){
				teamMember.setState(TBMemberState.Finish);
				teamItem.changeLeaderAfterFinish(player.getUserId());
				utbData.getFinishedHards().add(teamItem.getHardID());
				if(cfg.getMail() != 0){
					for(TeamMember mem : teamItem.getMembers()){
						if(mem.getState().equals(TBMemberState.Finish) && !StringUtils.equals(mem.getUserID(), player.getUserId())){
							EmailUtils.sendEmail(player.getUserId(), String.valueOf(cfg.getMail()));
							EmailUtils.sendEmail(mem.getUserID(), String.valueOf(cfg.getMail()));
						}
					}
				}
			}else{
				teamMember.setState(TBMemberState.HalfFinish);
			}
			if(cfg.getReward() != null && cfg.getReward().size() > 0){
				ItemBagMgr bagMgr = player.getItemBagMgr();
				for (ItemInfo itm : cfg.getReward()) {
					GameLog.info(LogModule.TeamBattle.getName(), player.getUserId(), String.format("informFightResult, 准备添加物品[%s]数量[%s]", itm.getItemID(), itm.getItemNum()), null);
					if (!bagMgr.addItem(itm.getItemID(), itm.getItemNum()))
						GameLog.error(LogModule.TeamBattle, player.getUserId(), String.format("informFightResult, 添加物品[%s]的时候不成功，有[%s]未添加", itm.getItemID(), itm.getItemNum()), null);
				}
			}
			teamMember.setLastFinishBattle(battleTime);
			utbData.setScore(utbData.getScore() + cfg.getScoreGain());
			utbData.getFinishedLoops().add(battleTime);
			if(!TBTeamItemMgr.getInstance().removeTeam(teamItem)){
				TBTeamItemMgr.getInstance().synData(teamItem.getTeamID());
			}
			UserTeamBattleDataHolder.getInstance().synData(player);
		}else if(teamMember.getState().equals(TBMemberState.Fight)){
			teamMember.setState(TBMemberState.Ready);
		}
		teamMember.setFightStartTime(0);
		TBTeamItemHolder.getInstance().updateTeam(teamItem);
		tbRsp.setRstType(TBResultType.SUCCESS);
	}

	/**
	 * 积分交换物品
	 * @param player
	 * @param tbRsp
	 * @param rewardID
	 * @param count
	 */
	public void scoreExchage(Player player, Builder tbRsp, String rewardID, int count) {
		if(StringUtils.isBlank(rewardID) || count <= 0){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("购买物品的数据格式有误");
			return;
		}
		TeamStoreCfg storeCfg = TeamStoreCfgDAO.getInstance().getCfgById(rewardID);
		if(storeCfg == null){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("该物品目前不在售");
			return;
		}
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(utbData == null){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("组队副本数据异常");
			return;
		}
		int needScore = storeCfg.getScore() * count;
		if(needScore > utbData.getScore()){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("积分不够");
			return;
		}
		ItemBagMgr bagMgr = player.getItemBagMgr();
		if(bagMgr.addItem(storeCfg.getGoodsId(), storeCfg.getGoodsNumber() * count)){
			utbData.setScore(utbData.getScore() - needScore);
			UserTeamBattleDataHolder.getInstance().update(player, utbData);
			UserTeamBattleDataHolder.getInstance().synData(player);
			tbRsp.setRstType(TBResultType.SUCCESS);
		}else{
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("购买失败");
			return;
		}
	}
	
	/**
	 * 几种加入队伍方式的通用方法
	 * @param player
	 * @param canJionTeam
	 * @throws JoinTeamException
	 */
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
		utbData.setMemPos("");
		
		TBTeamItemHolder.getInstance().updateTeam(canJionTeam);
		UserTeamBattleDataHolder.getInstance().update(player, utbData);
		UserTeamBattleDataHolder.getInstance().synData(player);
		TBTeamItemMgr.getInstance().synData(canJionTeam.getId());
	}

	/**
	 * 保存队伍成员的位置信息
	 * @param player
	 * @param tbRsp
	 * @param memPos
	 */
	public void saveMemPosition(Player player, Builder tbRsp, String memPos) {
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		if(utbData == null){
			tbRsp.setRstType(TBResultType.DATA_ERROR);
			tbRsp.setTipMsg("组队副本数据异常");
			return;
		}
		utbData.setMemPos(memPos);
		UserTeamBattleDataHolder.getInstance().update(player, utbData);
		tbRsp.setRstType(TBResultType.SUCCESS);
	}
}
