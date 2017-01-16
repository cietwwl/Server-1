package com.rounter.service;

import com.rounter.param.IRequestData;
import com.rounter.param.IResponseData;

/**
 * 9游服务接口
 * @author Alex
 *
 * 2016年12月11日 下午6:05:42
 */
public interface IUCService {

	IResponseData getRoleInfo(IRequestData request);
	
}
