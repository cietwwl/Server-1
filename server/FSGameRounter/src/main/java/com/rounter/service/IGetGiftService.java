package com.rounter.service;

import com.rounter.param.IResponseData;

public interface IGetGiftService {
	
	/**
	 * 请求获取礼包
	 * @param areaId
	 * @param userId
	 * @param giftId
	 * @param getDate 获取的日期
	 * @param platformID TODO
	 * @param platformId
	 * @return
	 */
	IResponseData getGift(String areaId, String userId, String giftId, String getDate, String platformID);
	
}
