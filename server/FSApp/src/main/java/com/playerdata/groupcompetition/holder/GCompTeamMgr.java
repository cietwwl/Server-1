package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.StringUtils;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyMagic;
import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.playerdata.groupcompetition.data.IGCUnit;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.holder.data.GCompTeam.GCompTeamType;
import com.playerdata.groupcompetition.holder.data.GCompTeamMember;
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

	private static GCompTeamMgr _instance = new GCompTeamMgr();

	private static AtomicInteger _idGenerator = new AtomicInteger(10000000);

	public static GCompTeamMgr getInstance() {
		return _instance;
	}

	private GCompTeamHolder _dataHolder = GCompTeamHolder.getInstance();
	private boolean canPersonalMatching = false; // 是否可以个人匹配
	private Map<String, Boolean> statusOfGroup = new HashMap<String, Boolean>();
	private boolean isEventsProcessing = false;

	protected GCompTeamMgr() {

	}

	// 发送队伍状态消息给所有队伍成员，不包括队长
	private void sendTeamStatusToAll(GCompTeam team, TeamStatusType status) {
		List<GCompTeamMember> members = team.getMembers();
		TeamStatusChange.Builder builder = TeamStatusChange.newBuilder();
		builder.setStatus(status);
		for (GCompTeamMember member : members) {
			if (member.isLeader()) {
				continue;
			} else {
				PlayerMgr.getInstance().find(member.getUserId()).SendMsg(Command.MSG_GROUP_COMPETITION_TEAM_STATUS_CHANGE, builder.build().toByteString());
			}
		}
	}

	// 发送队伍状态消息给特定的人
	private void sendTeamStatus(Player targetPlayer, TeamStatusType type) {
		targetPlayer.SendMsg(Command.MSG_GROUP_COMPETITION_TEAM_STATUS_CHANGE, TeamStatusChange.newBuilder().setStatus(type).build().toByteString());
	}

	// 发送组队邀请
	private void sendInvitation(Player invitor, Player targetPlayer, String teamId) {
		// 发送邀请
		TeamInvitation.Builder invitationBuilder = TeamInvitation.newBuilder();
		invitationBuilder.setTeamId(teamId);
		invitationBuilder.setTips(GCompTips.getTipsInvitation(invitor.getUserName()));
		targetPlayer.SendMsg(Command.MSG_GROUP_COMPETITION_TEAM_MEMBER_REQ, invitationBuilder.build().toByteString());
	}

	private IGCAgainst getMatchOfGroup(String groupId) {
		GCEventsType currentEventsType = GroupCompetitionMgr.getInstance().getCurrentEventsType();
		return GCompEventsDataMgr.getInstance().getGCAgainstOfGroup(groupId, currentEventsType);
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
		} else {
			result.setT2(CreateTeamMemberResultStatus.HERO_NOT_MATCH);
		}
		if (includeMagic && result.getT2() == CreateTeamMemberResultStatus.SUCCESS) {
			gcompTeamArmyInfo.armyMagic = new ArmyMagic(player.getMagic());
		}
		return result;
	}

	private boolean checkIfCanMakeTeam(Pair<Boolean, String> result) {
		// 检查是否处于队伍战状态
		GCompEventsStatus eventsStatus = GroupCompetitionMgr.getInstance().getCurrentEventsStatus();
		if (eventsStatus == GCompEventsStatus.TEAM_EVENTS || eventsStatus == GCompEventsStatus.PREPARE) {
			// 组队阶段和准备阶段都可以组队
			return true;
		} else {
			result.setT2(GCompTips.getTipsNotTeamEventsNow());
			return false;
		}
	}

	private boolean checkIfCanTeamMatching(Pair<Boolean, String> result) {
		// 检查是否处于队伍战状态
		GCompEventsStatus eventsStatus = GroupCompetitionMgr.getInstance().getCurrentEventsStatus();
		if (eventsStatus == GCompEventsStatus.TEAM_EVENTS) {
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
	private IReadOnlyPair<String, Integer> checkMatchAndGroup(Player player, Pair<Boolean, String> result, boolean checkMemberSize) {
		String groupId = GroupHelper.getInstance().getGroupId(player);
		if (groupId == null) {
			result.setT2(GCompTips.getTipsYouAreNotInGroup());
			return null;
		}

		IGCAgainst gcAgainst = this.getMatchOfGroup(groupId);
		if (gcAgainst == null) {
			result.setT2(GCompTips.getTipsYourGroupNotInMatch(GroupCompetitionMgr.getInstance().getCurrentEventsType().chineseName));
			return null;
		}

		if (gcAgainst.getGroupA().getGroupId().length() == 0 || gcAgainst.getGroupB().getGroupId().length() == 0) {
			result.setT2(GCompTips.getTipsYourGroupHaveNoEnemy());
			return null;
		}

		if (checkMemberSize) {
			if (statusOfGroup.get(groupId) != Boolean.TRUE) {
				result.setT2(GCompTips.getTipsYourGroupMemberNotEnough());
				return null;
			}
			String enemyGroupId = gcAgainst.getGroupA().getGroupId().equals(groupId) ? gcAgainst.getGroupB().getGroupId() : gcAgainst.getGroupA().getGroupId();
			if (statusOfGroup.get(enemyGroupId) != Boolean.TRUE) {
				result.setT2(GCompTips.getTipsEnemyGroupMemberNotEnough());
				return null;
			}
		}
		return Pair.CreateReadonly(groupId, gcAgainst.getId());
	}

	private Pair<CreateTeamMemberResultStatus, GCompTeamMember> createTeamMember(Player player, List<String> heroIds, boolean isLeader) {
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

	private void sendDimiss(List<GCompTeam> teams) {
		Player player;
		for (GCompTeam team : teams) {
			List<GCompTeamMember> members = team.getMembers();
			for (GCompTeamMember member : members) {
				if (!PlayerMgr.getInstance().isOnline(member.getUserId())) {
					continue;
				}
				player = PlayerMgr.getInstance().find(member.getUserId());
				_dataHolder.synRemove(player, team);
			}
		}
	}

	private GCompTeamMember createByGCUnit(IGCUnit gcUnit, boolean isLeader) {
		Pair<CreateTeamMemberResultStatus, GCompTeamMember> createResult = createTeamMember(PlayerMgr.getInstance().find(gcUnit.getUserId()), gcUnit.getHeroIds(), isLeader);
		if (createResult.getT1() != CreateTeamMemberResultStatus.SUCCESS) {
			return null;
		}
		GCompTeamMember teamMember = createResult.getT2();
		if (gcUnit.isRobot()) {
			teamMember.setRobot(true);
		}
		return teamMember;
	}

	private void userTeamStatusChange(String userId, boolean inTeam) {
		GCompOnlineMemberMgr.getInstance().changeUserTeamStatus(userId, inTeam);
	}

	public void onEventStatusChange(GCompEventsStatus currentStatus) {
		switch (currentStatus) {
		case TEAM_EVENTS:
			canPersonalMatching = false;
			isEventsProcessing = true;
			break;
		case REST:
			List<GCompTeam> teams = _dataHolder.clearAllTeam(); // 解散所有队伍
			sendDimiss(teams);
			break;
		case PERSONAL_EVENTS:
			canPersonalMatching = true;
			break;
		case FINISH:
			canPersonalMatching = false;
			isEventsProcessing = false;
			break;
		default:
			break;
		}
	}

	private void initStatus(IGCGroup gcGroup) {
		String groupId;
		if ((groupId = gcGroup.getGroupId()).length() > 0) {
			statusOfGroup.put(groupId, GCompMemberMgr.getInstance().getSizeOfGroupMember(groupId) >= GCompCommonConfig.getMinMemberCountOfGroup());
		}
	}

	public void onGroupMemberLeave(String groupId) {
		if (statusOfGroup.containsKey(groupId)) {
			boolean result = GCompMemberMgr.getInstance().getSizeOfGroupMember(groupId) >= GCompCommonConfig.getMinMemberCountOfGroup();
			if (!result) {
				statusOfGroup.put(groupId, result);
			}
			GCompUtil.log("{}，有成员离开帮派，离开后的匹配状态：{}", groupId, result);
		}
	}

	public void onEventsStart(GCEventsType eventsType, List<? extends IGCAgainst> againsts) {
		this._dataHolder.clearTeamData();
		this._dataHolder.createTeamData(againsts);
		this.statusOfGroup.clear();
		for (IGCAgainst against : againsts) {
			this.initStatus(against.getGroupA());
			this.initStatus(against.getGroupB());
		}
		GCompUtil.log("[{}]比赛开始，所有帮派的匹配状态:{}", eventsType.chineseName, statusOfGroup);
	}

	public void forcePlayerLeaveTeam(Player player) {
		if (!isEventsProcessing) {
			return;
		}
		String groupId = GroupHelper.getInstance().getGroupId(player);
		if (!StringUtils.isEmpty(groupId)) {
			int matchId = GCompEventsDataMgr.getInstance().getMatchIdOfGroup(GroupHelper.getInstance().getGroupId(player), GroupCompetitionMgr.getInstance().getCurrentEventsType());
			if (matchId > 0) {
				GCompTeam team = _dataHolder.getTeamOfUser(matchId, player.getUserId(), groupId);
				if (team != null) {
					GCompUtil.log("强制玩家离开队伍，teamId：{}，玩家id：{}", team.getTeamId(), player.getUserId());
					this.leaveTeam(player);
				}
			}
		}
	}

	// public void sendTeamData(int matchId, Player player) {
	// this._dataHolder.syn(matchId, player);
	// }

	/**
	 * 
	 * @param matchId
	 * @param teamId
	 * @return
	 */
	public boolean isAllOnline(int matchId, String teamId) {
		GCompTeam team = _dataHolder.getTeamByTeamId(matchId, teamId);
		if (team != null) {
			if (team.isPersonal()) {
				return PlayerMgr.getInstance().isOnline(team.getMembers().get(0).getUserId());
			} else {
				List<GCompTeamMember> members = team.getMembers();
				for (int i = 0, size = members.size(); i < size; i++) {
					if (!PlayerMgr.getInstance().isOnline(members.get(i).getUserId())) {
						return false;
					}
				}
				return true;
			}
		}
		return true;
	}

	public GCompTeam createRandomTeam(List<? extends IGCUnit> gcUnitList) {
		if (gcUnitList.isEmpty()) {
			throw new IllegalArgumentException("members.size() == 0");
		}
		GCompTeam team;
		boolean multiple = gcUnitList.size() > 1;
		GCompTeamMember leader = this.createByGCUnit(gcUnitList.get(0), true);
		if (multiple) {
			GCompTeamMember[] teamMembers = new GCompTeamMember[gcUnitList.size() - 1];
			GCompTeamMember tempMember;
			for (int i = 1, size = gcUnitList.size(); i < size; i++) {
				tempMember = this.createByGCUnit(gcUnitList.get(i), false);
				if (tempMember != null) {
					teamMembers[i - 1] = tempMember;
				} else {
					return null;
				}
			}
			team = GCompTeam.createNewTeam(UUID.randomUUID().toString(), GCompTeamType.MULTIPLE_PLAYERS, leader, teamMembers);
		} else {
			team = GCompTeam.createNewTeam(UUID.randomUUID().toString(), GCompTeamType.SINGLE_PLAYER, leader);
		}
		team.setRandomTeam(true);
		return team;
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

		if (!this.checkIfCanMakeTeam(result)) {
			return result;
		}

		if (!this.checkTeamHeroIds(player, heroIds, result)) {
			return result;
		}

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result, true);
		if (matchAndGroupInfo == null) {
			return result;
		}

		int matchId = matchAndGroupInfo.getT2();
		String groupId = matchAndGroupInfo.getT1();
		GCompTeam team = _dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if (team != null) {
			result.setT2(GCompTips.getTipsYouAreInTeam());
			return result;
		}

		this.createTeamInternal(player, heroIds, matchId, groupId, false, result);
		if (result.getT1()) {
			userTeamStatusChange(player.getUserId(), true);
		}

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
		if (heroIdList.size() != heroIds.size() || (heroIdList.size() == heroIds.size() && !heroIdList.containsAll(heroIds))) {
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

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result, false);
		if (matchAndGroupInfo == null) {
			return result;
		}

		GCompTeam team = this._dataHolder.getTeamOfUser(matchAndGroupInfo.getT2(), player.getUserId(), matchAndGroupInfo.getT1());
		if (team == null) {
			// 没有队伍的情况
			result.setT2(GCompTips.getTipsYouAreNotInTeam());
			return result;
		}

		GCompTeamMember teamMember = team.getTeamMember(player.getUserId());
		if (teamMember == null) {
			// 有可能被踢了
			result.setT2(GCompTips.getTipsYouAreNotInTeam());
			return result;
		}
		this.updateHerosInternal(player, teamMember, newHeroIds, result);

		_dataHolder.synToAllMembers(team);
		result.setT1(true);

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

		if (!this.checkIfCanMakeTeam(result)) {
			return result;
		}

		if (!checkTeamHeroIds(player, heroIds, result)) {
			return result;
		}

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result, false);
		if (matchAndGroupInfo == null) {
			return result;
		}

		int matchId = matchAndGroupInfo.getT2();
		GCompTeam team = this._dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if (team != null) {
			result.setT2(GCompTips.getTipsYouAreInTeam());
			return result;
		}

		team = this._dataHolder.getTeamByTeamId(matchId, teamId);
		if (team == null) {
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
					userTeamStatusChange(player.getUserId(), true);
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

		if (leader == target) {
			result.setT2(GCompTips.getTipsCannotInviteYourselft());
			return result;
		}

		if (!this.checkIfCanMakeTeam(result)) {
			// 检查是否可以组队
			return result;
		}

		if (target == null) {
			// 对方不存在
			result.setT2(GCompTips.getTipsTargetNotExists());
			return result;
		}

		if (!GCompOnlineMemberMgr.getInstance().isMemberOnline(target)) {
			// 对方不在线
			result.setT2(GCompTips.getTipsTargetNotOnline());
			return result;
		}

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(leader, result, true);
		if (matchAndGroupInfo == null) {
			// 没有帮派，或者帮派没有入围
			return result;
		}

		String targetGroupId = GroupHelper.getInstance().getGroupId(target);
		if (!matchAndGroupInfo.getT1().equals(targetGroupId)) {
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
		if (!myTeam.getTeamMember(leader.getUserId()).isLeader()) {
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

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result, false);
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

		// 检查是否队长
		GCompTeamMember teamMember = team.getTeamMember(player.getUserId());
		if (!teamMember.isLeader()) {
			result.setT2(GCompTips.getTipsYouAreNotLeader());
			return result;
		}

		// 不能踢除自己
		if (player.getUserId().equals(targetUserId)) {
			result.setT2(GCompTips.getTipsCannotKickYourself());
			return result;
		}

		teamMember = team.getTeamMember(targetUserId);
		if (teamMember != null) {
			result.setT1(true);
			team.removeTeamMember(teamMember);
			Player targetPlayer = PlayerMgr.getInstance().find(targetUserId);
			sendTeamStatus(targetPlayer, TeamStatusType.Kicked);
			_dataHolder.synRemove(targetPlayer, team);
			List<GCompTeamMember> members = team.getMembers();
			if (members.size() == 1) {
				_dataHolder.update(player, team);
			} else {
				List<Player> allPlayers = new ArrayList<Player>(2);
				allPlayers.add(player);
				for (GCompTeamMember m : members) {
					if (!m.getUserId().equals(player.getUserId())) {
						allPlayers.add(PlayerMgr.getInstance().find(m.getUserId()));
					}
				}
				_dataHolder.synToAllMembers(team);
			}
			userTeamStatusChange(targetUserId, false);
		} else {
			result.setT1(false);
			result.setT2(GCompTips.getTipsTargetNotInYourTeam());
		}

		return result;
	}

	public IReadOnlyPair<Boolean, String> leaveTeam(Player player) {
		Pair<Boolean, String> result = Pair.Create(false, null);

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result, false);
		if (matchAndGroupInfo == null) {
			return result;
		}

		// 检查是否有队伍
		int matchId = matchAndGroupInfo.getT2();
		GCompTeam team = this._dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if (team == null) {
			// result.setT2(GCompTips.getTipsYouAreNotInTeam());
			// return result;
			result.setT1(true); // 没有队伍直接返回成功
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
		} else {
			_dataHolder.synToAllMembers(team);
		}
		result.setT1(true);
		userTeamStatusChange(player.getUserId(), false);

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

		if (!checkIfCanTeamMatching(result)) {
			return;
		}

		GCompTeamMember member = team.getTeamMember(player.getUserId());
		member.setReady(true);

		_dataHolder.synToAllMembers(team);
		result.setT1(true);

		checkIfAllReady(team);
	}

	private void cancelReady(Player player, GCompTeam team, Pair<Boolean, String> result) {

		if (team.isMatching()) {
			result.setT2(GCompTips.getTipsTeamIsMatchingCannotCancelReady());
			return;
		}

		GCompTeamMember member = team.getTeamMember(player.getUserId());
		member.setReady(false);
		result.setT1(true);

		_dataHolder.synToAllMembers(team);
	}

	public IReadOnlyPair<Boolean, String> switchMemberStatus(Player player, boolean setReady) {
		Pair<Boolean, String> result = Pair.Create(false, null);

		// if(!this.checkIfCanMakeTeam(result)) {
		// return result;
		// }

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result, false);
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

		if (setReady) {
			setReady(player, team, result);
		} else {
			cancelReady(player, team, result);
		}

		return result;
	}

	public IReadOnlyPair<Boolean, String> startTeamMatching(Player player) {
		Pair<Boolean, String> result = Pair.Create(false, null);

		if (!this.checkIfCanTeamMatching(result)) {
			return result;
		}

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result, true);
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

		List<GCompTeamMember> memberList = team.getMembers();
		// 检查队伍是否满员
		if (memberList.size() < GCompCommonConfig.getMaxMemberCountOfTeam()) {
			result.setT2(GCompTips.getTipsTeamMemberCountIsNotMax());
			return result;
		}

		for (GCompTeamMember member : memberList) {
			if (member.isLeader()) {
				continue;
			}
			if (!member.isReady()) {
				result.setT2(GCompTips.getTipsSomeoneNotReady());
				return result;
			}
		}

		// 队伍正在匹配中
		if (team.isMatching()) {
			result.setT2(GCompTips.getTipsTeamIsMatching());
			return result;
		}

		// 队伍正在战斗中
		if (team.isInBattle()) {
			result.setT2(GCompTips.getTipsTeamIsInBattle());
			return result;
		}

		team.setMatching(true);
		GroupCompetitionMatchingCenter.getInstance().submitToMatchingCenter(matchId, matchAndGroupInfo.getT1(), team);
		result.setT1(true);
		sendTeamStatusToAll(team, TeamStatusType.StartMatch);
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

		// if(!this.checkIfCanMakeTeam(result)) {
		// return result;
		// }

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result, false);
		if (matchAndGroupInfo == null) {
			result.setT1(true); // 让他取消
			return result;
		}

		// 检查是否有队伍
		int matchId = matchAndGroupInfo.getT2();
		GCompTeam team = this._dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if (team == null) {
			result.setT1(true); // 让他取消
			result.setT2(GCompTips.getTipsYouAreNotInTeam());
			return result;
		}

		if (!team.isPersonal() && !team.getTeamMember(player.getUserId()).isLeader()) {
			// 不是队长
			result.setT2(GCompTips.getTipsYouAreNotLeader());
			return result;
		}

		// 队伍没有在匹配中
		if (!team.isMatching()) {
			result.setT1(true); // 让他取消
			result.setT2(GCompTips.getTipsTeamIsNotMatching());
			return result;
		}

		// 队伍正在战斗中
		if (team.isInBattle()) {
			result.setT2(GCompTips.getTipsTeamIsInBattle());
			return result;
		}

		team.setMatching(false);
		if (GroupCompetitionMatchingCenter.getInstance().cancelMatching(matchId, matchAndGroupInfo.getT1(), team)) {
			result.setT1(true);
			this.sendTeamStatusToAll(team, TeamStatusType.CancelMatch);
		} else {
			result.setT1(false);
			if (team.isPersonal()) {
				result.setT2(GCompTips.getTipsYouAlreadyMatched());
			} else {
				result.setT2(GCompTips.getTipsTeamAlreadyMatched());
			}
		}
		return result;
	}

	public IReadOnlyPair<Boolean, String> randomMatching(Player player, List<String> heroIds) {
		Pair<Boolean, String> result = Pair.Create(false, null);

		if (!checkTeamHeroIds(player, heroIds, result)) {
			return result;
		}

		if (!this.checkIfCanMakeTeam(result)) {
			return result;
		}

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result, true);
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

		if (GroupCompetitionMatchingCenter.getInstance().isInRandomMatching(matchId, matchAndGroupInfo.getT1(), player.getUserId())) {
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
			result.setT1(true); // 让他取消
			return result;
		}

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result, false);
		if (matchAndGroupInfo == null) {
			result.setT1(true); // 让他取消
			return result;
		}

		int matchId = matchAndGroupInfo.getT2();
		if (!GroupCompetitionMatchingCenter.getInstance().isInRandomMatching(matchId, matchAndGroupInfo.getT1(), player.getUserId())) {
			result.setT2(GCompTips.getTipsYouAreNotInRandomMatching());
			result.setT1(true); // 让他取消
			return result;
		}

		if (GroupCompetitionMatchingCenter.getInstance().cancelRandomMatching(matchId, matchAndGroupInfo.getT1(), player)) {
			result.setT1(true);
		} else {
			result.setT1(false);
			result.setT2(GCompTips.getTipsYouAlreadyMatched());
		}
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

		if (!canPersonalMatching) {
			result.setT2(GCompTips.getTipsNotPersonalEventsNow());
			return result;
		}

		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(player, result, false);
		if (matchAndGroupInfo == null) {
			return result;
		}

		// 检查是否有队伍
		int matchId = matchAndGroupInfo.getT2();
		GCompTeam team = this._dataHolder.getTeamOfUser(matchId, player.getUserId(), matchAndGroupInfo.getT1());
		if (team == null) {
			// 创建队伍
			team = this.createTeamInternal(player, heroIds, matchId, matchAndGroupInfo.getT1(), true, result);
			if (!result.getT1()) {
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
			if (!result.getT1()) {
				return result;
			}
		}

		team.setMatching(true);
		GroupCompetitionMatchingCenter.getInstance().submitToMatchingCenter(matchId, matchAndGroupInfo.getT1(), team);
		result.setT1(true);

		return result;
	}

	/**
	 * 
	 * 战斗结束的通知，这里会把所有成员的准备状态重新设置一下
	 * 
	 * @param team
	 */
	public void afterTeamBattleFinished(GCompTeam team) {
		if (team.isRandomTeam() || team.isPersonal()) {
			return;
		}
		List<GCompTeamMember> allMembers = team.getMembers();
		for (int i = 0, size = allMembers.size(); i < size; i++) {
			allMembers.get(i).setReady(false);
		}
		this._dataHolder.synToAllMembers(team);
	}

	public static enum CreateTeamMemberResultStatus {

		SUCCESS, HERO_NOT_MATCH {
			@Override
			public String getTips() {
				return GCompTips.getTipsNotAllHeroExists();
			}
		};
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
