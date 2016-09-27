package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyMagic;
import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.holder.data.GCompTeamMember;
import com.playerdata.groupcompetition.holder.data.GCompTeam.GCompTeamType;
import com.playerdata.groupcompetition.matching.GroupCompetitionMatchingCenter;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompCommonConfig;
import com.playerdata.groupcompetition.util.GCompEventsStatus;
import com.playerdata.groupcompetition.util.GCompTips;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.GroupCompetitionProto.TeamInvitation;
import com.rwproto.GroupCompetitionProto.TeamStatusChange;
import com.rwproto.GroupCompetitionProto.TeamStatusType;
import com.rwproto.MsgDef.Command;

public class GCompTeamMgr {

	private static final GCompTeamMgr _instance = new GCompTeamMgr();
	
	private static AtomicInteger _idGenerator = new AtomicInteger(10000000);
	
	public static final GCompTeamMgr getInstance() {
		return _instance;
	}
	
	private GCompTeamHolder _dataHolder = GCompTeamHolder.getInstance();
	
	protected GCompTeamMgr() {
		
	}
	
	private int getMatchIdOfGroup(String groupId) {
		GCEventsType currentEventsType = GroupCompetitionMgr.getInstance().getCurrentEventsType();
		return GCompEventsDataMgr.getInstance().getMatchIdOfGroup(groupId, currentEventsType);
	}
	
	private Pair<GCompTeamArmyInfo, CreateTeamMemberResultStatus> createArmyInfoList(Player player, List<String> heroIds, boolean includeMain, boolean includeMagic) {
		Pair<GCompTeamArmyInfo, CreateTeamMemberResultStatus> result = Pair.Create(null, CreateTeamMemberResultStatus.SUCCESS);
		GCompTeamArmyInfo gcompTeamArmyInfo = new GCompTeamArmyInfo();
		if (heroIds.size() > 0) {
			List<Hero> heros = FSHeroMgr.getInstance().getHeros(player, heroIds);
			if (heros.size() == heroIds.size()) {
				gcompTeamArmyInfo.heroArmyInfos = new ArrayList<ArmyHeroSimple>(heros.size() - 1);
				for (Hero h : heros) {
					if (h.getId().equals(player.getUserId())) {
						if (includeMain) {
							gcompTeamArmyInfo.playerArmyInfo = ArmyHeroSimple.newInstance(h);
						}
					} else {
						gcompTeamArmyInfo.heroArmyInfos.add(ArmyHeroSimple.newInstance(h));
					}
					gcompTeamArmyInfo.fighting += h.getFighting();
				}
				result.setT2(CreateTeamMemberResultStatus.SUCCESS);
				result.setT1(gcompTeamArmyInfo);
			} else {
				result.setT2(CreateTeamMemberResultStatus.HERO_NOT_MATCH);
				GCompUtil.log("请求创建TeamMember，有部分英雄不存在！英雄id列表：{}, playerId：{}", heroIds, player.getUserId());
			}
		}
		if (includeMagic && result.getT2() == CreateTeamMemberResultStatus.SUCCESS) {
			gcompTeamArmyInfo.armyMagic = new ArmyMagic(player.getMagic());
		}
		return result;
	}
	
	private boolean checkIfCanMakeTeam(Pair<Boolean, String> result) {
		// 检查是否处于队伍战状态
		GCompEventsStatus eventsStatus = GroupCompetitionMgr.getInstance().getCurrentEventsStatus();
		if(eventsStatus == GCompEventsStatus.TEAM_EVENTS || eventsStatus == GCompEventsStatus.PREPARE) {
			// 组队阶段和准备阶段都可以组队
			return true;
		} else {
			result.setT2(GCompTips.getTipsNotTeamEventsNow());
			return false;
		}
	}
	
	// 对将要创建成为GCompTeamMember的英雄列表进行检查，包括英雄列表是否为空，是否包含主角
	private boolean checkTeamHeroIds(Player player, List<String> heroIds, Pair<Boolean, String> result) {
		if (heroIds.isEmpty()) {
			result.setT2(GCompTips.getTipsHeroCountInvalidate());
			return false;
		}

		if (!heroIds.contains(player.getUserId())) {
			result.setT2(GCompTips.getTipsArmyMustIncludeMainRole());
			return false;
		}
		return true;
	}
	
