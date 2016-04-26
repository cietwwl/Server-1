package Gm.Task;

import java.util.HashMap;
import java.util.Map;

import Gm.AGMHandler;
import Gm.GMManager;
import Gm.GmRequest;

public class GmChatBanPlayer extends AGMHandler{

	@Override
	public GmRequest createGmRequest() {
		// TODO Auto-generated method stub
		this.opType = 20025;
		this.account = GMManager.ACCOUNT_VALUE;
		this.password = GMManager.PASSWORD_VALUE;
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("roleId", "100100001179");
		args.put("blockReason", "你被禁言了");
		args.put("expiresTime", 1461145283);
		
		GmRequest gmRequest = new GmRequest();
		gmRequest.setOpType(opType);
		gmRequest.setAccount(account);
		gmRequest.setPassword(password);
		gmRequest.setArgs(args);
		return gmRequest;
	}

}
