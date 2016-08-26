package com.rw.service.fashion;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rw.service.Privilege.IPrivilegeManager;
import com.rw.service.Privilege.datamodel.generalPrivilegeProperties;
import com.rw.service.Privilege.datamodel.generalPrivilegePropertiesHelper;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.FashionServiceProtos.FashionEventType;
import com.rwproto.FashionServiceProtos.FashionRequest;
import com.rwproto.FashionServiceProtos.FashionResponse.Builder;
import com.rwproto.PrivilegeProtos.GeneralPrivilegeNames;
import com.rwproto.RequestProtos.Request;


public class FashionService implements FsService<FashionRequest, FashionEventType>{

	private FashionHandle fashionHandler = FashionHandle.getInstance();

	@Override
	public ByteString doTask(FashionRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			FashionEventType eventType = request.getEventType();
			if (eventType == FashionEventType.buy || eventType == FashionEventType.renew){
				IPrivilegeManager privilegeMgr = player.getPrivilegeMgr();
				GeneralPrivilegeNames privilege = GeneralPrivilegeNames.isAllowBuyFashion;
				boolean isOpen = privilegeMgr.getBoolPrivilege(privilege);
				if (!isOpen){
					Builder resp = fashionHandler.getResponse(request);
					resp.setError(ErrorType.NOT_ENOUGH_VIP);
					
					generalPrivilegePropertiesHelper tiphelper = generalPrivilegePropertiesHelper.getInstance();
					generalPrivilegeProperties cfg = tiphelper.getByPrivilegeName(privilege);
					String tip = cfg.getEnableTip();
					if (StringUtils.isNotBlank(tip)){
						resp.setTips(tip);
					}
					return resp.build().toByteString();
				}
			}
			switch (eventType) {
			case buy:
				result = fashionHandler.buyFash(player,request);
				break;
			case off:
				result = fashionHandler.offFash(player,request);
				break;
			case on:
				result = fashionHandler.onFash(player,request);
				break;

			case renew:
				result = fashionHandler.renewFashion(player,request);
				break;
			case getFashiondata:
				result = fashionHandler.getFashionData(player,request);
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
	public FashionRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		FashionRequest req = FashionRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public FashionEventType getMsgType(FashionRequest request) {
		// TODO Auto-generated method stub
		return request.getEventType();
	}

}