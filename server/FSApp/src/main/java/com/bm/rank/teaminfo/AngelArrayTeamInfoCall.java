package com.bm.rank.teaminfo;

import com.playerdata.Player;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.team.TeamInfo;

/*
 * @author HC
 * @date 2016年4月18日 下午10:19:08
 * @Description 万仙阵阵容各种修改之后的Call集合
 */
public class AngelArrayTeamInfoCall {
	public static interface TeamInfoCallback {
		public void call(Player p, AngelArrayTeamInfoAttribute extendedAttribute);
	}

	/**
	 * 登陆时间修改了的Call
	 */
	public static TeamInfoCallback loginCall = new TeamInfoCallback() {

		@Override
		public void call(Player p, AngelArrayTeamInfoAttribute extendedAttribute) {
			extendedAttribute.setTime(System.currentTimeMillis());
		}
	};

	/**
	 * Vip修改了的Call
	 */
	public static TeamInfoCallback vipCall = new TeamInfoCallback() {

		@Override
		public void call(Player p, AngelArrayTeamInfoAttribute extendedAttribute) {
			TeamInfo teamInfo = extendedAttribute.getTeamInfo();
			teamInfo.setVip(p.getVip());
		}
	};

	/**
	 * 名字修改了的Call
	 */
	public static TeamInfoCallback nameCall = new TeamInfoCallback() {

		@Override
		public void call(Player p, AngelArrayTeamInfoAttribute extendedAttribute) {
			TeamInfo teamInfo = extendedAttribute.getTeamInfo();
			teamInfo.setName(p.getUserName());
		}
	};

	/**
	 * 头像修改了的Call
	 */
	public static TeamInfoCallback headIdCall = new TeamInfoCallback() {

		@Override
		public void call(Player p, AngelArrayTeamInfoAttribute extendedAttribute) {
			TeamInfo teamInfo = extendedAttribute.getTeamInfo();
			teamInfo.setHeadId(p.getHeadImage());
		}
	};

	/**
	 * 帮派名字修改了的Call
	 */
	public static TeamInfoCallback groupCall = new TeamInfoCallback() {

		@Override
		public void call(Player p, AngelArrayTeamInfoAttribute extendedAttribute) {
			TeamInfo teamInfo = extendedAttribute.getTeamInfo();
			teamInfo.setGroupName(UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(p.getUserId()).getGroupName());
		}
	};
}