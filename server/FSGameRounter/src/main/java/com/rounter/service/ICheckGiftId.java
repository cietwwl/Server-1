package com.rounter.service;

import com.rounter.param.IResponseData;


public interface ICheckGiftId {
	
	/**
	 * 检查gift参数是否合法
	 * @param platformId 登录服id
	 * @param page 请求第几页
	 * @param count 每页数量
	 * @return
	 */
	IResponseData checkGiftId(String giftId);
	
}
