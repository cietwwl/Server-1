package com.rounter.service;

import com.rounter.param.impl.Request9Game;
import com.rounter.param.impl.Response9Game;

/**
 * 9游服务接口
 * @author Alex
 *
 * 2016年12月11日 下午6:05:42
 */
public interface IUCService {

	Response9Game getRoleInfo(Request9Game request);
	
}
