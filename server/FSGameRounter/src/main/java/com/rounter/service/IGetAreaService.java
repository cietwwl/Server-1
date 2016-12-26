package com.rounter.service;

import com.rounter.param.IResponseData;


public interface IGetAreaService {
	
	/**
	 * 请求服务器列表
	 * @param platformId 登录服id
	 * @param page 请求第几页
	 * @param count 每页数量
	 * @return
	 */
	IResponseData getAreasInfo(String platformId, int page, int count);
	
}
