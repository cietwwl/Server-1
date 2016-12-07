package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.StringUtils;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.battle.GCompMatchBattleCmdHelper;
import com.playerdata.groupcompetition.holder.data.GCompFightingRecord;
import com.playerdata.groupcompetition.holder.data.GCompMatchData;
import com.playerdata.groupcompetition.holder.data.GCompMember;
import com.playerdata.groupcompetition.holder.data.GCompPersonFightingRecord;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.holder.data.GCompTeamMember;
import com.playerdata.groupcompetition.holder.data.IGCompMemberAgent;
import com.playerdata.groupcompetition.stageimpl.GCGroup;
import com.playerdata.groupcompetition.util.GCompBattleResult;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.groupcompetition.GCompBasicScoreCfgDAO;
import com.rwbase.dao.groupcompetition.GCompGroupScoreCfgDAO;
import com.rwbase.dao.groupcompetition.GCompPersonalScoreCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.GCompBasicScoreCfg;
import com.rwbase.dao.groupcompetition.pojo.GCompScoreCfg;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.GroupCompetitionBattleProto.GCBattleCommonRspMsg;
import com.rwproto.GroupCompetitionBattleProto.GCBattleReqType;
import com.rwproto.GroupCompetitionBattleProto.GCBattleResult;
import com.rwproto.GroupCompetitionBattleProto.GCPushMemberScoreRspMsg;
import com.rwproto.MsgDef.Command;

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
	private ConcurrentHashMap<Integer, GCompMatchData> matchDataMap = new ConcurrentHashMap<Integer, GCompMatchData>(16, 1.0f);
	/** 角色Id对应的匹配Id */
	private ConcurrentHashMap<String, Integer> userId2MatchId = new ConcurrentHashMap<String, Integer>(48, 1.0f);

	private GCompBasicScoreCfgDAO _basicScoreCfgDAO;
	private GCompPersonalScoreCfgDAO _personalScoreCfgDAO;

	/**
	 * 添加队伍匹配数据到列表
	 * 
	 * @param myTeam
	 * @param enemyTeam
	 */
	public void addTeamMatchData(GCompTeam myTeam, GCompTeam enemyTeam) {
		resetAllMemberData(myTeam, enemyTeam);

		GCompMatchData myMatchData = GCompMatchData.createTeamMatchData(myTeam, enemyTeam);
		GCompMatchData enemyMatchData = GCompMatchData.createTeamMatchData(enemyTeam, myTeam);

		int myMatchId = myMatchData.getMatchId();
		int enemyMatchId = enemyMatchData.getMatchId();

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
		resetAllMemberData(myTeam, enemyTeam);

		GCompMatchData myMatchData = GCompMatchData.createPersonalMatchData(myTeam, enemyTeam);
		GCompMatchData enemyMatchData = GCompMatchData.createPersonalMatchData(enemyTeam, myTeam);

		int myMatchId = myMatchData.getMatchId();
		int enemyMatchId = enemyMatchData.getMatchId();

		matchDataMap.put(myMatchId, myMatchData);
		matchDataMap.put(enemyMatchId, enemyMatchData);

		recordUserId2MatchInfo(myMatchData);
		recordUserId2MatchInfo(enemyMatchData);
	}

	/**
	 * 重置队伍成员的数据
	 * 
	 * @param team
	 */
	private void resetAllMemberData(GCompTeam teamA, GCompTeam teamB) {
		List<GCompTeamMember> teamAMembers = teamA.getMembers();
		List<GCompTeamMember> teamBMembers = teamB.getMembers();

		GCompTeamMember memberA;// A
		GCompTeamMember memberB;// B
		for (int i = 0, size = teamAMembers.size(); i < size; i++) {
			memberA = teamAMembers.get(i);
			memberB = teamBMembers.get(i);

			memberA.setResult(GCompBattleResult.NonStart);
			memberB.setResult(GCompBattleResult.NonStart);

			memberA.setEnemyName(memberB.getArmyInfo().getPlayerName());
			memberB.setEnemyName(memberA.getArmyInfo().getPlayerName());
		}
		teamA.setInBattle(true);
		teamB.setInBattle(true);
	}

	/**
	 * 记录并同步数据
	 * 
	 * @param team
	 * @param data
	 */
	private void recordUserId2MatchInfo(GCompMatchData data) {
		int matchId = data.getMatchId();

		List<GCompTeamMember> members = data.getMyTeam().getMembers();

		for (int i = 0, size = members.size(); i < size; i++) {
			GCompTeamMember member = members.get(i);
			if (member == null) {
				continue;
			}

			if (member.isRobot()) {
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
		Integer matchId = userId2MatchId.get(userId);
		if (matchId == null) {
			GCompUtil.log("updateBattleResult，matchId == null！成员：{}", userId);
			return;
		}

		GCompMatchData matchData = matchDataMap.get(matchId);
		if (matchData == null) {
			GCompUtil.log("updateBattleResult，matchData == null！成员：{}", userId);
			return;
		}

		boolean allBattleFinish = true;// 所有的战斗是否都完成了

		int myAddScore = 0;// 己方战斗之后增加的值
		int enemyAddScore = 0;// 敌方战斗之后增加的值

		GCompTeam myTeam = matchData.getMyTeam();
		List<GCompTeamMember> members = myTeam.getMembers();
		int size = members.size();

		List<String> synPlayerIdList = new ArrayList<String>(size);
		List<ByteString> synMsgList = new ArrayList<ByteString>(1);

		for (int i = 0; i < size; i++) {
			GCompTeamMember member = members.get(i);
			if (member == null) {
				continue;
			}

			String memberId = member.getUserId();
			if (!member.isRobot()) {// 不是机器人要同步结构
				synPlayerIdList.add(memberId);
			}

			if (memberId.equals(userId)) {
				member.setResult(result);

				myAddScore += result.myAdd;
				enemyAddScore += result.enemyAdd;

				synMsgList.add(GCompMatchBattleCmdHelper.buildPushBattleResultMsg(i, parseBattleResult2MsgEnum(result)));
				continue;
			}

			GCompBattleResult battleResult = member.getResult();
			if (battleResult == GCompBattleResult.NonStart || battleResult == GCompBattleResult.Fighting) {
				allBattleFinish = false;
				// GCompUtil.log("updateBattleResult，member.getResult()未完成！当前状态：{}，member：{}", battleResult, member.getArmyInfo().getPlayerName());
				continue;
			}

			myAddScore += battleResult.myAdd;
			enemyAddScore += battleResult.enemyAdd;
		}

		// 同步战斗结构到客户端
		sendMsg(synMsgList, synPlayerIdList);

		// 所有的战斗都完成了
		if (allBattleFinish) {
			// 战斗结果处理
			teamBattleResultHandler(myTeam, getTeamBattleResult(myAddScore, enemyAddScore));
			// 删除缓存的匹配数据
			GCompUtil.log("allBattleFinish, matchId:{}", matchId);
			removeMatchCache(matchId);
		}
	}

	/**
	 * 删除匹配的缓存数据
	 * 
	 * @param matchId
	 */
	private void removeMatchCache(Integer matchId) {
		GCompMatchData remove = matchDataMap.remove(matchId);
		GCompUtil.log("removeMatchCache，匹配Id：{}", matchId);
		if (remove == null) {
			return;
		}

		List<GCompTeamMember> members = remove.getMyTeam().getMembers();
		for (int i = 0, size = members.size(); i < size; i++) {
			GCompTeamMember member = members.get(i);
			if (member.isRobot()) {
				// 机器人不处理
				continue;
			}
			String userId = members.get(i).getUserId();
			userId2MatchId.remove(userId);

			GCompUtil.log("removeMatchCache，删除userId：{}>>>对应的匹配Id：{}", userId, matchId);
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
			return GCompBasicScoreCfg.BATTLE_RESULT_WIN;
		case Lose:
			return GCompBasicScoreCfg.BATTLE_RESULT_LOSE;
		default:
		case Draw:
			return GCompBasicScoreCfg.BATTLE_RESULT_DRAW;
		}
	}

	// 计算个人积分
	private Pair<Integer, Integer> calculatePersonalTeamScore(GCompTeamMember teamMember, int continueWins, GCBattleResult teamResult, IReadOnlyPair<Integer, Integer> teamScore) {
		GCompBasicScoreCfg basicScoreCfg = _basicScoreCfgDAO.getByBattleResult(getBattleResultIntValue(teamMember.getResult()));
		GCompScoreCfg continueWinScoreCfg = _personalScoreCfgDAO.getByContinueWins(continueWins);
		int personalScore = basicScoreCfg.getPersonalScore() + continueWinScoreCfg.getPersonalScore();
		int groupScore = basicScoreCfg.getGroupScore() + continueWinScoreCfg.getGroupScore();
		Pair<Integer, Integer> score = Pair.Create(personalScore, groupScore);
		score.setT1(score.getT1() + teamScore.getT1());
		score.setT2(score.getT2() + teamScore.getT2());
		return score;
	}

	// 获取队伍的额外奖励
	private IReadOnlyPair<Integer, Integer> getTeamAdditionalScore(GCompTeam myTeam, GCBattleResult teamResult) {
		if (myTeam.isPersonal()) {
			// 如果是个人队伍，没有额外的奖励
			return _EMPTY_SCORE;
		}
		int key;
		switch (teamResult) {
		case DRAW:
			key = GCompGroupScoreCfgDAO.KEY_TEAM_DRAW;
			break;
		case WIN:
			key = GCompGroupScoreCfgDAO.KEY_TEAM_WIN;
			break;
		default:
			key = GCompGroupScoreCfgDAO.KEY_TEAM_LOSE;
			break;
		}
		GCompScoreCfg cfg = GCompGroupScoreCfgDAO.getInstance().getByContinueWins(key);
		Pair<Integer, Integer> score = Pair.Create(cfg.getPersonalScore(), cfg.getGroupScore());
		return score;
	}

	// 判断队伍里面的成员是否全部都是机器人
	private boolean checkIfAllRobot(GCompTeam myTeam) {
		List<GCompTeamMember> allMembers = myTeam.getMembers();
		for (int i = 0, size = allMembers.size(); i < size; i++) {
			GCompTeamMember member = allMembers.get(i);
			if (!member.isRobot()) {
				return false;
			}
		}
		return true;
	}

	// 对GCompGroupMember的胜利次进行处理
	private void processWinTimes(GCBattleResult result, GCompMember gCompMember, GCompTeamMember member, IGCompMemberAgent agent) {
		agent = GCompMember.getAgent(member.isRobot());
		// 处理连胜
		switch (result) {
		case WIN: // 胜利增加连胜次数
			agent.incWins(gCompMember);
			break;
		case LOSE: // 失败重置连胜
			agent.resetContinueWins(gCompMember);
			break;
		default: // 其他情况对连胜不处理
			break;
		}
	}

	private void sendRspMsg(List<String> playerIdList, List<IReadOnlyPair<Integer, Integer>> scoreResult, GCBattleResult result) {
		// 组合消息
		// 结果响应消息
		GCPushMemberScoreRspMsg.Builder memberScoreRspMsg = GCPushMemberScoreRspMsg.newBuilder();
		for (int i = 0, size = scoreResult.size(); i < size; i++) {
			IReadOnlyPair<Integer, Integer> score = scoreResult.get(i);
			memberScoreRspMsg.addMemberScore(GCompMatchBattleCmdHelper.buildGCMemberScoreMsg(i, score.getT1(), score.getT2()));
		}
		memberScoreRspMsg.setResult(result);
		GCBattleCommonRspMsg.Builder rsp = GCBattleCommonRspMsg.newBuilder();
		rsp.setReqType(GCBattleReqType.PUSH_MEMBER_SCORE);
		rsp.setPushMemeberScoreRsp(memberScoreRspMsg);
		ByteString rs = rsp.setIsSuccess(true).build().toByteString();

		if (!playerIdList.isEmpty()) {
			PlayerMgr mgr = PlayerMgr.getInstance();
			for (int i = 0, pSize = playerIdList.size(); i < pSize; i++) {
				mgr.find(playerIdList.get(i)).SendMsg(Command.MSG_GROUP_COMPETITION_BATTLE, rs);
			}
		}
	}

	/**
	 * 处理战斗结果
	 * 
	 * @param result
	 */
	private void teamBattleResultHandler(GCompTeam myTeam, GCBattleResult result) {
		if (!myTeam.isInBattle()) {
			GCompUtil.log("队伍结算，重复结算，队伍id：{}", result, myTeam.getTeamId());
			return;
		}
		myTeam.setInBattle(false);
		GCompUtil.log("队伍结算，战斗结果：{}，队伍id：{}", result, myTeam.getTeamId());
		if (checkIfAllRobot(myTeam)) {
			// GCompUtil.log("队伍id：{}，全部成员都是机器人，不进行成员结算！", myTeam.getTeamId());
			return;
		}
		String groupId = GroupHelper.getInstance().getUserGroupId(myTeam.getLeaderId());
		IReadOnlyPair<Integer, Integer> teamScore = this.getTeamAdditionalScore(myTeam, result); // 队伍额外积分的计算

		GCGroup group = GCompEventsDataMgr.getInstance().getGCGroupOfCurrentEvents(groupId);
		int totalGroupScore = 0; // 加给帮派的总积分
		IGCompMemberAgent agent;
		GCompMember bestMember = null;
		List<GCompTeamMember> allTeamMembers = myTeam.getMembers(); // 队伍的所有成员
		int size = allTeamMembers.size();
		List<String> playerIdList = new ArrayList<String>(size);
		List<GCompPersonFightingRecord> personFightingRecords = new ArrayList<GCompPersonFightingRecord>(size);
		List<IReadOnlyPair<Integer, Integer>> memberScores = new ArrayList<IReadOnlyPair<Integer, Integer>>(size);
		boolean myTeamWin = result == GCBattleResult.WIN;
		for (int i = 0; i < size; i++) {
			GCompTeamMember teamMember = allTeamMembers.get(i);
			GCompMember groupMember = GCompMemberMgr.getInstance().getGCompMember(groupId, teamMember.getUserId());
			if (groupMember != null) {
				agent = GCompMember.getAgent(teamMember.isRobot());

				processWinTimes(result, groupMember, teamMember, agent);

				Pair<Integer, Integer> score = this.calculatePersonalTeamScore(teamMember, agent.getContinueWins(groupMember), result, teamScore);
				int tempGroupScore = score.getT2().intValue();
				int personScore = score.getT1().intValue();
				agent.addScore(groupMember, score.getT1());
				agent.addGroupScore(groupMember, tempGroupScore);
				totalGroupScore += tempGroupScore;

				agent.updateToClient(groupMember);

				if (myTeamWin) {
					// 胜利才广播
					agent.checkBroadcast(groupMember, group.getGroupName(), tempGroupScore);
				}

				// GCompUtil.log("处理战斗结果，memberId：{}，memberName：{}，当前连胜：{}，当前击杀：{}，当前积分：{}，本次积分：{}", groupMember.getUserId(), teamMember.getArmyInfo().getPlayerName(),
				// agent.getContinueWins(groupMember), groupMember.getTotalWinTimes(), groupMember.getScore(), score.getT1());

				if (!teamMember.isRobot()) {
					playerIdList.add(teamMember.getUserId());
					if (bestMember == null || bestMember.getScore() < groupMember.getScore()) {
						bestMember = groupMember;
					}
				}

				memberScores.add(score);

				GCompPersonFightingRecord personFightingRecord = new GCompPersonFightingRecord();
				personFightingRecord.setContinueWin(agent.getContinueWins(groupMember));
				personFightingRecord.setDefendName(teamMember.getEnemyName());
				personFightingRecord.setOffendName(groupMember.getUserName());
				personFightingRecord.setGroupScore(tempGroupScore);
				personFightingRecord.setPersonalScore(personScore);
				personFightingRecord.setBattleResult(teamMember.getResult());
				// System.out.println("进攻方 : " + groupMember.getUserName() + ", 防守方：" + teamMember.getEnemyName() + ", 进攻方是否胜利 : " + personFightingRecord.isOffendWin());
				personFightingRecords.add(personFightingRecord);
			}
		}

		if (group != null && totalGroupScore > 0) {
			int matchId = GCompEventsDataMgr.getInstance().getGroupMatchIdOfCurrent(groupId);
			group.updateScore(totalGroupScore);
			GameWorldFactory.getGameWorld().asynExecute(new GroupScoreUpdater(matchId, groupId, group.getGCompScore(), bestMember));
			// GCompUtil.log("战斗结果，帮派Id：{}，帮派名字：{}，本次积分：{}，当前积分：{}", group.getGroupId(), group.getGroupName(), totalGroupScore, group.getGCompScore());

			GameWorldFactory.getGameWorld().asynExecute(new FightingRecordUpdater(matchId, personFightingRecords));
		}

		this.sendRspMsg(playerIdList, memberScores, result);
		GCompTeamMgr.getInstance().afterTeamBattleFinished(myTeam);
	}

	/**
	 * 获取MatchData
	 * 
	 * @param userId
	 * @return
	 */
	public GCompMatchData getMatchData(String userId) {
		Integer matchId = userId2MatchId.get(userId);
		if (StringUtils.isEmpty(matchId)) {
			GCompUtil.log("userId2MatchId找不到对应的MatchId，角色Id是{}", userId);
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
		ClientDataSynMgr.synData(PlayerMgr.getInstance().find(userId), data, eSynType.GCompMatchEnemy, eSynOpType.UPDATE_SINGLE, data.getMatchId());
	}

	/**
	 * 断线重连的时候同步一下自己的数据到客户端
	 * 
	 * @param player
	 */
	public void synPlayerMatchData(Player player) {
		GCompMatchData matchData = getMatchData(player.getUserId());
		if (matchData == null) {
			return;
		}

		ClientDataSynMgr.synData(player, matchData, eSynType.GCompMatchEnemy, eSynOpType.UPDATE_SINGLE, matchData.getMatchId());
	}

	private static final int HP_CHANGE_RATE_MIN = 2;// 每一秒最小的血量变化，最慢可以50秒同步完成
	private static final int HP_CHANGE_RATE_MAX = 5;// 每1秒最大的血量变化，最快20秒同步完成
	private static final float RATE = 100.00f;// 血量变化的基数

	// private static final float HP_CHANGE_RATE = 0.05f;// 每1秒变化
	// private static final float HP_MAX_CHANGE_RATE = 0.1f;// 每秒中最大变化0.1
	private static final float MAX_FIGHTING_RATE = 1;// 最大的战力差比

	private static final int LOGOUT_TIME_MILLIS = 30000;// 10秒未开始战斗，直接判定失败
	private static final int MAX_TIMEOUT_MILLIS = 120000;// 共给100秒的时间去处理超时

	private static final Random random = new Random();

	/**
	 * 检查所有的匹配数据
	 */
	public void checkAllMatchBattleState() {
		if (matchDataMap.isEmpty()) {
			return;
		}

		long now = System.currentTimeMillis();

		List<Integer> removeMatchIdList = new ArrayList<Integer>();

		// 检查匹配数据
		for (Entry<Integer, GCompMatchData> e : matchDataMap.entrySet()) {
			GCompMatchData data = e.getValue();

			long finishMatchTime = data.getFinishMatchTime();

			GCompTeam myTeam = data.getMyTeam();

			List<GCompTeamMember> members = myTeam.getMembers();
			List<GCompTeamMember> enemyMembers = data.getEnemyTeam().getMembers();

			boolean allBattleFinish = true;// 所有的战斗是否都完成了

			int myAddScore = 0;// 己方战斗之后增加的值
			int enemyAddScore = 0;// 敌方战斗之后增加的值

			int size = members.size();

			List<String> needSynHpPlayerIdList = new ArrayList<String>(size);
			List<ByteString> hpRsp = new ArrayList<ByteString>(size);

			List<String> needBattleResultList = new ArrayList<String>(size);
			List<ByteString> battleResultRsp = new ArrayList<ByteString>(size);

			GCompTeamMember member;
			for (int i = 0; i < size; i++) {
				member = members.get(i);
				GCompBattleResult result = member.getResult();
				String userId = member.getUserId();
				// 有了战斗结果
				if (result != GCompBattleResult.NonStart && result != GCompBattleResult.Fighting) {
					myAddScore += result.myAdd;
					enemyAddScore += result.enemyAdd;
					if (!member.isRobot()) {// 不是机器人的情况
						needBattleResultList.add(userId);
					}
					continue;
				}

				if (!member.isRobot()) {// 不是机器人的情况
					needBattleResultList.add(userId);

					// 还没开始战斗，并且已经超出了战斗上限，直接判输
					if (result == GCompBattleResult.NonStart && (now - finishMatchTime >= LOGOUT_TIME_MILLIS)) {
						member.setResult(GCompBattleResult.Lose);
						myAddScore += GCompBattleResult.Lose.myAdd;
						enemyAddScore += GCompBattleResult.Lose.enemyAdd;

						System.err.println("超时了" + userId);

						battleResultRsp.add(GCompMatchBattleCmdHelper.buildPushBattleResultMsg(i, GCBattleResult.LOSE));
						continue;
					}

					// 战斗状态中，并且已经超出了规定的超时上限，直接判平
					if (result == GCompBattleResult.Fighting && (now - member.getStartBattleTime() >= MAX_TIMEOUT_MILLIS)) {
						member.setResult(GCompBattleResult.Draw);
						myAddScore += GCompBattleResult.Draw.myAdd;
						enemyAddScore += GCompBattleResult.Draw.enemyAdd;

						battleResultRsp.add(GCompMatchBattleCmdHelper.buildPushBattleResultMsg(i, GCBattleResult.DRAW));
						continue;
					}

					// 战斗状态中的人需要同步数据
					if (result == GCompBattleResult.Fighting) {
						needSynHpPlayerIdList.add(userId);
					}
				} else {// 机器人的情况
					// 机器人超过离线时间之后，就设置成正在战斗状态
					if (result == GCompBattleResult.NonStart && (now - finishMatchTime >= LOGOUT_TIME_MILLIS)) {
						member.setResult(GCompBattleResult.Fighting);
						member.setStartBattleTime(now);
						continue;
					}

					// 战斗状态
					if (result == GCompBattleResult.Fighting) {
						// 检查血量的变化
						long l = now - member.getStartBattleTime();// 当前血量变化的时间
						if (l <= 0) {
							continue;
						}

						// 己方战力
						int myFighting = member.getArmyInfo().getTeamFighting();
						// 敌方战力
						int enemyFighting = enemyMembers.get(i).getArmyInfo().getTeamFighting();

						long m = l / 1000;
						// 自己的战力小于对方
						float myHpPercent = 0;// 己方剩余
						float enemyHpPercent = 0;// 敌方剩余

						float myChangeRate = (random.nextInt(HP_CHANGE_RATE_MIN) + (HP_CHANGE_RATE_MAX - HP_CHANGE_RATE_MIN) + 1) / RATE;
						float enemyChangeRate = (random.nextInt(HP_CHANGE_RATE_MIN) + (HP_CHANGE_RATE_MAX - HP_CHANGE_RATE_MIN) + 1) / RATE;

						if (myFighting < enemyFighting) {
							int j = enemyFighting / myFighting;
							float changeRate = j >= MAX_FIGHTING_RATE ? myChangeRate * 2 : myChangeRate;
							myHpPercent = 1 - (j >= MAX_FIGHTING_RATE ? MAX_FIGHTING_RATE : j) * changeRate * m;

							enemyHpPercent = 1 - enemyChangeRate * m;
						} else {
							int j = myFighting / enemyFighting;
							myHpPercent = 1 - myChangeRate * m;

							float changeRate = j >= MAX_FIGHTING_RATE ? enemyChangeRate * 2 : enemyChangeRate;
							enemyHpPercent = 1 - (j >= MAX_FIGHTING_RATE ? MAX_FIGHTING_RATE : j) * changeRate * m;
						}

						GCBattleResult rspResult = GCBattleResult.NONE;
						GCompBattleResult tempResult = result;
						if (myHpPercent <= 0) {
							if (myHpPercent < enemyHpPercent) {// 先于敌方，己方输了
								tempResult = GCompBattleResult.Lose;
								rspResult = GCBattleResult.LOSE;
							} else {// 后于敌方，己方赢了
								tempResult = GCompBattleResult.Win;
								rspResult = GCBattleResult.WIN;
							}
						}

						if (enemyHpPercent <= 0) {
							if (enemyHpPercent < myHpPercent) {// 先于己方，己方赢了
								tempResult = GCompBattleResult.Win;
								rspResult = GCBattleResult.WIN;
							} else {// 后于己方，己方输了
								tempResult = GCompBattleResult.Lose;
								rspResult = GCBattleResult.LOSE;
							}
						}

						if (tempResult != result) {
							member.setResult(tempResult);
							myAddScore += tempResult.myAdd;
							enemyAddScore += tempResult.enemyAdd;
							battleResultRsp.add(GCompMatchBattleCmdHelper.buildPushBattleResultMsg(i, rspResult));
							continue;
						}

						// 构造一个同步血量的消息

						// System.err.println(myHpPercent + "," + enemyHpPercent + ">>>" + myChangeRate + "," + enemyChangeRate);
						hpRsp.add(GCompMatchBattleCmdHelper.buildPushHpInfoMsg(i, myHpPercent, enemyHpPercent));
					}
				}

				if (result == GCompBattleResult.NonStart || result == GCompBattleResult.Fighting) {
					allBattleFinish = false;
					// GCompUtil.log("checkAllMatchBattleState，member.getResult()未完成！当前状态：{}，member：{}，isRobot4Me：{}，enemy：{}，isRobot4Enemy：{}，matchId：{}", result,
					// member.getArmyInfo().getPlayerName(), member.isRobot(), member.getEnemyName(), enemyMembers.get(i).isRobot(), e.getKey());
				}
			}

			// GCompUtil.log("--------------------------------------checkAllMatchBattleState，打印结束的分割线---------------------------------------");

			// 要把需要推送到前台的消息发送出去
			sendMsg(hpRsp, needSynHpPlayerIdList);
			// 要同步战斗结果
			sendMsg(battleResultRsp, needBattleResultList);

			if (allBattleFinish) {
				// 战斗结果处理
				teamBattleResultHandler(myTeam, getTeamBattleResult(myAddScore, enemyAddScore));
				// 增加一个要删除的MatchId
				removeMatchIdList.add(e.getKey());
			}
		}

		// 删除匹配
		for (int i = 0, size = removeMatchIdList.size(); i < size; i++) {
			removeMatchCache(removeMatchIdList.get(i));
		}
	}

	/**
	 * 同步消息
	 * 
	 * @param rspList
	 * @param playerIdList
	 */
	private void sendMsg(List<ByteString> rspList, List<String> playerIdList) {
		// 要同步战斗结果
		// GCompUtil.log("推送消息给客户端，列表：{}", playerIdList);
		if (!rspList.isEmpty() && !playerIdList.isEmpty()) {
			PlayerMgr playerMgr = PlayerMgr.getInstance();
			for (int j = 0, pSize = playerIdList.size(); j < pSize; j++) {
				Player p = playerMgr.find(playerIdList.get(j));
				for (ByteString bs : rspList) {
					p.SendMsg(Command.MSG_GROUP_COMPETITION_BATTLE, bs);
				}
			}
		}
	}

	/**
	 * 转换类中用的枚举到协议枚举
	 * 
	 * @param battleResult
	 * @return
	 */
	private GCBattleResult parseBattleResult2MsgEnum(GCompBattleResult battleResult) {
		if (battleResult == GCompBattleResult.Draw) {
			return GCBattleResult.DRAW;
		}

		if (battleResult == GCompBattleResult.Lose) {
			return GCBattleResult.LOSE;
		}

		return GCBattleResult.WIN;
	}

	private static class GroupScoreUpdater implements Runnable {

		private final int matchId;
		private final String groupId;
		private final int currentScore;
		private final GCompMember bestMember;

		public GroupScoreUpdater(int matchId, String groupId, int currentScore, GCompMember bestMember) {
			this.matchId = matchId;
			this.groupId = groupId;
			this.currentScore = currentScore;
			this.bestMember = bestMember;
		}

		@Override
		public void run() {
			try {
				GCompDetailInfoMgr.getInstance().onScoreUpdate(matchId, groupId, currentScore, bestMember);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static class FightingRecordUpdater implements Runnable {

		private int matchId;
		private List<GCompPersonFightingRecord> _personRecords;

		FightingRecordUpdater(int pMatchId, List<GCompPersonFightingRecord> pRecords) {
			this.matchId = pMatchId;
			this._personRecords = pRecords;
		}

		@Override
		public void run() {
			try {
				GCompFightingRecord record = new GCompFightingRecord();
				record.setPersonalFightingRecords(_personRecords);
				record.setMatchId(matchId);
				record.setTime(System.currentTimeMillis());
				GCompFightingRecordMgr.getInstance().addFightingRecord(matchId, record);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 当阶段结束的时候，清除掉时效
	 */
	public void clearAllMatchData() {
		matchDataMap.clear();
		userId2MatchId.clear();
	}
}