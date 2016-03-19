package com.rwbase.dao.battletower.pojo.db;

import javax.persistence.Id;
import javax.persistence.Table;

import com.rwbase.dao.battletower.pojo.BattleTowerRoleInfo;
import com.rwbase.dao.battletower.pojo.readonly.TableBattleTowerRankIF;

/*
 * @author HC
 * @date 2015年9月3日 下午3:50:27
 * @Description 试练塔个人的阵容等信息
 */
@Table(name = "battle_tower_rank")
public class TableBattleTowerRank implements TableBattleTowerRankIF {
	@Id
	private String userId;// 角色Id
	private BattleTowerRoleInfo roleInfo;// 角色的信息

	public TableBattleTowerRank() {
	}

	public TableBattleTowerRank(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public BattleTowerRoleInfo getRoleInfo() {
		return roleInfo;
	}

	public void setRoleInfo(BattleTowerRoleInfo roleInfo) {
		this.roleInfo = roleInfo;
	}
}