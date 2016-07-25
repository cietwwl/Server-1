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


public class FashionService implements FsService{

	private FashionHandle fashionHandler = FashionHandle.getInstance();
	
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		FashionRequest req;
		try {
			req = FashionRequest.parseFrom(request.getBody().getSerializedContent());
			FashionEventType eventType = req.getEventType();
			if (eventType != FashionEventType.getFashiondata){
				IPrivilegeManager privilegeMgr = player.getPrivilegeMgr();
				GeneralPrivilegeNames privilege = GeneralPrivilegeNames.isAllowBuyFashion;
				boolean isOpen = privilegeMgr.getBoolPrivilege(privilege);
				if (!isOpen){
					Builder resp = fashionHandler.getResponse(req);
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
				result = fashionHandler.buyFash(player,req);
				break;
			case off:
				result = fashionHandler.offFash(player,req);
				break;
			case on:
				result = fashionHandler.onFash(player,req);
				break;

			case renew:
				result = fashionHandler.renewFashion(player,req);
				break;
			case getFashiondata:
				result = fashionHandler.getFashionData(player,req);
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