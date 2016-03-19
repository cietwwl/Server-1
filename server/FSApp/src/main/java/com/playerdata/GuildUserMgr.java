package com.playerdata;

import com.rwbase.dao.gulid.faction.GuildUserInfo;
import com.rwbase.dao.gulid.faction.GuildUserInfoHolder;

public class GuildUserMgr {

	private GuildUserInfoHolder guildUserInfoHolder;

	private Player m_player;

	public void init(Player player) {
		if (guildUserInfoHolder == null) {
			m_player = player;
			guildUserInfoHolder = new GuildUserInfoHolder(player);
		}
	}

	public GuildUserInfo getGuildUserInfo() {
		return guildUserInfoHolder.get();
	}

	public int getApplyCount() {
		return guildUserInfoHolder.get().getApplyCount();
	}

	public int addGuildCoin(int count) {
		int value = count + getGuildCoin();
		if (value >= 0) {
			guildUserInfoHolder.get().setGuildCoin(count);
			guildUserInfoHolder.update(m_player);
			return 1;
		}
		return 0;
	}

	public int addGuildMaterial(int count) {
		int guildMaterialNum = 0;
		if (guildUserInfoHolder == null || guildUserInfoHolder.get() == null) {
			return 0;
		}
		int value = count + guildMaterialNum;
		if (value >= 0) {
			guildUserInfoHolder.get().setGuildMaterial(value);
			guildUserInfoHolder.update(m_player);
			return 1;
		}
		return 0;
	}

	public int getGuildCoin() {
		return guildUserInfoHolder.get().getGuildCoin();
	}

	/**
	 * 增加获取公会名字的默认处理
	 * @return
	 */
	public String getGuildName() {
		if (guildUserInfoHolder == null) {
			return "";
		}
		GuildUserInfo guildUserInfo = guildUserInfoHolder.get();
		if (guildUserInfo == null) {
			return "";
		}
		return guildUserInfo.getGuildName();
	}

	public void setGuildName(String guildName) {
		guildUserInfoHolder.get().setGuildName(guildName);
		guildUserInfoHolder.update(m_player);
	}

	public String getGuildId() {
		return guildUserInfoHolder.get().getGuildId();
	}

	public void setGuildId(String gulidUid) {
		guildUserInfoHolder.get().setGuildId(gulidUid);
		guildUserInfoHolder.update(m_player);
	}

	public boolean isJoinTimeOk() {
		final long timeSpan = 24 * 3600 * 1000;
		long exitTime = guildUserInfoHolder.get().getExitTime();
		long currentTime = System.currentTimeMillis();
		return exitTime + timeSpan < currentTime;
	}

	public void updateWhenJoin(String guildId, String guildName) {
		guildUserInfoHolder.reset();
		guildUserInfoHolder.get().setGuildId(guildId);
		guildUserInfoHolder.get().setGuildName(guildName);
		guildUserInfoHolder.update(m_player);
	}

	public void cleanWhenExit() {
		guildUserInfoHolder.reset();
		guildUserInfoHolder.get().setExitTime(System.currentTimeMillis());
		guildUserInfoHolder.update(m_player);
	}

	public void setExitTime() {
		long currentTime = System.currentTimeMillis();
		guildUserInfoHolder.get().setExitTime(currentTime);
		guildUserInfoHolder.update(m_player);
	}

}
