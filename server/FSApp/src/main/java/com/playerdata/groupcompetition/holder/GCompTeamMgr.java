package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.protobuf.ByteString;
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
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompCommonConfig;
import com.playerdata.groupcompetition.util.GCompTips;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.service.group.helper.GroupHelper;
import com.rwproto.GroupCompetitionProto.TeamInvitation;
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
		Pair<GCompTeamArmyInfo, CreateTeamMemberResultStatus> result = Pair.Create(null, null);
		List<Hero> heros = FSHeroMgr.getInstance().getHeros(player, heroIds);
		if (heros.size() == heroIds.size()) {
			GCompTeamArmyInfo gcompTeamArmyInfo = new GCompTeamArmyInfo();
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
		return result;
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
	
	private void sendKickNotice(Player targetPlayer) {
		targetPlayer.SendMsg(Command.MSG_GROUP_COMPETITION_NOTIFY_KICK, ByteString.EMPTY);
	}
	
	private void sendNotifyNewLeader(Player player) {
		player.SendMsg(Command.MSG_GROUP_COMPETITION_BECOME_LEADER, ByteString.EMPTY);
	}
	
	public void onEventsStart(GCEventsType eventsType, List<? extends IGCAgainst> againsts) {
		this._dataHolder.clearTeamData();
		this._dataHolder.createTeamData(againsts);
	}
	
	public void sendTeamData(int matchId, Player player) {
		this._dataHolder.syn(matchId, player);
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
		
		Pair<CreateTeamMemberResultStatus, GCompTeamMember> createResult = this.createTeamMember(player, heroIds, true);
		if (createResult.getT1() == CreateTeamMemberResultStatus.SUCCESS) {
			GCompTeam newTeam = GCompTeam.createNewTeam(String.valueOf(_idGenerator.incrementAndGet()), createResult.getT2());
			this._dataHolder.addTeam(player, GroupCompetitionMgr.getInstance().getCurrentEventsType(), matchId, groupId, newTeam);
			result.setT1(true);
			result.setT2(GCompTips.getTipsCreateTeamSuccess());
		} else {
			result.setT2(createResult.getT1().getTips());
		}
		
		return result;
	}
	
	/**
	 * 
	 * 更新自身的英雄列表
	 * 
	 * @param player
	 * @param newHeroIds
	 */
	public IReadOnlyPair<Boolean, String> updateHeros(Player player, List<String> newHeroIds) {
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
		Pair<GCompTeamArmyInfo, CreateTeamMemberResultStatus> createResult = createArmyInfoList(player, newHeroIds, false, false);
		if (createResult.getT2() == CreateTeamMemberResultStatus.SUCCESS) {
			teamMember.getTeamInfo().setHeroList(createResult.getT1().heroArmyInfos);
			result.setT1(true);
		} else {
			result.setT1(false);
			result.setT2(createResult.getT2().getTips());
		}
		
		
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
		
		if (target == null) {
			result.setT2(GCompTips.getTipsTargetNotExists());
			return result;
		}
		
		if(GCOnlineMemberMgr.getInstance().isMemberOnline(target)) {
			result.setT2(GCompTips.getTipsTargetNotOnline());
			return result;
		}
		
		IReadOnlyPair<String, Integer> matchAndGroupInfo = this.checkMatchAndGroup(leader, result);
		if (matchAndGroupInfo == null) {
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
			result.setT2(GCompTips.getTipsTargetIsInTeam());
			return result;
		}
		
		GCompTeam myTeam = _dataHolder.getTeamOfUser(matchId, leader.getUserId(), targetGroupId);
		if(myTeam.getTeamMember(leader.getUserId()).isLeader()) {
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
		
		teamMember = team.getTeamMember(targetUserId);
		if(teamMember != null) {
			result.setT1(true);
			team.removeTeamMember(teamMember);
			sendKickNotice(PlayerMgr.getInstance().find(targetUserId));
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
		if (members.isEmpty()) {
			// 解散队伍
			_dataHolder.removeTeam(matchId, matchAndGroupInfo.getT1(), team);
		} else if (member.isLeader()) {
			// 转移队长
			GCompTeamMember newLeader = members.get(0);
			newLeader.setLeader(true);
			sendNotifyNewLeader(PlayerMgr.getInstance().find(newLeader.getUserId()));
			_dataHolder.synToAllMembers(team);
		}
		
		return result;
	}
	
	private void cancelRandy(Player player, GCompTeam team, Pair<Boolean, String> result) {
		
		if(team.isMatching()) {
			result.setT2(GCompTips.getTipsTeamIsMatchingCannotCancelReady());
			return;
		}
		
		GCompTeamMember member = team.getTeamMember(player.getUserId());
		member.setReady(false);
		
		_dataHolder.synToAllMembers(team);
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
	
	public IReadOnlyPair<Boolean, String> switchMemberStatus(Player player, boolean setReady) {
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
		
		if(setReady) {
			setReady(player, team, result);
		} else {
			cancelRandy(player, team, result);
		}
		
		return result;
	}
	
	public IReadOnlyPair<Boolean, String> startMatching(Player player) {
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
		
		return result;
	}
	
	
	private static enum CreateTeamMemberResultStatus {
		
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
