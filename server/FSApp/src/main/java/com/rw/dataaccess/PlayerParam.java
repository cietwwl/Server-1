package com.rw.dataaccess;

import com.rwbase.dao.role.pojo.RoleCfg;

public class PlayerParam {

	private final String accountId; // 玩家账号
	private final String userId; // 玩家唯一ID
	private final String userName; // 玩家名称
	private final String openAccount;
	private final int zoneId;
	private final int sex;
	private final long createTimeMillis;
	private final RoleCfg playerCfg;
	private final String headImage;
	private final int career;
	private final int starLevel;
	private final String clientInfoJson;

	public PlayerParam(String accountId, String openAccount, String userId, String userName, int zoneId, int sex, long createTimeMillis, RoleCfg playerCfg, String headImage, String clientInfoJson) {
		this.accountId = accountId;
		this.userId = userId;
		this.userName = userName;
		this.zoneId = zoneId;
		this.sex = sex;
		this.openAccount = openAccount;
		this.createTimeMillis = createTimeMillis;
		this.playerCfg = playerCfg;
		this.headImage = headImage;
		this.clientInfoJson = clientInfoJson;
		this.starLevel = 1;
		this.career = 0;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public int getZoneId() {
		return zoneId;
	}

	public int getSex() {
		return sex;
	}

	public long getCreateTimeMillis() {
		return createTimeMillis;
	}

	public RoleCfg getPlayerCfg() {
		return playerCfg;
	}

	public String getHeadImage() {
		return headImage;
	}

	public String getClientInfoJson() {
		return clientInfoJson;
	}

	public int getCareer() {
		return career;
	}

	public int getStarLevel() {
		return starLevel;
	}

	public String getOpenAccount() {
		return openAccount;
	}
}
