package com.rw.service.login.account;

import java.util.List;

import com.bm.login.ZoneBM;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.account.Account;
import com.rw.account.ZoneInfoCache;
import com.rw.platform.PlatformFactory;
import com.rw.service.RequestService;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.dao.user.accountInfo.UserZoneInfo;
import com.rwbase.dao.zone.TableZoneInfo;
import com.rwproto.AccountLoginProtos.AccountLoginRequest;
import com.rwproto.AccountLoginProtos.AccountLoginResponse;
import com.rwproto.AccountLoginProtos.UserInfo;
import com.rwproto.AccountLoginProtos.ZoneInfo;
import com.rwproto.AccountLoginProtos.eAccountLoginType;
import com.rwproto.AccountLoginProtos.eLoginResultType;
import com.rwproto.RequestProtos.Request;

/**
 * 客户端平台通信
 * @author lida
 *
 */
public class AccountLoginService implements RequestService{
	
	private static AccountLoginService instance;
	private AccountLoginHandler accountLoginHandler = AccountLoginHandler.getInstance();

	private AccountLoginService(){}
	
	public static AccountLoginService getInstance() {
		if(instance == null){
			instance = new AccountLoginService();
		}
		return instance;
	}
	
	public ByteString doTask(Request request, Account account) {
		ByteString result = null;
		try {
			AccountLoginRequest loginRequest = AccountLoginRequest.parseFrom(request.getBody().getSerializedContent());
			switch(loginRequest.getLoginType()){
			case ACCOUNT_LOGIN:
				result = accountLoginHandler.accountLogin(loginRequest, account);
				break;
			case ZONE_LIST:
				result = accountLoginHandler.getZoneList(loginRequest, account);
				break;
			case REFRESH_ZONE_STATUS:
				result = accountLoginHandler.refreshZoneInfo(loginRequest, account);
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			
		}
		return result;
	}

//	public ByteString packAccountLoginResponse(TableAccount userAccount, String accountId){
//		
//		AccountLoginResponse.Builder response = AccountLoginResponse
//				.newBuilder();
//		response.setLoginType(eAccountLoginType.ZONE_LIST);
//		List<TableZoneInfo> allZoneList = ZoneBM.getInstance().getAllZoneCfg();
//		for (TableZoneInfo zone : allZoneList) {
//			ZoneInfoCache zoneInfo = PlatformFactory.getPlatformService().getZoneInfo(zone.getZoneId());
//			response.addZoneList(getZoneInfo(zone, zoneInfo));
//		}
//		
//
//		List<UserZoneInfo> zoneList = userAccount.getUserZoneInfoList();
//		ZoneBM zoneBM = ZoneBM.getInstance();
//		UserInfo.Builder userInfo;
//		TableZoneInfo zone;
//		for (UserZoneInfo userZoneInfo : zoneList) {
//			userInfo = UserInfo.newBuilder();
//			zone = zoneBM.getTableZoneInfo(userZoneInfo.getZoneId());
//			ZoneInfoCache zoneInfo = PlatformFactory.getPlatformService().getZoneInfo(zone.getZoneId());
//			userInfo.setZoneInfo(getZoneInfo(zone, zoneInfo));
//			userInfo.setHeadImage(userZoneInfo.getHeadImage());
//			userInfo.setVipLv(userZoneInfo.getVipLevel());
//			userInfo.setCareer(userZoneInfo.getCareer());
//			userInfo.setLv(userZoneInfo.getLevel());
//			userInfo.setName(userZoneInfo.getUserName());
//			response.addUserList(userInfo);
//		}
//		response.setResultType(eLoginResultType.SUCCESS);
//		ByteString content = response.build().toByteString();
//		return content;
//		
//	}
	
//	private ZoneInfo getZoneInfo(TableZoneInfo zone, ZoneInfoCache zoneInfoCache)
//	{
//		ZoneInfo.Builder zoneInfo = ZoneInfo.newBuilder();
//		zoneInfo.setZoneId(zone.getZoneId());
//		zoneInfo.setZoneName(zone.getZoneName());
//		zoneInfo.setServerIp(zone.getServerIp());
//		zoneInfo.setPort(zone.getPort());
//		zoneInfo.setStatus(zone.getStatus());
//		zoneInfo.setRecommand(zone.getRecommand());
//		zoneInfo.setIsOpen(zoneInfoCache != null ? zoneInfoCache.getIsOpen() : zone.getIsOpen());
//		return zoneInfo.build();
//	}
}
