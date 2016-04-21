package com.rwbase.dao.anglearray.pojo.db;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.team.TeamInfo;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

/*
 * @author HC
 * @date 2016年4月19日 下午8:51:41
 * @Description 
 */
@Table(name = "angel_array_team_info")
public class AngelArrayTeamInfoData implements IMapItem {
	@Id
	private String userId;// 这个记录是属于那个玩家的
	private int minFighting;// 战斗力区间下限
	private int maxFighting;// 战斗力区间上限
	private int teamGroupId = 1;// 单纯只是为了约束所有的数据都能用一个MapItemStore加载
	@CombineSave(Column = "teamInfo")
	private TeamInfo teamInfo;// 角色的阵容信息

	@Override
	public String getId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getMinFighting() {
		return minFighting;
	}

	public void setMinFighting(int minFighting) {
		this.minFighting = minFighting;
	}

	public int getMaxFighting() {
		return maxFighting;
	}

	public void setMaxFighting(int maxFighting) {
		this.maxFighting = maxFighting;
	}

	public int getTeamGroupId() {
		return teamGroupId;
	}

	public void setTeamGroupId(int teamGroupId) {
		this.teamGroupId = teamGroupId;
	}

	public TeamInfo getTeamInfo() {
		return teamInfo;
	}

	public void setTeamInfo(TeamInfo teamInfo) {
		this.teamInfo = teamInfo;
	}
}