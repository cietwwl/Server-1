package com.rwbase.dao.groupsecret.syndata.base;

import com.rwbase.dao.groupsecret.syndata.SecretBaseInfoSynData;
import com.rwbase.dao.groupsecret.syndata.SecretTeamInfoSynData;

/*
 * @author HC
 * @date 2016年5月30日 下午5:39:05
 * @Description 
 */
public class GroupSecretDataSynData {
	private final SecretBaseInfoSynData base;// 基础
	private final SecretTeamInfoSynData team;// 驻守阵容

	public GroupSecretDataSynData(SecretBaseInfoSynData base, SecretTeamInfoSynData team) {
		this.base = base;
		this.team = team;
	}

	public SecretBaseInfoSynData getBase() {
		return base;
	}

	public SecretTeamInfoSynData getTeam() {
		return team;
	}
}