	// 检查角色是否在帮派里面，并且改帮派是否在本次对阵里面
	private IReadOnlyPair<String, Integer> checkMatchAndGroup(Player player, Pair<Boolean, String> result) {
		String groupId = GroupHelper.getGroupId(player);
		if (groupId == null) {
			result.setT2(GCompTips.getTipsYouAreNotInGroup());
			return null;
		}

		int matchId = this.getMatchIdOfGroup(groupId);
		if (matchId == 0) {
			result.setT2(GCompTips.getTipsYourGroupNotInMatch(GroupCompetitionMgr.getInstance().getCurrentEventsType().chineseName));
			return null;
		}

		return Pair.CreateReadonly(groupId, matchId);
	}
	
	private void sendInvitation(Player invitor, Player targetPlayer, String teamId) {
		// 发送邀请
		TeamInvitation.Builder invitationBuilder = TeamInvitation.newBuilder();
		invitationBuilder.setTeamId(teamId);
		invitationBuilder.setTips(GCompTips.getTipsInvitation(invitor.getUserName()));
		targetPlayer.SendMsg(Command.MSG_GROUP_COMPETITION_TEAM_MEMBER_REQ, invitationBuilder.build().toByteString());
	}
	
	private void sendTeamStatus(Player targetPlayer, TeamStatusType type) {
		targetPlayer.SendMsg(Command.MSG_GROUP_COMPETITION_TEAM_STATUS_CHANGE, TeamStatusChange.newBuilder().setStatus(type).build().toByteString());
	}
	
	private GCompTeam createTeamInternal(Player player, List<String> heroIds, int matchId, String groupId, boolean isPersonal, Pair<Boolean, String> result) {
		Pair<CreateTeamMemberResultStatus, GCompTeamMember> createResult = this.createTeamMember(player, heroIds, true);
		if (createResult.getT1() == CreateTeamMemberResultStatus.SUCCESS) {
			GCompTeam newTeam = GCompTeam.createNewTeam(String.valueOf(_idGenerator.incrementAndGet()), isPersonal ? GCompTeamType.SINGLE_PLAYER : GCompTeamType.MULTIPLE_PLAYERS, createResult.getT2());
			this._dataHolder.addTeam(player, GroupCompetitionMgr.getInstance().getCurrentEventsType(), matchId, groupId, newTeam);
			result.setT1(true);
			result.setT2(GCompTips.getTipsCreateTeamSuccess());
			return newTeam;
		} else {
			result.setT2(createResult.getT1().getTips());
			return null;
		}
	}
	
	public void onEventsStart(GCEventsType eventsType, List<? extends IGCAgainst> againsts) {
		this._dataHolder.clearTeamData();
		this._dataHolder.createTeamData(againsts);
	}
	
	public void sendTeamData(int matchId, Player player) {
		this._dataHolder.syn(matchId, player);
	}
	
	public Pair<CreateTeamMemberResultStatus, GCompTeamMember> createTeamMember(Player player, List<String> heroIds, boolean isLeader) {
		Pair<CreateTeamMemberResultStatus, GCompTeamMember> createResult = Pair.Create(CreateTeamMemberResultStatus.SUCCESS, null);
		Pair<GCompTeamArmyInfo, CreateTeamMemberResultStatus> createArmyInfoResult = this.createArmyInfoList(player, heroIds, true, true);
		if (createArmyInfoResult.getT2() == CreateTeamMemberResultStatus.SUCCESS) {
			GCompTeamArmyInfo teamArmyInfo = createArmyInfoResult.getT1();
			ArmyInfoSimple armyInfo = new ArmyInfoSimple();
			armyInfo.setPlayerName(player.getUserName());
			armyInfo.setHeroList(teamArmyInfo.heroArmyInfos);
			armyInfo.setPlayer(teamArmyInfo.playerArmyInfo);
			armyInfo.setPlayerHeadImage(player.getHeadImage());
			armyInfo.setTeamFighting(teamArmyInfo.fighting);
			armyInfo.setArmyMagic(teamArmyInfo.armyMagic);
			armyInfo.setGroupName(player.getUserGroupAttributeDataMgr().getUserGroupAttributeData().getGroupName());
			GCompTeamMember member = new GCompTeamMember(isLeader, armyInfo);
			createResult.setT2(member);
		} else {
			createResult.setT1(createArmyInfoResult.getT2());
		}
		return createResult;
	}
	
	/**
	 * 
	 * 创建队伍
	 * 
	 * @param player
	 * @param teamData
	 */
	public IReadOnlyPair<Boolean, String> createTeam(Player player, List<String> heroIds) {
		Pair<Boolean, String> result = Pair.Create(false, null);
		
		if(!this.checkIfCanMakeTeam(result)) {
			return result;
		}
		
		if(!this.checkTeamHeroIds(player, heroIds, result)) {
			return result;
		}
		
		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result);
		if(matchAndGroupInfo == null) {
			return result;
		}
		
