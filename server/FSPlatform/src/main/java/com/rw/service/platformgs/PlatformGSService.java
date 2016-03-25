package com.rw.service.platformgs;

import com.log.PlatformLog;
import com.rw.service.ResponseService;
import com.rwproto.PlatformGSMsg.UserInfoResponse;
import com.rwproto.PlatformGSMsg.ePlatformGSMsgType;
import com.rwproto.ResponseProtos.Response;

/**
 * 平台通信游戏服务器service
 * @author lida
 *
 */
public class PlatformGSService implements ResponseService {

	private PlatformGSHandler platformGSHandler = PlatformGSHandler
			.getInstance();

	public void processResponse(Response msg) {
		// TODO Auto-generated method stub
		try {
			UserInfoResponse userInfoResponse = UserInfoResponse
					.parseFrom(msg.getSerializedContent());
			ePlatformGSMsgType type = userInfoResponse.getPlatformGSMsgType();
			switch (type) {
			case USER_INFO:
				platformGSHandler.handlerResponseUserInfo(userInfoResponse);
				break;
			default:
				break;
			}

		} catch (Exception ex) {
			PlatformLog.error("PlatformGSService", "PlatformGSService[processResponse]", "", ex);
		}
	}

}
