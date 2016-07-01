package com.rw.service.setting;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.PlayerSettingProtos.MsgSettingRequest;
import com.rwproto.PlayerSettingProtos.eSettingRequestType;
import com.rwproto.RequestProtos.Request;

public class SettingService implements FsService
{
	private SettingHandler handler = SettingHandler.getInstance();
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			MsgSettingRequest settingRequest = MsgSettingRequest.parseFrom(request.getBody().getSerializedContent());
			eSettingRequestType settingServiceType = settingRequest.getRequestType();
			switch (settingServiceType) 
			{
			case RENAME:
				result = handler.rename(settingRequest, player);
				break;
			case EXCHANGE_CODE:
				result = handler.exchangeCode(settingRequest, player);
				break;
			case CHANGE_HEAD:
				result = handler.changeHead(settingRequest, player);
				break;
			case CHANGE_HEADBOX:
				result = handler.changeHeadBox(settingRequest, player);
				break;
			case GET_DATA:
				result = handler.getSettingInfo(settingRequest, player);
				break;
			default:
				break;
			}
			
		} catch (InvalidProtocolBufferException e) {
						e.printStackTrace();
		}
		return result;
	}

}
