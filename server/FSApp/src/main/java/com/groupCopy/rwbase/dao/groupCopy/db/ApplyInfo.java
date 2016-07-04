package com.groupCopy.rwbase.dao.groupCopy.db;

import com.playerdata.dataSyn.annotation.SynClass;


@SynClass
public class ApplyInfo {
	
	private String roleID;
	private String roleName;
	private long applyTime;//玩家申请时间

	public ApplyInfo() {
	}

	public ApplyInfo(String roleID, String roleName, long applyTime) {
		super();
		this.roleID = roleID;
		this.roleName = roleName;
		this.applyTime = applyTime;
	}

	public String getRoleID() {
		return roleID;
	}

	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public long getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(long applyTime) {
		this.applyTime = applyTime;
	}

}
