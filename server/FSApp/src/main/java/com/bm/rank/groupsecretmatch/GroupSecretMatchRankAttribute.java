package com.bm.rank.groupsecretmatch;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceCfg;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretResourceCfgDAO;
import com.rwproto.GroupSecretMatchProto.MatchSecretState;

/*
 * @author HC
 * @date 2016年5月26日 下午5:54:03
 * @Description 
 */
public class GroupSecretMatchRankAttribute {

	static class SecretState {
		final String atkUserId;// 攻击的人
		final long switchStateTime;// 转换状态时间
		final int state;// 秘境的状态

		public SecretState(String atkUserId, long switchStateTime, int state) {
			this.atkUserId = atkUserId;
			this.switchStateTime = switchStateTime;
			this.state = state;
		}
	}

	public GroupSecretMatchRankAttribute() {
	}

	public GroupSecretMatchRankAttribute(long createTime, int cfgId, String groupId) {
		this.createTime = createTime;
		this.cfgId = cfgId;
		this.groupId = groupId;
	}

	private long createTime;// 创建时间
	private int cfgId;// 秘境的Id
	private String groupId;// 帮派的Id
	private final AtomicReference<SecretState> stateReference = new AtomicReference<SecretState>();

	/**
	 * 是否是和平状态
	 * 
	 * @return
	 */
	@JsonIgnore
	public boolean isPeace() {
		return getState() == null;
	}

	/**
	 * 获取状态
	 * 
	 * @return
	 */
	@JsonIgnore
	private SecretState getState() {
		GroupSecretResourceCfg groupSecretResTmp = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(cfgId);// 秘境的资源模版表
		long now = System.currentTimeMillis();
		long robProtectMillis = TimeUnit.MINUTES.toMillis(groupSecretResTmp.getRobProtectTime());// 掠夺保护时间
		long battleProtectMillis = TimeUnit.MINUTES.toMillis(groupSecretResTmp.getProtectTime());// 战斗保护时间
		while (true) {
			SecretState state = stateReference.get();
			if (state == null) {
				return null;
			}

			long switchStateTime = state.switchStateTime;
			int gsState = state.state;
			if (gsState == MatchSecretState.IN_BATTLE_VALUE) {// 战斗保护中
				if (now - switchStateTime < battleProtectMillis) {
					return state;
				}
			} else if (gsState == MatchSecretState.IN_ROB_PROTECT_VALUE) {// 战斗掠夺保护
				if (now - switchStateTime < robProtectMillis) {
					return state;
				}
			} else if (gsState == MatchSecretState.IN_MAX_ROB_COUNT_VALUE) {// 已经被掠夺到了最大次数
				if (!DateUtils.isResetTime(5, 0, 0, switchStateTime)) {
					return state;
				}
			}

			// 中间可能有人改变他的状态，如果没有，那他就是成功设置者
			// 如果有，我需要重新获得最新状态
			if (stateReference.compareAndSet(state, null)) {
				return null;
			}
		}
	}

	/**
	 * 获取挑战权 1.有其他人已经获取 2.我进入保护
	 * 
	 * @param currentTime
	 * @return
	 */
	public boolean setFightingState(String atkUserId, long currentTime) {
		SecretState state = getState();
		if (state != null) {
			return state.atkUserId.equals(atkUserId);
		}

		return stateReference.compareAndSet(null, new SecretState(atkUserId, currentTime, MatchSecretState.IN_BATTLE_VALUE));
	}

	/**
	 * 设置掠夺保护状态
	 * 
	 * @param currentTime
	 * @return
	 */
	public boolean setRobProtectState(long currentTime) {
		SecretState state = getState();
		if (state == null) {// 和平状态是不能直接进入掠夺保护的
			return false;
		}

		if (state.state != MatchSecretState.IN_BATTLE_VALUE) {// 当前是战斗保护状态
			return false;
		}

		SecretState newState = new SecretState("", currentTime, MatchSecretState.IN_ROB_PROTECT_VALUE);
		return stateReference.compareAndSet(state, newState);
	}

	/**
	 * 设置掠夺到达上限的保护状态
	 * 
	 * @param currentTime
	 * @return
	 */
	public boolean setRobMaxProtectState(long currentTime) {
		SecretState state = getState();
		if (state == null) {// 和平状态是不能直接进入掠夺保护的
			return false;
		}

		if (state.state != MatchSecretState.IN_BATTLE_VALUE) {// 当前是战斗保护状态
			return false;
		}

		SecretState newState = new SecretState("", currentTime, MatchSecretState.IN_MAX_ROB_COUNT_VALUE);
		return stateReference.compareAndSet(state, newState);
	}

	/**
	 * 设置掠夺保护状态
	 * 
	 * @param currentTime
	 * @return
	 */
	public boolean setNonBattleState() {
		SecretState state = getState();
		if (state == null) {// 和平状态是不能直接进入掠夺保护的
			return false;
		}

		if (state.state != MatchSecretState.IN_BATTLE_VALUE) {// 当前是战斗保护状态
			return false;
		}

		return stateReference.compareAndSet(state, null);
	}

	public int getCfgId() {
		return cfgId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public String getGroupId() {
		return groupId;
	}
}