package com.rounter.service;

import com.rounter.param.IResponseData;

public interface IGetGiftService {
	
	IResponseData getGift(String platformId, String userId, String giftId);
	
}
