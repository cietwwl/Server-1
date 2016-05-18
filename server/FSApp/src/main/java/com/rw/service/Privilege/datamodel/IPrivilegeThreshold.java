package com.rw.service.Privilege.datamodel;

public interface IPrivilegeThreshold<PrivilegeNameEnum extends Enum<PrivilegeNameEnum>> {
	/**
	 * 返回-1表示不存在
	 * @param pname
	 * @return
	 */
	public int getThreshold(PrivilegeNameEnum pname);
}
