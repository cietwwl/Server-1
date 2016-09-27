package com.playerdata.groupcompetition.holder;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.StringUtils;

import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.holder.data.GCompMatchData;
import com.playerdata.groupcompetition.holder.data.GCompMember;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.holder.data.GCompTeamMember;
import com.playerdata.groupcompetition.stageimpl.GCGroup;
import com.playerdata.groupcompetition.util.GCompBattleResult;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.groupcompetition.GCompBasicScoreCfgDAO;
import com.rwbase.dao.groupcompetition.GCompGroupScoreCfgDAO;
import com.rwbase.dao.groupcompetition.GCompPersonalScoreCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.GCompBasicScoreCfg;
import com.rwbase.dao.groupcompetition.pojo.GCompScoreCfg;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.GroupCompetitionBattleProto.GCBattleResult;

/**
 * @Author HC
 * @date 2016年9月23日 下午2:54:50
 * @desc 匹配的数据
 **/

public class GCompMatchDataHolder {
	
	private static final IReadOnlyPair<Integer, Integer> _EMPTY_SCORE = Pair.CreateReadonly(0, 0);
	
	private static GCompMatchDataHolder holder = new GCompMatchDataHolder();

	public static GCompMatchDataHolder getHolder() {
		return holder;
	}

	GCompMatchDataHolder() {
		_basicScoreCfgDAO = GCompBasicScoreCfgDAO.getInstance();
		_personalScoreCfgDAO = GCompPersonalScoreCfgDAO.getInstance();
	}

	/** 缓存匹配的数据 <匹配的Id,匹配填充的数据> */
	private ConcurrentHashMap<String, GCompMatchData> matchDataMap = new ConcurrentHashMap<String, GCompMatchData>(16, 1.0f);
	/** 角色Id对应的匹配Id */
	private ConcurrentHashMap<String, String> userId2MatchId = new ConcurrentHashMap<String, String>(48, 1.0f);

	private GCompBasicScoreCfgDAO _basicScoreCfgDAO;
	private GCompPersonalScoreCfgDAO _personalScoreCfgDAO;
	
	/**
	 * 添加队伍匹配数据到列表
	 * 
	 * @param myTeam
	 * @param enemyTeam
	 */
	public void addTeamMatchData(GCompTeam myTeam, GCompTeam enemyTeam) {
		GCompMatchData myMatchData = GCompMatchData.createTeamMatchData(myTeam, enemyTeam);
		GCompMatchData enemyMatchData = GCompMatchData.createTeamMatchData(enemyTeam, myTeam);

		String myMatchId = myMatchData.getMatchId();
		String enemyMatchId = enemyMatchData.getMatchId();

		matchDataMap.put(myMatchId, myMatchData);
		matchDataMap.put(enemyMatchId, enemyMatchData);

		recordUserId2MatchInfo(myMatchData);
		recordUserId2MatchInfo(enemyMatchData);
	}

	/**
	 * 添加个人匹配数据到列表
	 * 
	 * @param myTeam
	 * @param enemyTeam
	 */
	public void addPersonalMatchData(GCompTeam myTeam, GCompTeam enemyTeam) {
		GCompMatchData myMatchData = GCompMatchData.createTeamMatchData(myTeam, enemyTeam);
		GCompMatchData enemyMatchData = GCompMatchData.createTeamMatchData(enemyTeam, myTeam);

		String myMatchId = myMatchData.getMatchId();
		String enemyMatchId = enemyMatchData.getMatchId();

		matchDataMap.put(myMatchId, myMatchData);
		matchDataMap.put(enemyMatchId, enemyMatchData);

		recordUserId2MatchInfo(myMatchData);
		recordUserId2MatchInfo(enemyMatchData);
	}

	/**
	 * 记录并同步数据
	 * 
	 * @param team
	 * @param data
	 */
	private void recordUserId2MatchInfo(GCompMatchData data) {
		String matchId = data.getMatchId();

		List<GCompTeamMember> members = data.getMyTeam().getMembers();

		for (int i = 0, size = members.size(); i < size; i++) {
			GCompTeamMember member = members.get(i);
			if (member == null) {
				continue;
			}

			String userId = member.getUserId();
			userId2MatchId.put(userId, matchId);

			synData(userId, data);
		}
	}

