package com.bm.rank.teaminfo;

import com.playerdata.team.TeamInfo;

/*
 * @author HC
 * @date 2016年4月18日 下午2:16:59
 * @Description 万仙阵要用的阵容信息的存储
 */
public class AngelArrayTeamInfoAttribute {
	private String userId;// 阵容所属的角色Id
	private long time;// 上次打竞技场的时间
	private TeamInfo teamInfo;// 具体的阵容信息

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * 获取阵容信息
	 * 
	 * @return
	 */
	public TeamInfo getTeamInfo() {
		return teamInfo;
	}

	public void setTeamInfo(TeamInfo teamInfo) {
		this.teamInfo = teamInfo;
	}
}