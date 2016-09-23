package com.playerdata.groupcompetition.holder;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.StringUtils;

import com.playerdata.groupcompetition.holder.data.GCompMatchData;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.holder.data.GCompTeamMember;
import com.playerdata.groupcompetition.util.GCompBattleResult;

/**
 * @Author HC
 * @date 2016年9月23日 下午2:54:50
 * @desc 匹配的数据
 **/

public class GCompMatchDataHolder {
	private static GCompMatchDataHolder holder = new GCompMatchDataHolder();

	public static GCompMatchDataHolder getHolder() {
		return holder;
	}

	GCompMatchDataHolder() {
	}

	/** 缓存匹配的数据 <匹配的Id,匹配填充的数据> */
	private ConcurrentHashMap<String, GCompMatchData> matchDataMap = new ConcurrentHashMap<String, GCompMatchData>(16, 1.0f);
	/** 角色Id对应的匹配Id */
	private ConcurrentHashMap<String, String> userId2MatchId = new ConcurrentHashMap<String, String>(48, 1.0f);

	/**
	 * 添加队伍匹配数据到列表
	 * 
	 * @param myTeam
	 * @param enemyTeam
	 */
	public void addTeamMatchData(GCompTeam myTeam, GCompTeam enemyTeam) {
		GCompMatchData data = GCompMatchData.createTeamMatchData(myTeam, enemyTeam);

		String matchId = data.getMatchId();

		matchDataMap.put(matchId, data);

		List<GCompTeamMember> members = myTeam.getMembers();
		for (int i = 0, size = members.size(); i < size; i++) {
			GCompTeamMember member = members.get(i);
			if (member == null) {
				continue;
			}

			String userId = member.getUserId();
			userId2MatchId.put(userId, matchId);
		}
	}

	/**
	 * 添加个人匹配数据到列表
	 * 
	 * @param myTeam
	 * @param enemyTeam
	 */
	public void addPersonalMatchData(GCompTeam myTeam, GCompTeam enemyTeam) {
		GCompMatchData data = GCompMatchData.createPersonalMatchData(myTeam, enemyTeam);

		String matchId = data.getMatchId();

		matchDataMap.put(matchId, data);

		List<GCompTeamMember> members = myTeam.getMembers();
		for (int i = 0, size = members.size(); i < size; i++) {
			GCompTeamMember member = members.get(i);
			if (member == null) {
				continue;
			}

			String userId = member.getUserId();
			userId2MatchId.put(userId, matchId);
		}
	}

	/**
	 * 更新战斗结果
	 * 
	 * @param userId
	 * @param result
	 */
	public void updateBattleResult(String userId, GCompBattleResult result) {
		String matchId = userId2MatchId.get(userId);
		if (matchId == null) {
			return;
		}

		GCompMatchData matchData = matchDataMap.get(matchId);
		if (matchData == null) {
			return;
		}

		boolean allBattleFinish = true;// 所有的战斗是否都完成了

		GCompTeam myTeam = matchData.getMyTeam();
		List<GCompTeamMember> members = myTeam.getMembers();
		for (int i = 0, size = members.size(); i < size; i++) {
			GCompTeamMember member = members.get(i);
			if (member == null) {
				continue;
			}

			if (member.getUserId().equals(userId)) {
				member.setResult(result);
				continue;
			}

			GCompBattleResult battleResult = member.getResult();
			if (battleResult == GCompBattleResult.NonStart || battleResult == GCompBattleResult.Fighting) {
				allBattleFinish = false;
			}
		}

		// 所有的战斗都完成了
		if (allBattleFinish) {
			matchDataMap.remove(matchId);

			for (int i = 0, size = members.size(); i < size; i++) {
				userId2MatchId.remove(members.get(i).getUserId());
			}
		}
	}

	/**
	 * 获取MatchData
	 * 
	 * @param userId
	 * @return
	 */
	public GCompMatchData getMatchData(String userId) {
		String matchId = userId2MatchId.get(userId);
		if (StringUtils.isEmpty(matchId)) {
			return null;
		}

		return matchDataMap.get(matchId);
	}
}