	/**
	 * 更新战斗结果
	 * 
	 * @param userId
	 * @param result
	 */
	public void updateBattleResult(String userId, GCompBattleResult result) {
		// 队伍战阶段连胜：队伍胜利，个人连胜增加；队伍失败，个人连胜终止
		// 队伍战阶段积分：队伍胜利，战败，平局，均可能对个人以及帮派有额外的加分；
		String matchId = userId2MatchId.get(userId);
		if (matchId == null) {
			return;
		}

		GCompMatchData matchData = matchDataMap.get(matchId);
		if (matchData == null) {
			return;
		}

		boolean allBattleFinish = true;// 所有的战斗是否都完成了

		int myAddScore = 0;// 己方战斗之后增加的值
		int enemyAddScore = 0;// 敌方战斗之后增加的值

		GCompTeam myTeam = matchData.getMyTeam();
		List<GCompTeamMember> members = myTeam.getMembers();
		for (int i = 0, size = members.size(); i < size; i++) {
			GCompTeamMember member = members.get(i);
			if (member == null) {
				continue;
			}

			if (member.getUserId().equals(userId)) {
				member.setResult(result);

				myAddScore += result.myAdd;
				enemyAddScore += result.enemyAdd;
				continue;
			}

			GCompBattleResult battleResult = member.getResult();
			if (battleResult == GCompBattleResult.NonStart || battleResult == GCompBattleResult.Fighting) {
				allBattleFinish = false;
				continue;
			}

			myAddScore += result.myAdd;
			enemyAddScore += result.enemyAdd;
		}

		// 所有的战斗都完成了
		if (allBattleFinish) {
			// 战斗结果处理
			teamBattleResultHandler(myTeam, getTeamBattleResult(myAddScore, enemyAddScore));

			matchDataMap.remove(matchId);

			for (int i = 0, size = members.size(); i < size; i++) {
				userId2MatchId.remove(members.get(i).getUserId());
			}
		}
	}

	/**
	 * 获取战斗结果
	 * 
	 * @param myAddScore
	 * @param enemyAddScore
	 * @return
	 */
	private GCBattleResult getTeamBattleResult(int myAddScore, int enemyAddScore) {
		if (myAddScore == enemyAddScore) {
			return GCBattleResult.DRAW;
		}

		if (myAddScore > enemyAddScore) {
			return GCBattleResult.WIN;
		}

		return GCBattleResult.LOSE;
	}
	
	private int getBattleResultIntValue(GCompBattleResult result) {
		switch (result) {
		case Win:
			return 1;
		case Lose:
			return 2;
		default:
		case Draw:
			return 3;
		}
	}
	
	private Pair<Integer, Integer> calculatePersonalTeamScore(GCompTeamMember teamMember, GCompMember gCompMember, GCBattleResult teamResult) {
		GCompBasicScoreCfg basicScoreCfg = _basicScoreCfgDAO.getByBattleResult(getBattleResultIntValue(teamMember.getResult()));
		GCompScoreCfg continueWinScoreCfg = _personalScoreCfgDAO.getByContinueWins(gCompMember.getContinueWins());
		int personalScore = basicScoreCfg.getPersonalScore() + continueWinScoreCfg.getPersonalScore();
		int groupScore = basicScoreCfg.getGroupScore() + continueWinScoreCfg.getGroupScore();
		return Pair.Create(personalScore, groupScore);
	}
	
	private Pair<Integer, Integer> getTeamAdditionalScore(GCBattleResult teamResult) {
		int key;
		switch (teamResult) {
		case DRAW:
			key = 0;
			break;
		case WIN:
			key = 1;
			break;
		default:
			key = -1;
			break;
		}
		GCompScoreCfg cfg = GCompGroupScoreCfgDAO.getInstance().getByContinueWins(key);
		Pair<Integer, Integer> score = Pair.Create(cfg.getPersonalScore(), cfg.getGroupScore());
		return score;
	}

	/**
	 * 处理战斗结果
	 * 
	 * @param result
	 */
	private void teamBattleResultHandler(GCompTeam myTeam, GCBattleResult result) {
		String groupId = GroupHelper.getUserGroupId(myTeam.getLeaderId());
		List<GCompTeamMember> allMembers = myTeam.getMembers();
		IReadOnlyPair<Integer, Integer> teamScore = null;
		if (!myTeam.isPersonal()) {
			teamScore = this.getTeamAdditionalScore(result);
		} else {
			teamScore = _EMPTY_SCORE;
		}
		int groupScore = 0;
		for (GCompTeamMember member : allMembers) {
			// 更新个人的数据
			member.getResult();
			GCompMember gCompMember = GCompMemberMgr.getInstance().getGCompMember(groupId, myTeam.getLeaderId());
			if (gCompMember != null) {
				// 处理连胜
				switch (result) {
				case WIN: // 胜利增加连胜次数
					gCompMember.incWinTimes();
					break;
				case LOSE: // 失败重置连胜
					gCompMember.resetContinueWins();
					break;
				default: // 其他情况对连胜不处理
					break;
				}

				Pair<Integer, Integer> score = this.calculatePersonalTeamScore(member, gCompMember, result);
				score.setT1(score.getT1() + teamScore.getT1());
				score.setT2(score.getT2() + teamScore.getT2());
				gCompMember.updateScore(score.getT1());
				groupScore += score.getT2();
			}
		}
		GCGroup group = GCompEventsDataMgr.getInstance().getGCGroupOfCurrentEvents(groupId);
		if (group != null) {
			group.updateScore(groupScore);
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

	/**
	 * 同步匹配的数据
	 * 
	 * @param userId
	 * @param data
	 */
	private void synData(String userId, GCompMatchData data) {
		ClientDataSynMgr.synData(PlayerMgr.getInstance().find(userId), data, eSynType.GCompMatchEnemy, eSynOpType.UPDATE_SINGLE);
	}
}