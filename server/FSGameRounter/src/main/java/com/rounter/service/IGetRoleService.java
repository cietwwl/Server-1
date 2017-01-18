package com.rounter.service;

import com.rounter.param.IResponseData;

public interface IGetRoleService {

	IResponseData getRoleInfo(String platformId, String accountId);
	
}
