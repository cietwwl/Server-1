package com.rw.netty.http.requestHandler;

import com.rw.platform.PlatformFactory;
import com.rw.service.http.platformResponse.PlatformNoticeBaseDataResponse;
import com.rw.service.http.request.ResponseObject;
import com.rwbase.dao.platformNotice.TablePlatformNotice;

public class PlatformNoticeHandler {
	public static ResponseObject updatePlatformNotice(PlatformNoticeBaseDataResponse response){
		
		TablePlatformNotice platformNotice = PlatformFactory.getPlatformService().getPlatformNotice();
		boolean insert = false;
		if(platformNotice == null){
			platformNotice = new TablePlatformNotice();
			insert = true;
		}
		platformNotice.setTitle(response.getTitle());
		platformNotice.setContent(response.getContent());
		platformNotice.setStartTime(response.getStartTime());
		platformNotice.setEndTime(response.getEndTime());
		
		PlatformFactory.getPlatformService().updatePlatformNotice(platformNotice, insert);
		ResponseObject result = new ResponseObject();
		result.setSuccess(true);
		return result;
	}
}
