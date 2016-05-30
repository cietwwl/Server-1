package com.rwbase.dao.groupsecret.syndata;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年5月30日 上午11:51:23
 * @Description 防守信息的同步
 */
@SynClass
public class DefendUserInfoSynData {
	private final int index;// 驻守点索引
	private final boolean isBeat;// 是否已经击败了<如果是true，就不用读取TeamInfo，这个只针对匹配的信息才有意义>
	private final DefendTeamInfoSynData teamInfo;// 驻守的阵容信息

	public DefendUserInfoSynData(int index, boolean isBeat, DefendTeamInfoSynData teamInfo) {
		this.index = index;
		this.isBeat = isBeat;
		this.teamInfo = teamInfo;
	}

	public int getIndex() {
		return index;
	}

	public boolean isBeat() {
		return isBeat;
	}

	public DefendTeamInfoSynData getTeamInfo() {
		return teamInfo;
	}
}