		int matchId = matchAndGroupInfo.getT2();
		String groupId = matchAndGroupInfo.getT1();
		GCompTeam team = _dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if(team != null) {
			result.setT2(GCompTips.getTipsYouAreInTeam());
			return result;
		}
		
		this.createTeamInternal(player, heroIds, matchId, groupId, false, result);
		
		return result;
	}
	
	public GCompTeam getTeam(int matchId, String groupId, String teamId) {
		return _dataHolder.getTeamByTeamId(matchId, teamId);
	}
	
	private void updateHerosInternal(Player player, GCompTeamMember teamMember, List<String> heroIds, Pair<Boolean, String> result) {
		ArmyInfoSimple ais = teamMember.getArmyInfo();
		List<String> heroIdList = ais.getHeroIdList();
		heroIdList.add(player.getUserId());
		boolean updateMagic = player.getMagic().getModelId() != teamMember.getArmyInfo().getArmyMagic().getModelId();
		boolean needUpdate = updateMagic;
		if(heroIdList.size() != heroIds.size() || (heroIdList.size() == heroIds.size() && !heroIdList.containsAll(heroIds))) {
			needUpdate = true;
		}
		if (needUpdate) {
			Pair<GCompTeamArmyInfo, CreateTeamMemberResultStatus> createResult = createArmyInfoList(player, heroIds, false, updateMagic);
			if (createResult.getT2() == CreateTeamMemberResultStatus.SUCCESS) {
				teamMember.getArmyInfo().setHeroList(createResult.getT1().heroArmyInfos);
				result.setT1(true);
			} else {
				result.setT1(false);
				result.setT2(createResult.getT2().getTips());
			}
		} else {
			result.setT1(true);
		}
	}
	
	/**
	 * 
	 * 更新自身的英雄列表
	 * 
	 * @param player
	 * @param newHeroIds
	 */
	public IReadOnlyPair<Boolean, String> updateHeros(Player player, List<String> newHeroIds) {
		GCompUtil.log("更新英雄，playerId：{}，英雄列表：{}", player.getUserId(), newHeroIds);
		Pair<Boolean, String> result = Pair.Create(false, null);

		if (!checkTeamHeroIds(player, newHeroIds, result)) {
			return result;
		}

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result);
		if (matchAndGroupInfo == null) {
			return result;
		}
		
		GCompTeam team = this._dataHolder.getTeamOfUser(matchAndGroupInfo.getT2(), player.getUserId(), matchAndGroupInfo.getT1());
		GCompTeamMember teamMember = team.getTeamMember(player.getUserId());
		this.updateHerosInternal(player, teamMember, newHeroIds, result);
		
		
		_dataHolder.synToAllMembers(team);
		
		return result;
	}
	
	/**
	 * 
	 * 加入队伍
	 * 
	 * @param player
	 * @param teamId
	 */
	public IReadOnlyPair<Boolean, String> joinTeam(Player player, String teamId, List<String> heroIds) {
		Pair<Boolean, String> result = Pair.Create(false, null);
		
		if(!this.checkIfCanMakeTeam(result)) {
			return result;
		}

		if (!checkTeamHeroIds(player, heroIds, result)) {
			return result;
		}

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result);
		if (matchAndGroupInfo == null) {
			return result;
		}
		
		int matchId = matchAndGroupInfo.getT2();
		GCompTeam team = this._dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if(team != null) {
			result.setT2(GCompTips.getTipsYouAreInTeam());
			return result;
		}
		
		team = this._dataHolder.getTeamByTeamId(matchId, teamId);
		if(team == null) {
			result.setT2(GCompTips.getTipsTeamNotExists());
			return result;
		}
		
		Pair<CreateTeamMemberResultStatus, GCompTeamMember> createMemberResult = this.createTeamMember(player, heroIds, false);
		if (createMemberResult.getT1() == CreateTeamMemberResultStatus.SUCCESS) {
			synchronized (team) {
				if (team.getMembers().size() < GCompCommonConfig.getMaxMemberCountOfTeam()) {
					team.addTeamMember(createMemberResult.getT2());
					_dataHolder.synToAllMembers(team);
					result.setT1(true);
				} else {
					result.setT2(GCompTips.getTipsTeamMemberIsMax());
				}
			}
		} else {
			result.setT2(createMemberResult.getT1().getTips());
		}
		return result;
	}	
	
	public IReadOnlyPair<Boolean, String> inviteMember(Player leader, Player target) {
		Pair<Boolean, String> result = Pair.Create(false, null);
		
		if(leader == target) {
			result.setT2(GCompTips.getTipsCannotInviteYourselft());
			return result;
		}
		
		if(!this.checkIfCanMakeTeam(result)) {
			// 检查是否可以组队
			return result;
		}
		
		if (target == null) {
			// 对方不存在
			result.setT2(GCompTips.getTipsTargetNotExists());
			return result;
		}
		
		if(!GCOnlineMemberMgr.getInstance().isMemberOnline(target)) {
			// 对方不在线
			result.setT2(GCompTips.getTipsTargetNotOnline());
			return result;
		}
		
		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(leader, result);
		if (matchAndGroupInfo == null) {
			// 没有帮派，或者帮派没有入围
			return result;
		}
		
		String targetGroupId = GroupHelper.getGroupId(target);
		if(!matchAndGroupInfo.getT1().equals(targetGroupId)) {
			// 不在同一帮派
			result.setT2(GCompTips.getTipsTargetNotInYourGroup());
			return result;
		}
		
		int matchId = matchAndGroupInfo.getT2();
		if (_dataHolder.getTeamOfUser(matchId, target.getUserId(), targetGroupId) != null) {
			// 对方已经有队伍
			result.setT2(GCompTips.getTipsTargetIsInTeam());
			return result;
		}
		
		GCompTeam myTeam = _dataHolder.getTeamOfUser(matchId, leader.getUserId(), targetGroupId);
		if(!myTeam.getTeamMember(leader.getUserId()).isLeader()) {
			result.setT2(GCompTips.getTipsYouAreNotLeader());
		} else if (myTeam.getMembers().size() < GCompCommonConfig.getMaxMemberCountOfTeam()) {
			result.setT1(true);
			this.sendInvitation(leader, target, myTeam.getTeamId());
		} else {
			result.setT2(GCompTips.getTipsTeamMemberIsMax());
		}
		
		return result;
	}
	
	public IReadOnlyPair<Boolean, String> kickMember(Player player, String targetUserId) {
		Pair<Boolean, String> result = Pair.Create(false, null);
		
		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result);
		if (matchAndGroupInfo == null) {
			return result;
		}
		
		// 检查是否有队伍
		int matchId = matchAndGroupInfo.getT2();
		GCompTeam team = this._dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if(team == null) {
			result.setT2(GCompTips.getTipsYouAreNotInTeam());
			return result;
		}
		
		// 检查是否队长
		GCompTeamMember teamMember = team.getTeamMember(player.getUserId());
		if(!teamMember.isLeader()) {
			result.setT2(GCompTips.getTipsYouAreNotLeader());
			return result;
		}
		
		// 不能踢除自己
		if(player.getUserId().equals(targetUserId)) {
			result.setT2(GCompTips.getTipsCannotKickYourself());
			return result;
		}
		
		teamMember = team.getTeamMember(targetUserId);
		if(teamMember != null) {
			result.setT1(true);
			team.removeTeamMember(teamMember);
			Player targetPlayer = PlayerMgr.getInstance().find(targetUserId);
			sendTeamStatus(targetPlayer, TeamStatusType.Kicked);
			_dataHolder.synRemove(targetPlayer, team);
			List<GCompTeamMember> members = team.getMembers();
			if(members.size() == 1) {
				_dataHolder.update(player, team);
			} else {
				List<Player> allPlayers = new ArrayList<Player>(2);
				allPlayers.add(player);
				for (GCompTeamMember m : members) {
					if(!m.getUserId().equals(player.getUserId())) {
						allPlayers.add(PlayerMgr.getInstance().find(m.getUserId()));
					}
				}
				_dataHolder.synToAllMembers(team);
			}
		} else {
			result.setT1(false);
			result.setT2(GCompTips.getTipsTargetNotInYourTeam());
		}
		
		return result;
	}
	
	public IReadOnlyPair<Boolean, String> leaveTeam(Player player) {
		Pair<Boolean, String> result = Pair.Create(false, null);

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result);
		if (matchAndGroupInfo == null) {
			return result;
		}

		// 检查是否有队伍
		int matchId = matchAndGroupInfo.getT2();
		GCompTeam team = this._dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if (team == null) {
			result.setT2(GCompTips.getTipsYouAreNotInTeam());
			return result;
		}
		
		if (team.isMatching()) {
			// 队伍正在匹配中
			result.setT2(GCompTips.getTipsTeamIsMatchingCannotLeave());
			return result;
		}
		
		GCompTeamMember member = team.getTeamMember(player.getUserId());
		team.removeTeamMember(member);
		List<GCompTeamMember> members = team.getMembers();
		_dataHolder.synRemove(player, team);
		if (members.isEmpty()) {
			// 解散队伍
			_dataHolder.removeTeam(matchId, matchAndGroupInfo.getT1(), team);
		} else if (member.isLeader()) {
			// 转移队长
			GCompTeamMember newLeader = members.get(0);
			newLeader.setLeader(true);
			team.setLeaderId(newLeader.getUserId());
			sendTeamStatus(PlayerMgr.getInstance().find(newLeader.getUserId()), TeamStatusType.BecomeLeader);
			_dataHolder.synToAllMembers(team);
		}
		result.setT1(true);
		
		return result;
	}
	
	private void checkIfAllReady(GCompTeam team) {
		List<GCompTeamMember> allMembers = team.getMembers();
		boolean allReady = true;
		for (GCompTeamMember tempMember : allMembers) {
			if (!tempMember.isReady()) {
				allReady = false;
			}
		}
		if (allReady) {
			// 全部准备好，发送标识给客户端
		}
	}
	
	private void setReady(Player player, GCompTeam team, Pair<Boolean, String> result) {
		GCompTeamMember member = team.getTeamMember(player.getUserId());
		member.setReady(true);
		
		_dataHolder.synToAllMembers(team);
		
		checkIfAllReady(team);
	}
	
	private void cancelReady(Player player, GCompTeam team, Pair<Boolean, String> result) {
		
		if(team.isMatching()) {
			result.setT2(GCompTips.getTipsTeamIsMatchingCannotCancelReady());
			return;
		}
		
		GCompTeamMember member = team.getTeamMember(player.getUserId());
		member.setReady(false);
		
		_dataHolder.synToAllMembers(team);
	}
	
	public IReadOnlyPair<Boolean, String> switchMemberStatus(Player player, boolean setReady) {
		Pair<Boolean, String> result = Pair.Create(false, null);
		
		if(!this.checkIfCanMakeTeam(result)) {
			return result;
		}
		
		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result);
		if (matchAndGroupInfo == null) {
			return result;
		}

		// 检查是否有队伍
		int matchId = matchAndGroupInfo.getT2();
		GCompTeam team = this._dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if (team == null) {
			result.setT2(GCompTips.getTipsYouAreNotInTeam());
			return result;
		}
		
		if(setReady) {
			setReady(player, team, result);
		} else {
			cancelReady(player, team, result);
		}
		
		return result;
	}
	
	public IReadOnlyPair<Boolean, String> startTeamMatching(Player player) {
		Pair<Boolean, String> result = Pair.Create(false, null);
		
		if(!this.checkIfCanMakeTeam(result)) {
			return result;
		}

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result);
		if (matchAndGroupInfo == null) {
			return result;
		}

		// 检查是否有队伍
		int matchId = matchAndGroupInfo.getT2();
		GCompTeam team = this._dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if (team == null) {
			result.setT2(GCompTips.getTipsYouAreNotInTeam());
			return result;
		}
		
		// 检查队伍是否满员
		if(team.getMembers().size() < GCompCommonConfig.getMaxMemberCountOfTeam()) {
			result.setT2(GCompTips.getTipsTeamMemberCountIsNotMax());
			return result;
		}
		
		// 队伍正在匹配中
		if(team.isMatching()) {
			result.setT2(GCompTips.getTipsTeamIsMatching());
			return result;
		}
		
		// 队伍正在战斗中
		if(team.isInBattle()) {
			result.setT2(GCompTips.getTipsTeamIsInBattle());
			return result;
		}
		
		team.setMatching(true);
		GroupCompetitionMatchingCenter.getInstance().submitToMatchingCenter(matchId, matchAndGroupInfo.getT1(), team);
		result.setT1(true);
		return result;
	}
	
	/**
	 * 
	 * 取消匹配（个人和组队通用）
	 * 
	 * @param player
	 * @return
	 */
	public IReadOnlyPair<Boolean, String> cancelTeamMatching(Player player) {
		Pair<Boolean, String> result = Pair.Create(false, null);
		
//		if(!this.checkIfCanMakeTeam(result)) {
//			return result;
//		}

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result);
		if (matchAndGroupInfo == null) {
			return result;
		}

		// 检查是否有队伍
		int matchId = matchAndGroupInfo.getT2();
		GCompTeam team = this._dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if (team == null) {
			result.setT2(GCompTips.getTipsYouAreNotInTeam());
			return result;
		}
		
		// 队伍没有在匹配中
		if(!team.isMatching()) {
			result.setT2(GCompTips.getTipsTeamIsNotMatching());
			return result;
		}
		
		// 队伍正在战斗中
		if(team.isInBattle()) {
			result.setT2(GCompTips.getTipsTeamIsInBattle());
			return result;
		}
		
		team.setMatching(false);
		GroupCompetitionMatchingCenter.getInstance().cancelMatching(matchId, matchAndGroupInfo.getT1(), team);
		result.setT1(true);
		return result;
	}
	
	public IReadOnlyPair<Boolean, String> randomMatching(Player player, List<String> heroIds) {
		Pair<Boolean, String> result = Pair.Create(false, null);

		if (!this.checkIfCanMakeTeam(result)) {
			return result;
		}

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result);
		if (matchAndGroupInfo == null) {
			return result;
		}

		// 检查是否有队伍
		int matchId = matchAndGroupInfo.getT2();
		GCompTeam team = this._dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if (team != null) {
			result.setT2(GCompTips.getTipsYouAreInTeam());
			return result;
		}
		
		if(GroupCompetitionMatchingCenter.getInstance().isInRandomMatching(matchId, matchAndGroupInfo.getT1(), player.getUserId())) {
			result.setT2(GCompTips.getTipsYouAreInRandomMatching());
			return result;
		}
		
		GroupCompetitionMatchingCenter.getInstance().addToRandomMatching(matchId, matchAndGroupInfo.getT1(), player, heroIds);
		result.setT1(true);
		return result;
	}
	
	public IReadOnlyPair<Boolean, String> cancelRandomMatching(Player player) {
		Pair<Boolean, String> result = Pair.Create(false, null);

		if (!this.checkIfCanMakeTeam(result)) {
			return result;
		}

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result);
		if (matchAndGroupInfo == null) {
			return result;
		}
		
		int matchId = matchAndGroupInfo.getT2();
		if(!GroupCompetitionMatchingCenter.getInstance().isInRandomMatching(matchId, matchAndGroupInfo.getT1(), player.getUserId())) {
			result.setT2(GCompTips.getTipsYouAreNotInRandomMatching());
			return result;
		}
		
		GroupCompetitionMatchingCenter.getInstance().cancelRandomMatching(matchId, matchAndGroupInfo.getT1(), player);
		result.setT1(true);
		return result;
	}
	
	/**
	 * 
	 * 个人匹配
	 * 
	 * @param player
	 * @param heroIds
	 * @return
	 */
	public IReadOnlyPair<Boolean, String> personalMatching(Player player, List<String> heroIds) {
		Pair<Boolean, String> result = Pair.Create(false, null);

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result);
		if (matchAndGroupInfo == null) {
			return result;
		}

		// 检查是否有队伍
		int matchId = matchAndGroupInfo.getT2();
		GCompTeam team = this._dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if (team == null) {
			// 创建队伍
			team = this.createTeamInternal(player, heroIds, matchId, matchAndGroupInfo.getT1(), true, result);
			if(!result.getT1()) {
				return result;
			}
		} else if (team.isMatching()) {
			result.setT2(GCompTips.getTipsTeamIsMatching());
			return result;
		} else if (team.isInBattle()) {
			result.setT2(GCompTips.getTipsTeamIsInBattle());
			return result;
		} else {
			this.updateHerosInternal(player, team.getMembers().get(0), heroIds, result);
			if(!result.getT1()) {
				return result;
			}
		}
		
		team.setMatching(true);
		GroupCompetitionMatchingCenter.getInstance().submitToMatchingCenter(matchId, matchAndGroupInfo.getT1(), team);
		result.setT1(true);
		
		return result;
	}
	
	
	public static enum CreateTeamMemberResultStatus {
		
		SUCCESS,
		HERO_NOT_MATCH {
			@Override
			public String getTips() {
				return GCompTips.getTipsNotAllHeroExists();
			}
		}
		;
		public String getTips() {
			return "";
		}
	}
	
	private static class GCompTeamArmyInfo {
		
		ArmyHeroSimple playerArmyInfo;
		List<ArmyHeroSimple> heroArmyInfos;
		ArmyMagic armyMagic;
		int fighting;
	}
}
