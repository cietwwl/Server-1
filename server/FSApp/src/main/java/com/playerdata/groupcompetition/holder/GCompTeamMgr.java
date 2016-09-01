package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.army.ArmyMagic;
import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.holder.data.GCompTeamMember;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompTips;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.service.group.helper.GroupHelper;

public class GCompTeamMgr {

	private static final GCompTeamMgr _instance = new GCompTeamMgr();
	
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
	
	private Pair<CreateTeamMemberResultStatus, GCompTeamMember> createTeamMember(Player player, List<String> heroIds, boolean isLeader) {
		Pair<CreateTeamMemberResultStatus, GCompTeamMember> createResult = Pair.Create(CreateTeamMemberResultStatus.SUCCESS, null);
		List<Hero> heros = FSHeroMgr.getInstance().getHeros(player, heroIds);
		if (heros.size() == heroIds.size()) {
			List<ArmyHeroSimple> armyHeros = new ArrayList<ArmyHeroSimple>(heros.size() - 1);
			ArmyHeroSimple playerArmyHero = null;
			ArmyHeroSimple temp;
			int fighting = 0;
			for (int i = 0, size = heros.size(); i < size; i++) {
				temp = ArmyHeroSimple.newInstance(heros.get(i));
				if (temp.getId().equals(player.getUserId())) {
					playerArmyHero = temp;
				} else {
					armyHeros.add(temp);
				}
				fighting += temp.getFighting();
			}
			ArmyMagic armyMagic = new ArmyMagic(player.getMagic());
			ArmyInfoSimple armyInfo = new ArmyInfoSimple();
			armyInfo.setPlayerName(player.getUserName());
			armyInfo.setHeroList(armyHeros);
			armyInfo.setPlayer(playerArmyHero);
			armyInfo.setPlayerHeadImage(player.getHeadImage());
			armyInfo.setTeamFighting(fighting);
			armyInfo.setArmyMagic(armyMagic);
			armyInfo.setGroupName(player.getUserGroupAttributeDataMgr().getUserGroupAttributeData().getGroupName());
			GCompTeamMember member = new GCompTeamMember(isLeader, armyInfo);
			createResult.setT2(member);
		} else {
			createResult.setT1(CreateTeamMemberResultStatus.HERO_NOT_MATCH);
			GCompUtil.log("请求创建TeamMember，有部分英雄不存在！英雄id列表：{}, playerId：{}", heroIds, player.getUserId());
		}
		return createResult;
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
		
		if (heroIds.isEmpty()) {
			result.setT2(GCompTips.getTipsHeroCountInvalidate());
			return result;
		}

		if (!heroIds.contains(player.getUserId())) {
			result.setT2(GCompTips.getTipsArmyMustIncludeMainRole());
			return result;
		}
		
		String groupId = GroupHelper.getGroupId(player);
		if (groupId == null) {
			result.setT2(GCompTips.getTipsYouAreNotInGroup());
			return result;
		}
		
		int matchId = this.getMatchIdOfGroup(groupId);
		if (matchId == 0) {
			result.setT2(GCompTips.getTipsYourGroupNotInMatch(GroupCompetitionMgr.getInstance().getCurrentEventsType().chineseName));
			return result;
		}
		
		GCompTeam team = _dataHolder.get(matchId, player.getUserId());
		if(team != null) {
			result.setT2(GCompTips.getTipsYouAreInTeam());
			return result;
		}
		
		Pair<CreateTeamMemberResultStatus, GCompTeamMember> createResult = this.createTeamMember(player, heroIds, true);
		if (createResult.getT1() == CreateTeamMemberResultStatus.SUCCESS) {
			GCompTeam newTeam = GCompTeam.createNewTeam(UUID.randomUUID().toString(), createResult.getT2());
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
	public void updateHeros(Player player, int matchId, List<String> newHeroIds) {
		
	}
	
	/**
	 * 
	 * 加入队伍
	 * 
	 * @param player
	 * @param teamId
	 */
	public void joinTeam(Player player, int matchId, int teamId) {
		
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
}
