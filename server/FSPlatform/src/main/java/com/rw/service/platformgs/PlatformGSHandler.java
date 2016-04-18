package com.rw.service.platformgs;

import com.bm.login.AccoutBM;
import com.rw.account.Account;
import com.rw.platform.PlatformFactory;
import com.rw.service.http.platformResponse.UserBaseDataResponse;
import com.rwbase.dao.user.accountInfo.AccountLoginRecord;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.dao.user.accountInfo.UserZoneInfo;
import com.rwproto.PlatformGSMsg.UserInfoResponse;

public class PlatformGSHandler {
	private static PlatformGSHandler instance;
	
	public static PlatformGSHandler getInstance(){
		if(instance == null){
			instance = new PlatformGSHandler();
		}
		return instance;
	}
	
	public void handlerResponseUserInfo(UserInfoResponse msg) {
		String accountId = msg.getAccountId();
		int level = msg.getLevel();
		int vipLevel = msg.getVipLevel();
		long lastLoginTime = msg.getLastLoginTime();
		String headImage = msg.getHeadImage();
		int career = msg.getCareer();
		String userName = msg.getUserName();

		AccoutBM accountBM = AccoutBM.getInstance();

		Account account = PlatformFactory.getPlatformService().getAccount(
				accountId);
		if (account != null) {

			TableAccount userAccount = account.getTableAccount();
			if (userAccount == null) {
				userAccount = AccoutBM.getInstance().getByAccountId(accountId);
			}
			if (userAccount == null) {
				return;
			}
			AccountLoginRecord record = userAccount.getRecord();
			int zoneId = record.getZoneId();
			String userId = record.getUserId();

			UserZoneInfo userZoneInfo = userAccount
					.getUserZoneInfoByZoneId(zoneId);
			userAccount.setLastLogin(false, zoneId);
			userAccount.setLastLoginTime(System.currentTimeMillis());
			boolean blnUpdate = false;
			if (userAccount.getUserZoneInfoByZoneId(zoneId) != null) {
				userZoneInfo = userAccount.getUserZoneInfoByZoneId(zoneId);
				if (userZoneInfo.getLevel() != level) {
					userZoneInfo.setLevel(level);
					blnUpdate = true;
				}
				if (userZoneInfo.getVipLevel() != vipLevel) {
					userZoneInfo.setVipLevel(vipLevel);
					blnUpdate = true;
				}
				if (!userZoneInfo.getHeadImage().equals(headImage)) {
					userZoneInfo.setHeadImage(headImage);
					blnUpdate = true;
				}
				if (userZoneInfo.getLastLoginMillis() != lastLoginTime) {
					userZoneInfo.setLastLoginMillis(lastLoginTime);
					blnUpdate = true;
				}
				if (userZoneInfo.getCareer() != career) {
					userZoneInfo.setCareer(career);
					blnUpdate = true;
				}
				if (!userZoneInfo.getUserName().equals(userName)) {
					userZoneInfo.setUserName(userName);
					blnUpdate = true;
				}
			} else {
				userZoneInfo = new UserZoneInfo();
				userZoneInfo.setZoneId(zoneId);
				userZoneInfo.setUserId(userId);
				userZoneInfo.setCareer(career);
				userZoneInfo.setHeadImage(headImage);
				userZoneInfo.setLastLoginMillis(System.currentTimeMillis());
				userZoneInfo.setLevel(level);
				userZoneInfo.setVipLevel(vipLevel);
				userZoneInfo.setUserName(userName);
				userAccount.addUserZoneInfo(userZoneInfo);
				blnUpdate = true;
			}

			if (blnUpdate) {
				accountBM.update(userAccount);
			}
		}

	}
}
