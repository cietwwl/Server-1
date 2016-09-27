package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.StringUtils;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.battle.GCompMatchBattleCmdHelper;
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
				continue;
			}

			myAddScore += battleResult.myAdd;
			enemyAddScore += battleResult.enemyAdd;
		}

		// 所有的战斗都完成了
		if (allBattleFinish) {
			// 战斗结果处理
			teamBattleResultHandler(myTeam, getTeamBattleResult(myAddScore, enemyAddScore));
			// 删除缓存的匹配数据
			removeMatchCache(matchId);
		}

		// 同步战斗结构到客户端
		sendMsg(synMsgList, synPlayerIdList);
	}

	/**
	 * 删除匹配的缓存数据
	 * 
	 * @param matchId
	 */
	private void removeMatchCache(String matchId) {
		GCompMatchData remove = matchDataMap.remove(matchId);
		if (remove == null) {
			return;
		}

		List<GCompTeamMember> members = remove.getMyTeam().getMembers();
		for (int i = 0, size = members.size(); i < size; i++) {
			userId2MatchId.remove(members.get(i).getUserId());
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

	private static final float HP_CHANGE_RATE = 0.05f;// 每1秒变化
	private static final float HP_MAX_CHANGE_RATE = 0.1f;// 每秒中最大变化0.1
	private static final float MAX_FIGHTING_RATE = 1;// 最大的战力差比

	private static final int LOGOUT_TIME_MILLIS = 5000;// 5秒未开始战斗，直接判定失败
	private static final int MAX_TIMEOUT_MILLIS = 100000;// 共给100秒的时间去处理超时

	/**
	 * 检查所有的匹配数据
	 */
	public void checkAllMatchBattleState() {
		if (matchDataMap.isEmpty()) {
			return;
		}

		long now = System.currentTimeMillis();

		List<String> removeMatchIdList = new ArrayList<String>();

		// 检查匹配数据
		for (Entry<String, GCompMatchData> e : matchDataMap.entrySet()) {
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
				// 有了战斗结果
				if (result != GCompBattleResult.NonStart && result != GCompBattleResult.Fighting) {
					myAddScore += result.myAdd;
					enemyAddScore += result.enemyAdd;
					continue;
				}

				if (!member.isRobot()) {// 不是机器人的情况
					String userId = member.getUserId();

					needBattleResultList.add(userId);

					// 还没开始战斗，并且已经超出了战斗上限，直接判输
					if (result == GCompBattleResult.NonStart && (now - finishMatchTime >= LOGOUT_TIME_MILLIS)) {
						member.setResult(GCompBattleResult.Lose);
						myAddScore += GCompBattleResult.Lose.myAdd;
						enemyAddScore += GCompBattleResult.Lose.enemyAdd;

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
						// 己方战力
						int myFighting = member.getArmyInfo().getTeamFighting();
						// 敌方战力
						int enemyFighting = enemyMembers.get(i).getArmyInfo().getTeamFighting();

						long m = l / 1000;
						// 自己的战力小于对方
						float myHpPercent = 0;// 己方剩余
						float enemyHpPercent = 0;// 敌方剩余
						if (myFighting < enemyFighting) {
							int j = enemyFighting / myFighting;
							float changeRate = j >= MAX_FIGHTING_RATE ? HP_MAX_CHANGE_RATE : HP_CHANGE_RATE;

							myHpPercent = 1 - (j >= MAX_FIGHTING_RATE ? MAX_FIGHTING_RATE : j) * changeRate * m;
							enemyHpPercent = 1 - HP_CHANGE_RATE * m;
						} else {
							int j = myFighting / enemyFighting;
							float changeRate = j >= MAX_FIGHTING_RATE ? HP_MAX_CHANGE_RATE : HP_CHANGE_RATE;

							myHpPercent = 1 - HP_CHANGE_RATE * m;
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
						hpRsp.add(GCompMatchBattleCmdHelper.buildPushHpInfoMsg(i, myHpPercent, enemyHpPercent));
					}
				}

				if (result == GCompBattleResult.NonStart || result == GCompBattleResult.Fighting) {
					allBattleFinish = false;
				}
			}

			if (allBattleFinish) {
				// 战斗结果处理
				teamBattleResultHandler(myTeam, getTeamBattleResult(myAddScore, enemyAddScore));
				// 增加一个要删除的MatchId
				removeMatchIdList.add(e.getKey());
			}

			// 要把需要推送到前台的消息发送出去
			sendMsg(hpRsp, needSynHpPlayerIdList);
			// 要同步战斗结果
			sendMsg(battleResultRsp, needBattleResultList);
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
}