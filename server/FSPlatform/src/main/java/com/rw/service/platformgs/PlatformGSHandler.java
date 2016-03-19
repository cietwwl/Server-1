package com.rw.service.platformgs;

import com.bm.login.AccoutBM;
import com.rw.account.Account;
import com.rw.platform.PlatformFactory;
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
		
		Account account = PlatformFactory.getPlatformService().getAccount(accountId);
		if(account!=null){
			
			TableAccount userAccount = account.getTableAccount();
			if(userAccount == null){
				userAccount = AccoutBM.getInstance().getByAccountId(accountId);
			}
			UserZoneInfo lastLogin = userAccount.getLastLogin(false);
			boolean blnUpdate = false;
			if(lastLogin.getLevel() != level){
				lastLogin.setLevel(level);
				blnUpdate = true;
			}
			if (lastLogin.getVipLevel() != vipLevel) {
				lastLogin.setVipLevel(vipLevel);
				blnUpdate = true;
			}
			if (!lastLogin.getHeadImage().equals(headImage)) {
				lastLogin.setHeadImage(headImage);
				blnUpdate = true;
			}
			if (lastLogin.getLastLoginMillis() != lastLoginTime) {
				lastLogin.setLastLoginMillis(lastLoginTime);
				blnUpdate = true;
			}
			if (lastLogin.getCareer() != career) {
				lastLogin.setCareer(career);
				blnUpdate = true;
			}
			if (!lastLogin.getUserName().equals(userName)) {
				lastLogin.setUserName(userName);
				blnUpdate = true;
			}
			if (blnUpdate) {
				accountBM.update(userAccount);
			}
		}
		
//		ByteString content = AccountLoginService.getInstance().packAccountLoginResponse(userAccount, accountId);
//		Account account = PlatformFactory.getPlatformService().getAccount(accountId);
//		UnprocessResquest unprocessResquest = account.getUnprocessResquest(Command.MSG_LOGIN_PLATFORM.getNumber());
//		if (unprocessResquest != null) {
//			FsNettyControler controler = SpringContextUtil
//					.getBean("fsNettyControler");
//			ChannelHandlerContext ctx = UserChannelMgr.get(accountId);
//			controler.sendResponse(unprocessResquest, content, ctx);
//		}
	}
}
