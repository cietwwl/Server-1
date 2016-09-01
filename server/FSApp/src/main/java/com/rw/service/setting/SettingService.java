package com.rw.service.setting;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.PlayerSettingProtos.MsgSettingRequest;
import com.rwproto.PlayerSettingProtos.eSettingRequestType;
import com.rwproto.RequestProtos.Request;

public class SettingService implements FsService<MsgSettingRequest, eSettingRequestType>
{
	private SettingHandler handler = SettingHandler.getInstance();
	
	@Override
	public ByteString doTask(MsgSettingRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			eSettingRequestType settingServiceType = request.getRequestType();
			switch (settingServiceType) {
			case RENAME:
				result = handler.rename(request, player);
				break;
			case EXCHANGE_CODE:
				result = handler.exchangeCode(request, player);
				break;
			case CHANGE_HEAD:
				result = handler.changeHead(request, player);
				break;
			case CHANGE_HEADBOX:
				result = handler.changeHeadBox(request, player);
				break;
			case GET_DATA:
				result = handler.getSettingInfo(request, player);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public MsgSettingRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgSettingRequest settingRequest = MsgSettingRequest.parseFrom(request.getBody().getSerializedContent());
		return settingRequest;
	}
	@Override
	public eSettingRequestType getMsgType(MsgSettingRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}

}
