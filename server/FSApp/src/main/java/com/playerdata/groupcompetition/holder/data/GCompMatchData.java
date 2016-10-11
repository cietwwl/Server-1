package com.playerdata.groupcompetition.holder.data;

import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.util.GCompMatchConst.GCompMatchState;
import com.playerdata.groupcompetition.util.GCompMatchConst.GCompMatchType;

/**
 * @Author HC
 * @date 2016年9月23日 上午11:49:31
 * @desc
 **/

@SynClass
public class GCompMatchData {
	private static AtomicInteger macthIdGenerate = new AtomicInteger();

	/**
	 * 新创建一个队伍匹配
	 * 
	 * @param myTeam
	 * @param enemyTeam
	 */
	public static GCompMatchData createTeamMatchData(GCompTeam myTeam, GCompTeam enemyTeam) {
		GCompMatchData matchData = new GCompMatchData();
		matchData.myTeam = myTeam;
		matchData.enemyTeam = enemyTeam;
		matchData.matchId = macthIdGenerate.incrementAndGet();
		matchData.matchType = GCompMatchType.TEAM_MATCH.type;
		matchData.finishMatchTime = System.currentTimeMillis();
		return matchData;
	}

	/**
	 * 新创建一个私人匹配
	 * 
	 * @param myTeam
	 * @param enemyTeam
	 */
	public static GCompMatchData createPersonalMatchData(GCompTeam myTeam, GCompTeam enemyTeam) {
		GCompMatchData matchData = new GCompMatchData();
		matchData.myTeam = myTeam;
		matchData.enemyTeam = enemyTeam;
		matchData.matchId = macthIdGenerate.incrementAndGet();
		matchData.matchType = GCompMatchType.TEAM_MATCH.type;
		matchData.finishMatchTime = System.currentTimeMillis();
		return matchData;
	}

	/** 使用UUID */
	@IgnoreSynField
	private int matchId;// 匹配的Id
	private GCompTeam myTeam;// 己方的队伍信息
	private GCompTeam enemyTeam;// 敌人的队伍信息
	/**
	 * <pre>
	 * 匹配类型
	 * 1：队伍匹配
	 * 2：个人匹配
	 * 
	 * {@link GCompMatchType}
	 * </pre>
	 */
	@IgnoreSynField
	private int matchType;// 匹配的类型<1：队伍匹配 2：个人匹配>

	/**
	 * <pre>
	 * 匹配状态 
	 * 1：未匹配 
	 * 2：匹配中 
	 * 3：可以战斗
	 * 
	 * {@link GCompMatchState}
	 * 
	 * <font color="ff0000">默认是未匹配状态</font>
	 * </pre>
	 */
	@IgnoreSynField
	private int matchState = GCompMatchState.START_BATTLE.state;
	@IgnoreSynField
	private long finishMatchTime;// 完成匹配的时间点

	GCompMatchData() {
	}

	/**
	 * 获取匹配的Id
	 * 
	 * @return
	 */
	public int getMatchId() {
		return matchId;
	}

	/**
	 * 获取己方的队伍阵容
	 * 
	 * @return
	 */
	public GCompTeam getMyTeam() {
		return myTeam;
	}

	/**
	 * 获取敌方的队伍阵容
	 * 
	 * @return
	 */
	public GCompTeam getEnemyTeam() {
		return enemyTeam;
	}

	/**
	 * 获取匹配的类型
	 * 
	 * @return
	 */
	public int getMatchType() {
		return matchType;
	}

	/**
	 * 获取匹配的状态
	 * 
	 * @return
	 */
	public int getMatchState() {
		return matchState;
	}

	/**
	 * 更新匹配的状态
	 * 
	 * @param state
	 */
	public void updateMatchState(GCompMatchState state) {
		this.matchState = state.state;
	}

	/**
	 * 更新匹配到的敌人的信息
	 * 
	 * @param enemyTeam
	 */
	public void updateMatchEnemyTeamData(GCompTeam enemyTeam) {
		this.enemyTeam = enemyTeam;
	}

	/**
	 * 获取匹配完成的时间
	 * 
	 * @return
	 */
	public long getFinishMatchTime() {
		return finishMatchTime;
	}
}