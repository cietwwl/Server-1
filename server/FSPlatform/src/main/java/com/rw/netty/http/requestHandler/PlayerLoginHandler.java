package com.rw.netty.http.requestHandler;

import java.util.Map;

import com.bm.login.AccoutBM;
import com.rw.netty.http.HttpServer;
import com.rw.service.http.platformResponse.UserBaseDataResponse;
import com.rw.service.http.request.ResponseObject;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.dao.user.accountInfo.UserZoneInfo;

public class PlayerLoginHandler {
	public static ResponseObject notifyPlayerLogin(UserBaseDataResponse userBaseDataResponse){
		
		String accountId = userBaseDataResponse.getAccountId();
		String userId = userBaseDataResponse.getUserId();
		int zoneId = userBaseDataResponse.getZoneId();
		boolean result = false;
		try {

			AccoutBM accountBM = AccoutBM.getInstance();
			TableAccount userAccount = accountBM.getByAccountId(accountId);
			if (userAccount == null) {
				throw new Exception();
			}
			UserZoneInfo userZoneInfo = userAccount.getUserZoneInfoByZoneId(zoneId);
			userAccount.setLastLogin(false, zoneId);
			userAccount.setLastLoginTime(System.currentTimeMillis());
			if (userAccount.getUserZoneInfoByZoneId(zoneId) != null) {
				setUserZoneInfo(userBaseDataResponse, userZoneInfo);
			} else {
				userZoneInfo = new UserZoneInfo();
				setUserZoneInfo(userBaseDataResponse, userZoneInfo);
				userAccount.addUserZoneInfo(userZoneInfo);

			}
			int update = accountBM.update(userAccount);
			result = update == -1 ? false : true;
		} catch (Exception ex) {
			result = false;
		}
		ResponseObject response = new ResponseObject();
		response.setSuccess(result);
		
		
		return response;
	}
	
	private static void setUserZoneInfo(UserBaseDataResponse userBaseDataResponse, UserZoneInfo userZoneInfo){
		String userId = userBaseDataResponse.getUserId();
		int zoneId = userBaseDataResponse.getZoneId();
		String headImage = userBaseDataResponse.getHeadImage();
		int career = userBaseDataResponse.getCareer();
		String userName = userBaseDataResponse.getUserName();
		int level = userBaseDataResponse.getLevel();
		int vipLevel = userBaseDataResponse.getVipLevel();
		
		userZoneInfo.setZoneId(zoneId);
		userZoneInfo.setUserId(userId);
		userZoneInfo.setCareer(career);
		userZoneInfo.setHeadImage(headImage);
		userZoneInfo.setLastLoginMillis(System.currentTimeMillis());
		userZoneInfo.setLevel(level);
		userZoneInfo.setVipLevel(vipLevel);
		userZoneInfo.setUserName(userName);
	}
}
