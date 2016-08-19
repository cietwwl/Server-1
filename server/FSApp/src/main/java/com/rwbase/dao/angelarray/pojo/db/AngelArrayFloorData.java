package com.rwbase.dao.angelarray.pojo.db;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.team.TeamInfo;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

/*
 * @author HC
 * @date 2016年4月20日 下午4:44:03
 * @Description 万仙阵的层信息，固定信息，一般是不动这里的
 */
@Table(name = "angel_array_enemy_data")
public class AngelArrayFloorData implements IMapItem {
	@Id
	private String id;// 记录的Id
	private String userId;// 属于那个角色
	private int floor;// 层数
	@CombineSave(Column = "teamInfo")
	private TeamInfo teamInfo;// 存储的层信息

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public TeamInfo getTeamInfo() {
		return teamInfo;
	}

	public void setTeamInfo(TeamInfo teamInfo) {
		this.teamInfo = teamInfo;
	}
}