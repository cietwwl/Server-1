package com.rounter.service;

import com.rounter.param.IResponseData;

public interface IGetRoleService {

	/**
	 * 获取个人在各个区的角色信息
	 * @param platformId
	 * @param accountId
	 * @return
	 */
	IResponseData getRoleInfo(String platformId, String accountId);
	
}
