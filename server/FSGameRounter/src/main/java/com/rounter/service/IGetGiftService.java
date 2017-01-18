package com.rounter.service;

import com.rounter.param.IResponseData;

public interface IGetGiftService {
	
	/**
	 * 请求获取礼包
	 * @param platformId
	 * @param areaId
	 * @param userId
	 * @param giftId
	 * @param getDate 获取的日期
	 * @return
	 */
	IResponseData getGift(String areaId, String userId, String giftId, String getDate);
	
}
